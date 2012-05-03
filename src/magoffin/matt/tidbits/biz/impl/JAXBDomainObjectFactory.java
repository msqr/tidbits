/* ===================================================================
 * JAXBDomainObjectFactory.java
 * 
 * Created Sep 19, 2005 3:23:22 PM
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

package magoffin.matt.tidbits.biz.impl;

import javax.xml.bind.JAXBException;

import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.domain.ObjectFactory;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.PaginationIndex;
import magoffin.matt.tidbits.domain.PaginationIndexSection;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import magoffin.matt.tidbits.domain.UiModel;
import magoffin.matt.tidbits.domain.UiSession;
import magoffin.matt.tidbits.domain.User;
import magoffin.matt.xweb.XAppContext;
import magoffin.matt.xweb.XwebParameter;

/**
 * JAXB implementation of DomainObjectFactory.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class JAXBDomainObjectFactory implements DomainObjectFactory {
	
	private static final ObjectFactory TIDBITS_OBJECT_FACTORY = new ObjectFactory();
	private static final magoffin.matt.xweb.ObjectFactory XWEB_OBJECT_FACTORY = 
		new magoffin.matt.xweb.ObjectFactory();
	
	public User newUserInstance() {
		return TIDBITS_OBJECT_FACTORY.createUser();
	}
	
	public Tidbit newTidbitInstance() {
		return TIDBITS_OBJECT_FACTORY.createTidbit();
	}

	public TidbitKind newTidbitKindInstance() {
		return TIDBITS_OBJECT_FACTORY.createTidbitKind();
	}

	public XAppContext newXAppContextInstance() {
		try {
			return XWEB_OBJECT_FACTORY.createXAppContext();
		} catch ( JAXBException e ) {
			throw new RuntimeException(e);
		}
	}

	public UiModel newModelInstance() {
		return TIDBITS_OBJECT_FACTORY.createUiModel();
	}

	public UiSession newSessionInstance() {
		return TIDBITS_OBJECT_FACTORY.createUiSession();
	}

	public PaginationCriteria newPaginationCriteriaInstance() {
		return TIDBITS_OBJECT_FACTORY.createPaginationCriteria();
	}

	public PaginationIndex newPaginationIndexInstance() {
		return TIDBITS_OBJECT_FACTORY.createPaginationIndex();
	}

	public PaginationIndexSection newPaginationIndexSectionInstance() {
		return TIDBITS_OBJECT_FACTORY.createPaginationIndexSection();
	}

	public SearchResults newSearchResultsInstance() {
		return TIDBITS_OBJECT_FACTORY.createSearchResults();
	}

	public XwebParameter newXwebParameterInstance() {
		try {
			return XWEB_OBJECT_FACTORY.createXwebParameter();
		} catch ( JAXBException e ) {
			throw new RuntimeException(e);
		}
	}

	public Object clone(Object original) {
	    if ( original == null ) {
	        return null;
	    }

		throw new IllegalArgumentException("The object [" 
				+original.getClass().getName()
		        +"] is not supported for cloneing.");
	}
	
}
