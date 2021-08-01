/* ===================================================================
 * PermissionGroupDao.java
 * 
 * Created 31/07/2021 12:00:46 PM
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

package magoffin.matt.tidbits.dao;

import java.util.List;
import java.util.Set;
import magoffin.matt.dao.GenericDao;
import magoffin.matt.tidbits.domain.PermissionGroup;

/**
 * DAO for {@link PermissionGroup} entities.
 *
 * @author matt
 * @version 1.0
 */
public interface PermissionGroupDao extends GenericDao<PermissionGroup, Long> {

	/**
	 * Return all available {@link PermissionGroup} objects.
	 * 
	 * @return list of PermissionGroup, or empty List if none available
	 */
	List<PermissionGroup> getAllPermissionGroups();

	/**
	 * Get a {@link PermissionGroup} by its name.
	 * 
	 * @param name
	 *        the name to find
	 * @return the group, or {@literal null}
	 */
	PermissionGroup getPermissionGroupByName(String name);

	/**
	 * Get all permission groups a person is a member of.
	 * 
	 * @param name
	 *        the name to look for
	 * @return the groups {@code name} is a member of
	 */
	Set<PermissionGroup> findAllPermissionGroupMemberships(String name);

}
