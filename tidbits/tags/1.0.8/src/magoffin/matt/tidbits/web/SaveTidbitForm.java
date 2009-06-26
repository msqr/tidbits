/* ===================================================================
 * SaveTidbitForm.java
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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.tidbits.domain.Model;
import magoffin.matt.tidbits.domain.Tidbit;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

/**
 * Save a Tidbit.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class SaveTidbitForm extends AddTidbitForm {
	
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		// get the Tidbit ID to edit
		Command cmd = new Command();
		ServletRequestDataBinder binder = createBinder(request, cmd);
		binder.bind(request);
		
		Long id = cmd.getTidbit().getTidbitId();
		Tidbit tidbit = getTidbitsBiz().getTidbit(id);
		cmd.setTidbit(tidbit);
		return cmd;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		Map<String, Object> viewModel = super.referenceData(request);
		Model model = (Model)viewModel.get(getModelObjectKey());
		Command cmd = (Command)command;
		model.getTidbit().add(cmd.getTidbit());
		return viewModel;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command, BindException errors) throws Exception {
		Command cmd = (Command)command;
		if ( cmd.delete ) {
			getTidbitsBiz().deleteTidbit(cmd.getTidbit().getTidbitId());
			return new ModelAndView(getSuccessView());
		}
		return super.onSubmit(request, response, command, errors);
	}
	
	/** Class command. */
	public class Command extends AddTidbitForm.Command {
		
		private boolean delete = false;
		
		/**
		 * @return the delete
		 */
		public boolean isDelete() {
			return delete;
		}
		
		/**
		 * @param delete the delete to set
		 */
		public void setDelete(boolean delete) {
			this.delete = delete;
		}
		
	}
	
}
