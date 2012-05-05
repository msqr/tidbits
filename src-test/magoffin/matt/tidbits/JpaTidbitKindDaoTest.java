/* ===================================================================
 * JpaTidbitKindDaoTest.java
 * 
 * Created May 4, 2012 5:16:27 PM
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

package magoffin.matt.tidbits;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.List;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitKindDao;
import magoffin.matt.tidbits.domain.TidbitKind;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link JpaTidbitKindDao} class.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public class JpaTidbitKindDaoTest extends BaseTransactionalTest {

	private JpaTidbitKindDao dao;

	private Long id;

	@Before
	public void setup() {
		dao = new JpaTidbitKindDao();
		dao.setEm(getEm());
	}

	@Test
	public void storeEntity() {
		TidbitKind obj = new TidbitKind();
		obj.setComment("comment");
		obj.setName("name");
		obj.setCreatedBy("foo");
		Long pk = dao.store(obj);
		flushEntityManager();
		assertNotNull("Primary key must not be null", pk);
		this.id = pk;
	}

	@Test
	public void getEntity() {
		storeEntity();
		TidbitKind entity = dao.get(this.id);
		assertNotNull(entity);
		assertEquals(this.id, entity.getKindId());
		assertEquals("comment", entity.getComment());
		assertEquals("name", entity.getName());
		assertNotNull(entity.getCreationDate());
		assertNull(entity.getModifyDate());
	}

	@Test
	public void getMissingEntity() {
		storeEntity();
		TidbitKind entity = dao.get(-123L);
		assertNull(entity);
	}

	@Test
	public void updateEntity() {
		storeEntity();
		TidbitKind entity = dao.get(this.id);
		entity.setComment("comment updated");
		entity.setName("name updated");
		Long pk = dao.store(entity);
		assertNotNull("Primary key must not be null", pk);
		assertEquals("Primary key should not have changed (update)", this.id, pk);
		TidbitKind updated = dao.get(this.id);
		assertEquals(entity.getComment(), updated.getComment());
		assertEquals(entity.getName(), updated.getName());
		assertNotNull("Modify date should be set", entity.getModifyDate());
	}

	@Test
	public void findAll() {
		storeEntity();
		List<TidbitKind> list = dao.getAllTidbitKinds();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(this.id, list.get(0).getKindId());
	}

	@Test
	public void findByName() {
		storeEntity();
		List<TidbitKind> list = dao.findTidbitKindsByName("name");
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(this.id, list.get(0).getKindId());
	}

	@Test
	public void findByNameNoMatch() {
		storeEntity();
		List<TidbitKind> list = dao.findTidbitKindsByName("no match");
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void findByNameMultiMatch() {
		storeEntity();
		storeEntity();
		List<TidbitKind> list = dao.findTidbitKindsByName("name");
		assertNotNull(list);
		assertEquals(2, list.size());
	}
}
