/* ===================================================================
 * TidbitsBizImplTest.java
 * 
 * Created Jul 3, 2012 6:45:56 PM
 * 
 * Copyright (c) 2012 Matt Magoffin.
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA
 * ===================================================================
 */

package magoffin.matt.tidbits.biz.impl;

import static magoffin.matt.tidbits.TestSupport.becomeUser;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import com.Ostermiller.util.CSVParser;
import magoffin.matt.tidbits.BaseTransactionalTest;
import magoffin.matt.tidbits.TestSupport;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.tidbits.dao.PermissionGroupDao;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.dao.jpa.JpaPermissionGroupDao;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitDao;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitKindDao;
import magoffin.matt.tidbits.domain.Permission;
import magoffin.matt.tidbits.domain.PermissionGroup;
import magoffin.matt.tidbits.domain.Tidbit;

/**
 * Unit test for the {@link TidbitsBizImplTest} class.
 * 
 * @author matt
 * @version 1.0
 */
public class TidbitsBizImplTest extends BaseTransactionalTest {

	private TidbitDao tidbitDao;
	private TidbitKindDao tidbitKindDao;
	private PermissionGroupDao permissionGroupDao;
	private TidbitsBizImpl biz;

	private List<Tidbit> tidbits;

	@Before
	public void setup() {
		tidbitDao = new JpaTidbitDao(getEm());
		tidbitKindDao = new JpaTidbitKindDao(getEm());
		permissionGroupDao = new JpaPermissionGroupDao(getEm());

		biz = new TidbitsBizImpl();
		biz.setDomainObjectFactory(new JAXBDomainObjectFactory());
		biz.setTidbitDao(tidbitDao);
		biz.setTidbitKindDao(tidbitKindDao);
	}

	@After
	public void teardown() {
		TestSupport.clearActor();
	}

	@Test
	public void importCsv() throws Exception {
		tidbits = biz
				.parseCsvData(new ClassPathResource("tidbits-sample.csv", getClass()).getInputStream());
		assertNotNull(tidbits);
		assertEquals("Should have parsed", 24, tidbits.size());
		for ( Tidbit t : tidbits ) {
			assertNull("Primary key should not be set", t.getId());
			assertNotNull("Kind should be available", t.getKind());
		}
	}

	@Test
	public void exportCsv() throws Exception {
		importCsv();
		for ( Tidbit t : tidbits ) {
			t.setCreatedBy("test");
			t.getKind().setCreatedBy("test");
		}
		biz.saveTidbits(tidbits);
		ByteArrayOutputStream byos = new ByteArrayOutputStream();
		biz.exportCsvData(byos);
		String csv = byos.toString("UTF-8");
		log.debug("Exported CSV data:\n{}", csv);
		CSVParser parser = new CSVParser(new StringReader(csv));
		int i = 0;
		for ( String[] line = parser.getLine(); line != null; line = parser.getLine(), i++ ) {
			assertEquals(10, line.length);
		}
		assertEquals("Exported record cound", 24, i);
	}

	@Test
	public void exportCsv_own() throws Exception {
		// GIVEN
		importCsv();
		int i = 0;
		for ( Tidbit t : tidbits ) {
			switch (i) {
				case 0:
					t.setCreatedBy("test");
					break;

				case 1:
					t.setCreatedBy("foo");
					break;

				default:
					t.setCreatedBy("bar");
					i = -1;
			}
			t.getKind().setCreatedBy("test");
			i++;
		}
		biz.saveTidbits(tidbits);

		// WHEN
		becomeUser("foo", TidbitsBiz.ROLE_USER);
		ByteArrayOutputStream byos = new ByteArrayOutputStream();
		biz.exportCsvData(byos);
		String csv = byos.toString("UTF-8");
		log.debug("Exported CSV data:\n{}", csv);

		// THEN
		CSVParser parser = new CSVParser(new StringReader(csv));
		i = 0;
		for ( String[] line = parser.getLine(); line != null; line = parser.getLine(), i++ ) {
			assertThat("CSV contains 10 columns", line, is(arrayWithSize(10)));
			assertThat("Created by", line[8], is(equalTo("foo")));
		}
		assertThat("Exported only own records", i, is(equalTo(8)));
	}

	private PermissionGroup savePermissionGroup() {
		PermissionGroup obj = new PermissionGroup();
		obj.setName("foo");
		obj.setCreatedBy("foo");

		List<Permission> perms = new ArrayList<>(3);
		Permission p = new Permission();
		p.setCreatedBy("foo");
		p.setName("bar");
		perms.add(p);
		obj.setPermission(perms);
		return permissionGroupDao.get(permissionGroupDao.store(obj));
	}

	@Test
	public void exportCsv_membership() throws Exception {
		// GIVEN
		importCsv();
		int i = 0;
		for ( Tidbit t : tidbits ) {
			switch (i) {
				case 0:
					t.setCreatedBy("test");
					break;

				case 1:
					t.setCreatedBy("foo");
					break;

				default:
					t.setCreatedBy("bar");
					i = -1;
			}
			t.getKind().setCreatedBy("test");
			i++;
		}
		biz.saveTidbits(tidbits);
		savePermissionGroup();

		// WHEN
		becomeUser("bar", TidbitsBiz.ROLE_USER);
		ByteArrayOutputStream byos = new ByteArrayOutputStream();
		biz.exportCsvData(byos);
		String csv = byos.toString("UTF-8");
		log.debug("Exported CSV data:\n{}", csv);

		// THEN
		CSVParser parser = new CSVParser(new StringReader(csv));
		i = 0;
		for ( String[] line = parser.getLine(); line != null; line = parser.getLine(), i++ ) {
			assertThat("CSV contains 10 columns", line, is(arrayWithSize(10)));
			assertThat("Created by", line[8], is(either(equalTo("foo")).or(equalTo("bar"))));
		}
		assertThat("Exported own and membership records", i, is(equalTo(16)));
	}

	@Test
	public void exportCsv_admin() throws Exception {
		// GIVEN
		importCsv();
		int i = 0;
		for ( Tidbit t : tidbits ) {
			switch (i) {
				case 0:
					t.setCreatedBy("test");
					break;

				case 1:
					t.setCreatedBy("foo");
					break;

				default:
					t.setCreatedBy("bar");
					i = -1;
			}
			t.getKind().setCreatedBy("test");
			i++;
		}
		biz.saveTidbits(tidbits);

		// WHEN
		becomeUser("admin", TidbitsBiz.ROLE_USER, TidbitsBiz.ROLE_ADMIN);
		ByteArrayOutputStream byos = new ByteArrayOutputStream();
		biz.exportCsvData(byos);
		String csv = byos.toString("UTF-8");
		log.debug("Exported CSV data:\n{}", csv);

		// THEN
		CSVParser parser = new CSVParser(new StringReader(csv));
		i = 0;
		for ( String[] line = parser.getLine(); line != null; line = parser.getLine(), i++ ) {
			assertThat("CSV contains 10 columns", line, is(arrayWithSize(10)));
		}
		assertThat("Exported all records", i, is(equalTo(24)));
	}

}
