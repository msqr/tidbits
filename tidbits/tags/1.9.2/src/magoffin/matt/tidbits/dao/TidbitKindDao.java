/* ===================================================================
 * TidbitDao.java
 * 
 * Created Jul 2, 2006 6:36:45 PM
 * 
 * Copyright (c) 2006 Matt Magoffin.
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

package magoffin.matt.tidbits.dao;

import java.util.List;

import magoffin.matt.dao.GenericDao;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * DAO for TidbitKind domain objects.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public interface TidbitKindDao extends GenericDao<TidbitKind,Long> {
	
	/**
	 * Return a List of all TidbitKind objects.
	 * @return list of TidbitKind, or empty List if none available
	 */
	List<TidbitKind> getAllTidbitKinds();
	
	/**
	 * Get a TidbitKind by its name.
	 * 
	 * @param name the name to find
	 * @return List of TidbitKinds
	 */
	TidbitKind getTidbitKindByName(String name);
	
	/**
	 * Return a List of TidbitKind objects with a matching name.
	 * @param name the name to find
	 * @return List of TidbitKinds
	 */
	List<TidbitKind> findTidbitKindsByName(String name);
	
}
