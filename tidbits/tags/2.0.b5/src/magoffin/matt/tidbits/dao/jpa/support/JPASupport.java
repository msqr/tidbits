/* ===================================================================
 * JPASupport.java
 * 
 * Created Jun 16, 2012 3:44:23 PM
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
 * $Id$
 * ===================================================================
 */

package magoffin.matt.tidbits.dao.jpa.support;

import java.util.Map;

/**
 * API for generic support of JPA within the application.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public interface JPASupport {

	/**
	 * Get a set of JPA driver names.
	 * 
	 * <p>
	 * For example there might be one driver per database vendor.
	 * </p>
	 * 
	 * @return set of drivers, with keys as driver names and values as labels
	 */
	Map<String, String> availableDrivers();

}
