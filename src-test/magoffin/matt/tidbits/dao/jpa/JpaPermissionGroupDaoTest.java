/* ===================================================================
 * JpaPermissionGroupDaoTest.java
 * 
 * Created 31/07/2021 12:44:54 PM
 * 
 * Copyright (c) 2021 Matt Magoffin.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import magoffin.matt.tidbits.BaseTransactionalTest;
import magoffin.matt.tidbits.domain.Permission;
import magoffin.matt.tidbits.domain.PermissionGroup;

/**
 * Test case for the {@link JpaPermissionGroupDao} class.
 *
 * @author matt
 * @version 1.0
 */
public class JpaPermissionGroupDaoTest extends BaseTransactionalTest {

	private JpaPermissionGroupDao dao;

	private Long id;

	@Before
	public void setup() {
		dao = new JpaPermissionGroupDao();
		dao.setEm(getEm());
	}

	@Test
	public void storeEntity() {
		PermissionGroup obj = new PermissionGroup();
		obj.setName("name");
		obj.setCreatedBy("foo");

		List<Permission> perms = new ArrayList<>(3);
		for ( int i = 0; i < 3; i++ ) {
			Permission p = new Permission();
			p.setCreatedBy("foo");
			p.setName("user" + i);
			if ( i % 2 == 1 ) {
				p.setWrite(true);
			}
			perms.add(p);
		}
		obj.setPermission(perms);

		Long pk = dao.store(obj);
		flushEntityManager();
		assertNotNull("Primary key must not be null", pk);
		this.id = pk;
	}

	@Test
	public void getEntity() {
		storeEntity();
		PermissionGroup entity = dao.get(this.id);
		assertNotNull(entity);
		assertEquals(this.id, entity.getId());
		assertEquals("name", entity.getName());
		assertEquals("foo", entity.getCreatedBy());
		assertNotNull(entity.getCreationDate());
		assertNull(entity.getModifyDate());

		List<Permission> perms = entity.getPermission();
		assertNotNull(perms);
		assertEquals(3, perms.size());
		for ( int i = 0; i < 3; i++ ) {
			Permission p = perms.get(i);
			assertEquals("foo", p.getCreatedBy());
			assertNotNull(entity.getCreationDate());
			assertNotNull(p.getId());
			assertEquals("user" + i, p.getName());
			assertEquals(i % 2 == 1 ? true : false, p.isWrite());
		}
	}

	@Test
	public void getMissingEntity() {
		storeEntity();
		PermissionGroup entity = dao.get(-123L);
		assertNull(entity);
	}

}
