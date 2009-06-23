/* ===================================================================
 * AddTidbitKindForm.java
 * 
 * Created Jul 3, 2006 10:10:33 PM
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
import magoffin.matt.tidbits.domain.TidbitKind;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Add a new TidbitKind.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class AddTidbitKindForm extends AbstractForm {

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command, 
			BindException errors) throws Exception {
		Command cmd = (Command)command;
		TidbitKind saved = getTidbitsBiz().saveTidbitKind(cmd.kind);
		Model model = getDomainObjectFactory().newModelInstance();
		model.getKind().add(saved);
		Map<String,Object> viewModel = new LinkedHashMap<String,Object>();
		viewModel.put(getModelObjectKey(),model);
		return new ModelAndView(getSuccessView(),viewModel);
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return new Command();
	}

	/** Command class. */
	public class Command {
		
		private TidbitKind kind = getDomainObjectFactory().newTidbitKindInstance();

		/**
		 * @return Returns the kind.
		 */
		public TidbitKind getKind() {
			return kind;
		}

		/**
		 * @param kind The kind to set.
		 */
		public void setKind(TidbitKind kind) {
			this.kind = kind;
		}
		
	}

}
