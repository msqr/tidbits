/* ===================================================================
 * JpaTidbitDaoTest.java
 * 
 * Created May 6, 2012 4:33:09 PM
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

package magoffin.matt.tidbits.dao.jpa;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import magoffin.matt.tidbits.BaseTransactionalTest;
import magoffin.matt.tidbits.dao.ExportCallback;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.Permission;
import magoffin.matt.tidbits.domain.PermissionGroup;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * Test case for the {@link JpaTidbitDaoTest} class.
 *
 * @author matt
 * @version 1.1
 */
public class JpaTidbitDaoTest extends BaseTransactionalTest {

	private JpaTidbitDao dao;
	private JpaTidbitKindDao kindDao;
	private JpaPermissionGroupDao permissionGroupDao;

	private Long id;
	private TidbitKind kind;

	@Before
	public void setup() {
		dao = new JpaTidbitDao();
		dao.setEm(getEm());

		kindDao = new JpaTidbitKindDao();
		kindDao.setEm(getEm());

		permissionGroupDao = new JpaPermissionGroupDao();
		permissionGroupDao.setEm(getEm());

		TidbitKind k = new TidbitKind();
		k.setName("name");
		k.setCreatedBy("test");
		this.kind = kindDao.get(kindDao.store(k));
	}

	@Test
	public void storeEntity() {
		Tidbit obj = new Tidbit();
		obj.setComment("comment");
		obj.setName("name");
		obj.setCreatedBy("foo");
		obj.setKind(this.kind);
		Long pk = dao.store(obj);
		flushEntityManager();
		assertNotNull("Primary key must not be null", pk);
		this.id = pk;
	}

	@Test
	public void getEntity() {
		storeEntity();
		Tidbit entity = dao.get(this.id);
		assertNotNull(entity);
		assertEquals(this.id, entity.getId());
		assertEquals("comment", entity.getComment());
		assertEquals("name", entity.getName());
		assertEquals(this.kind, entity.getKind());
		assertNotNull(entity.getCreationDate());
		assertNull(entity.getModifyDate());
	}

	@Test
	public void getMissingEntity() {
		storeEntity();
		Tidbit entity = dao.get(-123L);
		assertNull(entity);
	}

	@Test
	public void updateEntity() {
		storeEntity();
		Tidbit entity = dao.get(this.id);
		entity.setComment("comment updated");
		entity.setName("name updated");
		Long pk = dao.store(entity);
		flushEntityManager();
		assertNotNull("Primary key must not be null", pk);
		assertEquals("Primary key should not have changed (update)", this.id, pk);
		Tidbit updated = dao.get(this.id);
		assertEquals(entity.getComment(), updated.getComment());
		assertEquals(entity.getName(), updated.getName());
		assertNotNull("Modify date should be set", entity.getModifyDate());
	}

	@Test
	public void reassignKinds() {
		storeEntity();
		storeEntity();

		TidbitKind k2 = new TidbitKind();
		k2.setName("name2");
		k2.setCreatedBy("test");
		k2 = kindDao.get(kindDao.store(k2));

		int changed = dao.reassignTidbitKinds(this.kind, k2);
		assertEquals(2, changed);
		assertEquals("Tidbit should now be assigned the new kind", k2, dao.get(this.id).getKind());
	}

	@Test
	public void findAllNoResults() {
		SearchResults results = dao.getAllTidbits(null, null);
		assertNotNull(results);
		assertEquals(Long.valueOf(0L), results.getTotalResults());
		assertEquals(Long.valueOf(0L), results.getReturnedResults());
		assertNotNull(results.getTidbit());
		assertEquals(0, results.getTidbit().size());
		assertFalse(results.isIsPartialResult());
	}

	@Test
	public void findAllOneResult() {
		storeEntity();
		SearchResults results = dao.getAllTidbits(null, null);
		assertNotNull(results);
		assertEquals(Long.valueOf(1L), results.getTotalResults());
		assertEquals(Long.valueOf(1L), results.getReturnedResults());
		assertNotNull(results.getTidbit());
		assertEquals(1, results.getTidbit().size());
		assertFalse(results.isIsPartialResult());

		assertEquals(this.id, results.getTidbit().get(0).getId());
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
	public void findAllMultipleResult_membership() {
		// GIVEN
		storeEntity(); // owned by "foo"
		Tidbit obj1 = dao.get(this.id);

		// create 2nd owned by "bar"
		Tidbit obj2 = new Tidbit();
		obj2.setName("name2");
		obj2.setCreatedBy("bar");
		obj2.setKind(this.kind);
		obj2 = dao.get(dao.store(obj2));

		// create 3rd owned by "bam"
		Tidbit obj3 = new Tidbit();
		obj3.setName("name3");
		obj3.setCreatedBy("bam");
		obj3.setKind(this.kind);
		obj3 = dao.get(dao.store(obj3));

		savePermissionGroup();

		// WHEN
		SearchResults results = dao.getAllTidbits(null, "bar"); // search by "bar"

		// THEN
		assertThat("Results not null", results, is(notNullValue()));
		assertThat("Total result count", results.getTotalResults(), is(equalTo(2L)));
		assertThat("Returned result count", results.getReturnedResults(), is(equalTo(2L)));
		assertThat("Full result", results.isIsPartialResult(), is(equalTo(false)));

		List<Tidbit> list = results.getTidbit();
		assertThat("Owned and member tidbit returned", list, contains(obj2, obj1));
	}

	@Test
	public void findAllMultipleResults() {
		storeEntity();
		storeEntity();
		storeEntity();
		SearchResults results = dao.getAllTidbits(null, null);
		assertNotNull(results);
		assertEquals(Long.valueOf(3L), results.getTotalResults());
		assertEquals(Long.valueOf(3L), results.getReturnedResults());
		assertNotNull(results.getTidbit());
		assertEquals(3, results.getTidbit().size());
		assertFalse(results.isIsPartialResult());

		assertEquals(this.id, results.getTidbit().get(0).getId());
	}

	@Test
	public void findAllPaginatedResults() {
		storeEntity();
		final Long id1 = this.id;
		storeEntity();
		final Long id2 = this.id;
		storeEntity();
		final Long id3 = this.id;

		PaginationCriteria pagination = new PaginationCriteria();
		pagination.setPageOffset(0L);
		pagination.setPageSize(2L);

		SearchResults results = dao.getAllTidbits(pagination, null);
		assertNotNull(results);
		assertEquals(Long.valueOf(3L), results.getTotalResults());
		assertEquals(Long.valueOf(2L), results.getReturnedResults());
		assertTrue(results.isIsPartialResult());

		assertNotNull(results.getPagination());
		assertEquals(pagination.getPageOffset(), results.getPagination().getPageOffset());
		assertEquals(pagination.getPageSize(), results.getPagination().getPageSize());

		assertNotNull(results.getTidbit());
		assertEquals(2, results.getTidbit().size());

		assertEquals(id3, results.getTidbit().get(0).getId());
		assertEquals(id2, results.getTidbit().get(1).getId());

		pagination.setPageOffset(1L);
		results = dao.getAllTidbits(pagination, null);
		assertNotNull(results);
		assertEquals(Long.valueOf(3L), results.getTotalResults());
		assertEquals(Long.valueOf(1L), results.getReturnedResults());
		assertTrue(results.isIsPartialResult());

		assertNotNull(results.getPagination());
		assertEquals(pagination.getPageOffset(), results.getPagination().getPageOffset());
		assertEquals(pagination.getPageSize(), results.getPagination().getPageSize());

		assertNotNull(results.getTidbit());
		assertEquals(1, results.getTidbit().size());

		assertEquals(id1, results.getTidbit().get(0).getId());
	}

	@Test
	public void findAllPaginatedResults_membership() {
		storeEntity();
		final Tidbit t1 = dao.get(this.id);

		storeEntity();
		final Tidbit t2 = dao.get(this.id);
		t2.setCreatedBy("bar");
		dao.store(t2);

		storeEntity();
		final Tidbit t3 = dao.get(this.id);
		t3.setCreatedBy("bam");
		dao.store(t3);

		storeEntity();
		final Tidbit t4 = dao.get(this.id);

		savePermissionGroup();

		PaginationCriteria pagination = new PaginationCriteria();
		pagination.setPageOffset(0L);
		pagination.setPageSize(2L);

		SearchResults results = dao.getAllTidbits(pagination, "bar");

		assertThat("Results not null", results, is(notNullValue()));
		assertThat("Total result count", results.getTotalResults(), is(equalTo(3L)));
		assertThat("Returned result count", results.getReturnedResults(), is(equalTo(2L)));
		assertThat("Full result", results.isIsPartialResult(), is(equalTo(true)));
		assertThat("Pagination provided", results.getPagination(), is(notNullValue()));
		assertThat("Page offset", results.getPagination().getPageOffset(), is(equalTo(0L)));
		assertThat("Page size", results.getPagination().getPageSize(), is(equalTo(2L)));

		assertThat("Owned and member tidbit returned", results.getTidbit(), contains(t4, t2));

		pagination.setPageOffset(1L);
		results = dao.getAllTidbits(pagination, "bar");

		assertThat("Results not null", results, is(notNullValue()));
		assertThat("Total result count", results.getTotalResults(), is(equalTo(3L)));
		assertThat("Returned result count", results.getReturnedResults(), is(equalTo(1L)));
		assertThat("Full result", results.isIsPartialResult(), is(equalTo(true)));
		assertThat("Pagination provided", results.getPagination(), is(notNullValue()));
		assertThat("Page offset", results.getPagination().getPageOffset(), is(equalTo(1L)));
		assertThat("Page size", results.getPagination().getPageSize(), is(equalTo(2L)));

		assertThat("Final member tidbit returned", results.getTidbit(), contains(t1));
	}

	@Test
	public void exportAllTidbits() {
		storeEntity();
		final Long id1 = this.id;
		final List<Tidbit> tidbits = new ArrayList<Tidbit>(3);
		tidbits.add(dao.get(id1));
		storeEntity();
		final Long id2 = this.id;
		tidbits.add(dao.get(id2));
		storeEntity();
		final Long id3 = this.id;
		tidbits.add(dao.get(id3));
		final Set<Long> exportedIds = new HashSet<Long>();
		dao.exportAllTidbits(new ExportCallback() {

			@Override
			public boolean handleRow(Object[] data, int row) {
				Tidbit t = dao.get((Long) data[7]);
				assertNotNull("Tidbit should be found", t);
				assertEquals(t.getName(), data[0]);
				assertEquals(t.getKind().getName(), data[1]);
				assertEquals(t.getData(), data[2]);
				assertEquals(t.getCreationDateItem(), data[3]);
				assertEquals(t.getModifyDateItem(), data[4]);
				assertEquals(t.getComment(), data[5]);
				assertEquals(t.getKind().getComment(), data[6]);
				assertEquals(t.getCreatedBy(), data[8]);
				assertEquals(t.getKind().getId(), data[9]);

				exportedIds.add(t.getId());
				return true;
			}
		}, null);
		assertEquals(3, exportedIds.size());
		assertTrue(id1.toString(), exportedIds.contains(id1));
		assertTrue(id2.toString(), exportedIds.contains(id2));
		assertTrue(id3.toString(), exportedIds.contains(id3));
	}

	@Test
	public void exportAllTidbits_membership() {
		storeEntity();
		final Tidbit t1 = dao.get(this.id);

		storeEntity();
		final Tidbit t2 = dao.get(this.id);
		t2.setCreatedBy("bam");
		dao.store(t2);

		storeEntity();
		final Tidbit t3 = dao.get(this.id);
		t3.setCreatedBy("bar");
		dao.store(t3);

		savePermissionGroup();

		final Set<Long> exportedIds = new LinkedHashSet<>();
		dao.exportAllTidbits(new ExportCallback() {

			@Override
			public boolean handleRow(Object[] data, int row) {
				Tidbit t = dao.get((Long) data[7]);
				assertThat("Tidbit found", t, is(notNullValue()));
				assertThat("Name", data[0], is(equalTo(t.getName())));
				assertThat("Kind name", data[1], is(equalTo(t.getKind().getName())));
				assertThat("Data", data[2], is(equalTo(t.getData())));
				assertThat("Creation date", data[3], is(equalTo(t.getCreationDateItem())));
				assertThat("Modify date", data[4], is(equalTo(t.getModifyDateItem())));
				assertThat("Comment", data[5], is(equalTo(t.getComment())));
				assertThat("Kind comment", data[6], is(equalTo(t.getKind().getComment())));
				assertThat("Created by", data[8], is(equalTo(t.getCreatedBy())));
				assertThat("Kind ID", data[9], is(equalTo(t.getKind().getId())));
				exportedIds.add(t.getId());
				return true;
			}
		}, "bar");
		assertThat("Exported owned and membership items", exportedIds, contains(t1.getId(), t3.getId()));
	}

}
