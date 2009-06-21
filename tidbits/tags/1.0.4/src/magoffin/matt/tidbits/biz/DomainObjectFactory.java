/* ===================================================================
 * DomainObjectFactory.java
 * 
 * Created Sep 19, 2005 3:21:15 PM
 * 
 * Copyright (c) 2005 Matt Magoffin.
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

import magoffin.matt.tidbits.domain.Model;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.PaginationIndex;
import magoffin.matt.tidbits.domain.PaginationIndexSection;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Session;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import magoffin.matt.tidbits.domain.User;
import magoffin.matt.xweb.XAppContext;
import magoffin.matt.xweb.XwebParameter;

/**
 * Object factory interface for domain objects.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public interface DomainObjectFactory {

	/**
	 * Get a new Model instance.
	 * @return new Model instance
	 */
	Model newModelInstance();
	
	/**
	 * Get a new PaginationCriteria instance.
	 * 
	 * @return new PaginationCriteria
	 */
	PaginationCriteria newPaginationCriteriaInstance();
	
	/**
	 * Get a new PaginationIndex instance.
	 * 
	 * @return new PaginationIndex
	 */
	PaginationIndex newPaginationIndexInstance();
	
	/**
	 * Get a new PaginationIndexSection instance.
	 * @return new PaginationIndexSection instance
	 */
	PaginationIndexSection newPaginationIndexSectionInstance();
	
	/**
	 * Get a new SearchResults instance.
	 * @return search results
	 */
	SearchResults newSearchResultsInstance();
	
	/**
	 * Get a new Session instance.
	 * @return new Session instance
	 */
	Session newSessionInstance();
	
	/**
	 * Get a new Tidbit instance.
	 * @return new Tidbit instance
	 */
	Tidbit newTidbitInstance();
	
	/**
	 * Get a new TidbitKind instance.
	 * @return new TidbitKind instance
	 */
	TidbitKind newTidbitKindInstance();
	
	/**
	 * Get a new User instance.
	 * @return new User instance
	 */
	User newUserInstance();
	
	/**
	 * Get a new XAppContext instance.
	 * @return new XAppContext instance
	 */
	XAppContext newXAppContextInstance();
	
	/**
	 * Get a new XwebParameter instance.
	 * @return the XwebParameter instance
	 */
	XwebParameter newXwebParameterInstance();
	
	/**
	 * Clone a domain object.
	 * @param o the object to clone
	 * @return the cloned object
	 */
	Object clone(Object o);
	
}
