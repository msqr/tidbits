/* ===================================================================
 * TidbitKindsController.java
 * 
 * Created Jul 4, 2006 9:09:36 PM
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

package magoffin.matt.tidbits.web;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.tidbits.domain.Model;

import org.springframework.web.servlet.ModelAndView;

/**
 * Get all available TidbitKind objects.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class TidbitKindsController extends AbstractController {

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Model model = getDomainObjectFactory().newModelInstance();
		model.getKind().addAll(getTidbitsBiz().getAvailableTidbitKinds());
		Map<String, Object> viewModel = new LinkedHashMap<String, Object>();
		viewModel.put(getModelObjectKey(), model);
		return new ModelAndView(getSuccessView(), viewModel);
	}

}
