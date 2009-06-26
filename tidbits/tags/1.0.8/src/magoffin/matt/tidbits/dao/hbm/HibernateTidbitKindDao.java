/* ===================================================================
 * HibernateUserDao.java
 * 
 * Created Jul 2, 2006 1:15:27 PM
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

package magoffin.matt.tidbits.dao.hbm;

import java.util.List;

import magoffin.matt.dao.hbm.GenericHibernateDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * Hibernate implementation of TidbitKindDao.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class HibernateTidbitKindDao extends GenericHibernateDao<TidbitKind,Long> 
implements TidbitKindDao {

	/** The query to find all TidbitKind objects. */
	public static final String FIND_ALL = "TidbitKindAll";
	
	/** The query to find  TidbitKind objects matching a partial name. */
	public static final String FIND_FOR_NAME = "TidbitKindsForName";
	
	/** The query to find  TidbitKind objects matching a name. */
	public static final String FIND_FOR_NAME_EXACT = "TidbitKindForName";
	
	/**
	 * Default constructor.
	 */
	public HibernateTidbitKindDao() {
		super(TidbitKind.class);
	}

	@Override
	protected Long getPrimaryKey(TidbitKind domainObject) {
		if ( domainObject == null ) return null;
		return domainObject.getKindId();
	}

	public List<TidbitKind> getAllTidbitKinds() {
		return findByNamedQuery(FIND_ALL, (Object[])null);
	}

	public List<TidbitKind> findTidbitKindsByName(String name) {
		return findByNamedQuery(FIND_FOR_NAME, new Object[]{name});
	}

	public TidbitKind getTidbitKindByName(String name) {
		List<TidbitKind> results = findByNamedQuery(FIND_FOR_NAME_EXACT, 
				new Object[]{name});
		if ( results.size() < 1 ) return null;
		return results.get(0);
	}

}
