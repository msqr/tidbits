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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

	@Test
	public void findByName() {
		storeEntity();
		PermissionGroup entity = dao.get(this.id);

		PermissionGroup group = dao.getPermissionGroupByName(entity.getName());
		assertThat("Group found by name", group, is(equalTo(entity)));

		group = dao.getPermissionGroupByName("not.a.name");
		assertThat("Group not found by name", group, is(nullValue()));
	}

	@Test
	public void findByMembership() {
		storeEntity();
		PermissionGroup entity = dao.get(this.id);

		Set<PermissionGroup> groups = dao.findAllPermissionGroupMemberships("user1");
		assertThat("Group found by membership", groups, containsInAnyOrder(entity));

		groups = dao.findAllPermissionGroupMemberships("userX");
		assertThat("Group not found by membership", groups, hasSize(0));
	}

	@Test
	public void findByMembership_multi() {
		storeEntity();
		PermissionGroup entity1 = dao.get(this.id);

		PermissionGroup obj = new PermissionGroup();
		obj.setName("name2");
		obj.setCreatedBy("foo");

		List<Permission> perms = new ArrayList<>(3);
		Permission p = new Permission();
		p.setCreatedBy("foo");
		p.setName("user1");
		perms.add(p);
		obj.setPermission(perms);

		Long pk = dao.store(obj);
		flushEntityManager();

		PermissionGroup entity2 = dao.get(pk);

		Set<PermissionGroup> groups = dao.findAllPermissionGroupMemberships("user1");
		assertThat("Groups found by membership", groups, containsInAnyOrder(entity1, entity2));

		groups = dao.findAllPermissionGroupMemberships("user0");
		assertThat("Group found by membership", groups, containsInAnyOrder(entity1));

		groups = dao.findAllPermissionGroupMemberships("userX");
		assertThat("Group not found by membership", groups, hasSize(0));
	}

}
