/* ===================================================================
 * BasicTidbitSearchCriteria.java
 * 
 * Created Jul 4, 2006 10:45:50 PM
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

package magoffin.matt.tidbits.support;

import magoffin.matt.tidbits.biz.TidbitSearchCriteria;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.Tidbit;

/**
 * Basic implementation of TidbitSearchCriteria.
 * 
 * @author matt.magoffin
 * @version 1.0
 */
public class BasicTidbitSearchCriteria implements TidbitSearchCriteria {

	private TidbitSearchType searchType;
	private PaginationCriteria paginationCriteria;
	private Tidbit tidbitTemplate;
	private String query;

	/**
	 * Default constructor.
	 */
	public BasicTidbitSearchCriteria() {
		this(TidbitSearchType.FOR_TEMPLATE, null, null);
	}

	/**
	 * Construct with parameters.
	 * 
	 * @param searchType
	 *        the search type
	 * @param paginationCriteria
	 *        the pagination criteria
	 * @param tidbitTemplate
	 *        the template
	 */
	public BasicTidbitSearchCriteria(TidbitSearchType searchType, PaginationCriteria paginationCriteria,
			Tidbit tidbitTemplate) {
		this.searchType = searchType;
		this.paginationCriteria = paginationCriteria;
		this.tidbitTemplate = tidbitTemplate;
	}

	@Override
	public TidbitSearchType getSearchType() {
		return searchType;
	}

	@Override
	public PaginationCriteria getPaginationCriteria() {
		return paginationCriteria;
	}

	@Override
	public Tidbit getTidbitTemplate() {
		return tidbitTemplate;
	}

	@Override
	public String getQuery() {
		return query;
	}

	/**
	 * @param paginationCriteria
	 *        The paginationCriteria to set.
	 */
	public void setPaginationCriteria(PaginationCriteria paginationCriteria) {
		this.paginationCriteria = paginationCriteria;
	}

	/**
	 * @param searchType
	 *        The searchType to set.
	 */
	public void setSearchType(TidbitSearchType searchType) {
		this.searchType = searchType;
	}

	/**
	 * @param tidbitTemplate
	 *        The tidbitTemplate to set.
	 */
	public void setTidbitTemplate(Tidbit tidbitTemplate) {
		this.tidbitTemplate = tidbitTemplate;
	}

	/**
	 * @param query
	 *        the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

}
