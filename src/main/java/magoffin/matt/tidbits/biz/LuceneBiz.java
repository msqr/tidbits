/* ===================================================================
 * LuceneBiz.java
 * 
 * Created May 7, 2012 6:49:51 PM
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

package magoffin.matt.tidbits.biz;

import magoffin.matt.tidbits.domain.SearchResults;

/**
 * API for searching with Lucene.
 * 
 * @author matt
 * @version 1.0
 */
public interface LuceneBiz {

	/**
	 * Search for Tidbits.
	 * 
	 * @param tidbitCriteria
	 *        the search criteria
	 * @return the results
	 */
	public SearchResults findTidbits(final TidbitSearchCriteria tidbitCriteria);

	/**
	 * Index an individual Tidbit.
	 * 
	 * @param tidbitId
	 *        the ID of the Tidbit to index
	 */
	public void indexTidbit(Long tidbitId);

	/**
	 * Delete an individual Tidibit.
	 * 
	 * @param tidbitId
	 *        the ID of the Tidbit to remove from the index
	 */
	public void deleteTidbit(Long tidbitId);

	/**
	 * Reindex all Tidbits.
	 */
	public void reindexTidbits();

}
