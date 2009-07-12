/* ===================================================================
 * QuietRedirectView.java
 * 
 * Created Jul 5, 2006 6:26:03 PM
 * 
 * Copyright (c) 2006 Matt Magoffin (spamsqr@msqr.us)
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

package magoffin.matt.tidbits.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.web.servlet.view.RedirectView;

/**
 * Redirect without any Xweb parameters.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class QuietRedirectView extends RedirectView {

	@SuppressWarnings("unchecked")
	@Override
	protected Map queryProperties(Map model) {
		if ( model == null || model.size() < 1 ) return model;
		Map<String, Object> modelMap = new LinkedHashMap<String, Object>();
		for ( String key : (Set<String>)model.keySet() ) {
			if ( key.startsWith("magoffin.matt.xweb.") ) {
				continue;
			}
			modelMap.put(key, model.get(key));
		}
		return modelMap;
	}

}
