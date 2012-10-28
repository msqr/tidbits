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
 * $Id$
 * ===================================================================
 */

package magoffin.matt.tidbits.dao.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import magoffin.matt.tidbits.BaseTransactionalTest;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitDao;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitKindDao;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import org.junit.Before;
import org.junit.Test;

/**
 * FIXME
 * 
 * <p>TODO</p>
 *
 * @author matt
 * @version $Revision$ $Date$
 */
public class JpaTidbitDaoTest extends BaseTransactionalTest {

	private JpaTidbitDao dao;
	private JpaTidbitKindDao kindDao;

	private Long id;
	private TidbitKind kind;

	@Before
	public void setup() {
		dao = new JpaTidbitDao();
		dao.setEm(getEm());

		kindDao = new JpaTidbitKindDao();
		kindDao.setEm(getEm());

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
		SearchResults results = dao.getAllTidbits(null);
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
		SearchResults results = dao.getAllTidbits(null);
		assertNotNull(results);
		assertEquals(Long.valueOf(1L), results.getTotalResults());
		assertEquals(Long.valueOf(1L), results.getReturnedResults());
		assertNotNull(results.getTidbit());
		assertEquals(1, results.getTidbit().size());
		assertFalse(results.isIsPartialResult());

		assertEquals(this.id, results.getTidbit().get(0).getId());
	}

	@Test
	public void findAllMultipleResults() {
		storeEntity();
		storeEntity();
		storeEntity();
		SearchResults results = dao.getAllTidbits(null);
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

		SearchResults results = dao.getAllTidbits(pagination);
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
		results = dao.getAllTidbits(pagination);
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

}
