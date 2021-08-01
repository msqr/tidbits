/* ===================================================================
 * SecurityInterceptorTest.java
 * 
 * Created 1/08/2021 1:42:47 PM
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

package magoffin.matt.tidbits.aop;

import static magoffin.matt.tidbits.TestSupport.becomeUser;
import static magoffin.matt.tidbits.biz.TidbitsBiz.ROLE_ADMIN;
import static magoffin.matt.tidbits.biz.TidbitsBiz.ROLE_USER;
import static org.mockito.BDDMockito.given;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import magoffin.matt.tidbits.biz.AuthorizationException;
import magoffin.matt.tidbits.dao.PermissionGroupDao;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.domain.Permission;
import magoffin.matt.tidbits.domain.PermissionGroup;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * Test cases for the {@link SecurityInterceptor} class.
 *
 * @author matt
 * @version 1.0
 */
@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class SecurityInterceptorTest {

	private static final String TEST_USER = "test.user";
	private static final String TEST_USER_2 = "other.user";

	@Mock
	private PermissionGroupDao permissionGroupDao;
	@Mock
	private TidbitDao tidbitDao;
	@Mock
	private TidbitKindDao tidbitKindDao;

	private SecurityInterceptor interceptor;

	@Before
	public void setup() {
		interceptor = new SecurityInterceptor();
		interceptor.setPermissionGroupDao(permissionGroupDao);
		interceptor.setTidbitDao(tidbitDao);
		interceptor.setTidbitKindDao(tidbitKindDao);
	}

	@Test
	public void saveTidbitKind_user_noOwner() {
		// GIVEN
		TidbitKind kind = new TidbitKind();

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbitKind(kind);

		// THEN
	}

	@Test
	public void saveTidbitKind_user_self() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setCreatedBy(TEST_USER);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbitKind(kind);

		// THEN
	}

	@Test(expected = AuthorizationException.class)
	public void saveTidbitKind_user_other_noPermissions() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setCreatedBy(TEST_USER_2);

		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(null);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbitKind(kind);

		// THEN
	}

	@Test(expected = AuthorizationException.class)
	public void saveTidbitKind_user_other_readPermission() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setCreatedBy(TEST_USER_2);

		PermissionGroup group = new PermissionGroup();
		group.setName(kind.getCreatedBy());
		Permission p = new Permission();
		p.setName(TEST_USER);
		p.setWrite(false);
		group.getPermission().add(p);
		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(group);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbitKind(kind);

		// THEN
	}

	@Test
	public void saveTidbitKind_user_other_writePermission() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setCreatedBy(TEST_USER_2);

		PermissionGroup group = new PermissionGroup();
		group.setName(kind.getCreatedBy());
		Permission p = new Permission();
		p.setName(TEST_USER);
		p.setWrite(true);
		group.getPermission().add(p);
		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(group);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbitKind(kind);

		// THEN
	}

	@Test
	public void saveTidbitKind_admin_other() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setCreatedBy(TEST_USER_2);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER, ROLE_ADMIN);
		interceptor.beforeTidbitKind(kind);

		// THEN
	}

	@Test
	public void deleteTidbitKind_user_self() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setId(1L);
		kind.setCreatedBy(TEST_USER);
		given(tidbitKindDao.get(1L)).willReturn(kind);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeDeleteTidbitKind(1L);

		// THEN
	}

	@Test(expected = AuthorizationException.class)
	public void deleteTidbitKind_user_other_noPermissions() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setId(1L);
		kind.setCreatedBy(TEST_USER_2);
		given(tidbitKindDao.get(1L)).willReturn(kind);

		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(null);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeDeleteTidbitKind(1L);

		// THEN
	}

	@Test(expected = AuthorizationException.class)
	public void deleteTidbitKind_user_other_readPermission() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setId(1L);
		kind.setCreatedBy(TEST_USER_2);
		given(tidbitKindDao.get(1L)).willReturn(kind);

		PermissionGroup group = new PermissionGroup();
		group.setName(kind.getCreatedBy());
		Permission p = new Permission();
		p.setName(TEST_USER);
		p.setWrite(false);
		group.getPermission().add(p);
		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(group);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeDeleteTidbitKind(1L);

		// THEN
	}

	@Test
	public void deleteTidbitKind_user_other_writePermission() {
		// GIVEN
		TidbitKind kind = new TidbitKind();
		kind.setId(1L);
		kind.setCreatedBy(TEST_USER_2);
		given(tidbitKindDao.get(1L)).willReturn(kind);

		PermissionGroup group = new PermissionGroup();
		group.setName(kind.getCreatedBy());
		Permission p = new Permission();
		p.setName(TEST_USER);
		p.setWrite(true);
		group.getPermission().add(p);
		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(group);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeDeleteTidbitKind(1L);

		// THEN
	}

	@Test
	public void saveTidbit_user_noOwner() {
		// GIVEN
		Tidbit t = new Tidbit();

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbit(t);

		// THEN
	}

	@Test
	public void saveTidbit_user_self() {
		// GIVEN
		Tidbit t = new Tidbit();
		t.setCreatedBy(TEST_USER);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbit(t);

		// THEN
	}

	@Test(expected = AuthorizationException.class)
	public void saveTidbit_user_other_noPermissions() {
		// GIVEN
		Tidbit t = new Tidbit();
		t.setId(1L);

		Tidbit existing = new Tidbit();
		existing.setId(t.getId());
		existing.setCreatedBy(TEST_USER_2);
		given(tidbitDao.get(t.getId())).willReturn(existing);

		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(null);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbit(t);

		// THEN
	}

	@Test(expected = AuthorizationException.class)
	public void saveTidbit_user_other_readPermission() {
		// GIVEN
		Tidbit t = new Tidbit();
		t.setId(1L);

		Tidbit existing = new Tidbit();
		existing.setId(t.getId());
		existing.setCreatedBy(TEST_USER_2);
		given(tidbitDao.get(t.getId())).willReturn(existing);

		PermissionGroup group = new PermissionGroup();
		group.setName(t.getCreatedBy());
		Permission p = new Permission();
		p.setName(TEST_USER);
		p.setWrite(false);
		group.getPermission().add(p);
		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(group);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbit(t);

		// THEN
	}

	@Test
	public void saveTidbit_user_other_writePermission() {
		// GIVEN
		Tidbit t = new Tidbit();
		t.setId(1L);

		Tidbit existing = new Tidbit();
		existing.setId(t.getId());
		existing.setCreatedBy(TEST_USER_2);
		given(tidbitDao.get(t.getId())).willReturn(existing);

		PermissionGroup group = new PermissionGroup();
		group.setName(t.getCreatedBy());
		Permission p = new Permission();
		p.setName(TEST_USER);
		p.setWrite(true);
		group.getPermission().add(p);
		given(permissionGroupDao.getPermissionGroupByName(TEST_USER_2)).willReturn(group);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER);
		interceptor.beforeTidbit(t);

		// THEN
	}

	@Test
	public void saveTidbit_admin_other() {
		// GIVEN
		Tidbit t = new Tidbit();
		t.setId(1L);

		Tidbit existing = new Tidbit();
		existing.setId(t.getId());
		existing.setCreatedBy(TEST_USER_2);
		given(tidbitDao.get(t.getId())).willReturn(existing);

		// WHEN
		becomeUser(TEST_USER, ROLE_USER, ROLE_ADMIN);
		interceptor.beforeTidbit(t);

		// THEN
	}
}
