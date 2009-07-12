/* ===================================================================
 * TidbitSearchCriteria.java
 * 
 * Created Jul 2, 2006 6:56:47 PM
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

package magoffin.matt.tidbits.biz;

import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.Tidbit;

/**
 * Search criteria API.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public interface TidbitSearchCriteria {

	/** A Tidbit search type. */
	public static enum TidbitSearchType {
		
		/** Search for Tidbits matching an index key. */
		FOR_INDEX_KEY,
		
		/** Search for Tidbits matching the Tidbit template. */
		FOR_TEMPLATE,
		
		/** Search for Tidbits matching a query string. */
		FOR_QUERY;
	}
	
	/**
	 * Get the search type.
	 * @return search type
	 */
	TidbitSearchType getSearchType();
	
	/**
	 * Get the pagination criteria.
	 * @return the pagination criteria
	 */
	PaginationCriteria getPaginationCriteria();
	
	/**
	 * Get a Tidbit instance to use as a search template.
	 * @return the Tidbit template
	 */
	Tidbit getTidbitTemplate();
	
	/**
	 * Get a query string to search by.
	 * @return the query string
	 */
	String getQuery();
	
}
