/* ===================================================================
 * TidbitController.java
 * 
 * Created Jul 6, 2006 9:08:06 PM
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
import magoffin.matt.tidbits.domain.Tidbit;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Get a single Tidbit.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class TidbitController extends AbstractCommandController {

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		Command cmd = (Command)command;
		
		Model model = getDomainObjectFactory().newModelInstance();
		Tidbit tidbit = getTidbitsBiz().getTidbit(cmd.tidbitId);
		model.getTidbit().add(tidbit);
		
		Map<String, Object> viewModel = new LinkedHashMap<String, Object>();
		viewModel.put(getModelObjectKey(), model);
		return new ModelAndView(getSuccessView(), viewModel);
		
	}
	
	/** Command class. */
	public static class Command {
		private Long tidbitId;

		/**
		 * @return Returns the tidbitId.
		 */
		public Long getTidbitId() {
			return tidbitId;
		}

		/**
		 * @param tidbitId The tidbitId to set.
		 */
		public void setTidbitId(Long tidbitId) {
			this.tidbitId = tidbitId;
		}
		
	}

}