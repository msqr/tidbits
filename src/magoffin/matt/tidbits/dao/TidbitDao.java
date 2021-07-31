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
 */

package magoffin.matt.tidbits.dao;

import magoffin.matt.dao.GenericDao;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * DAO for Tidbit domain objects.
 * 
 * @author matt.magoffin
 * @version 1.0
 */
public interface TidbitDao extends GenericDao<Tidbit, Long> {

	/**
	 * Return a SearchResults of all Tidbit objects.
	 * 
	 * @param pagination
	 *        pagination criteria
	 * @return SearchResults of Tidbit
	 */
	SearchResults getAllTidbits(PaginationCriteria pagination);

	/**
	 * Reassign all Tidbits presently using a specific TidbitKind so they are
	 * set to the reassign TidbitKind.
	 * 
	 * @param original
	 *        the original
	 * @param reassign
	 *        the reassign
	 * @return the number of tidbits affected
	 */
	int reassignTidbitKinds(TidbitKind original, TidbitKind reassign);

	/**
	 * Export all Tidbits.
	 * 
	 * <p>
	 * The returned data will be:
	 * </p>
	 * 
	 * <ol>
	 * <li>tidbit name</li>
	 * <li>tidbit kind name</li>
	 * <li>tidbit data</li>
	 * <li>tidbit create date</li>
	 * <li>tidbit modify date</li>
	 * <li>tidbit comment</li>
	 * <li>tidbit kind comment</li>
	 * <li>tidbit ID</li>
	 * <li>tidbit created by</li>
	 * <li>tidbit kind ID</li>
	 * </ol>
	 * 
	 * @param callback
	 *        the callback
	 */
	void exportAllTidbits(ExportCallback callback);
}
