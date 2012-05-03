/* ===================================================================
 * SaveTidbitKindForm.java
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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.tidbits.domain.TidbitKind;
import magoffin.matt.tidbits.domain.UiModel;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

/**
 * Add a new TidbitKind.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class SaveTidbitKindForm extends AddTidbitKindForm {

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		// get the Tidbit ID to edit
		Command cmd = new Command();
		ServletRequestDataBinder binder = createBinder(request, cmd);
		binder.bind(request);
		
		Long id = cmd.getKind().getKindId();
		TidbitKind kind = getTidbitsBiz().getTidbitKind(id);
		cmd.setKind(kind);
		return cmd;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		Map<String, Object> viewModel = super.referenceData(request);
		UiModel model = (UiModel)viewModel.get(getModelObjectKey());
		Command cmd = (Command)command;
		model.getKind().add(cmd.getKind());
		return viewModel;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command, BindException errors) throws Exception {
		Command cmd = (Command)command;
		if ( cmd.delete ) {
			getTidbitsBiz().deleteTidbitKind(cmd.getKind().getKindId(), cmd.getReassign());
			return new ModelAndView(getSuccessView());
		}
		return super.onSubmit(request, response, command, errors);
	}
	
	/** Class command. */
	public class Command extends AddTidbitKindForm.Command {
		
		private boolean delete = false;
		private Long reassign = null;
		
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
		
		/**
		 * @return the reassign
		 */
		public Long getReassign() {
			return reassign;
		}
		
		/**
		 * @param reassign the reassign to set
		 */
		public void setReassign(Long reassign) {
			this.reassign = reassign;
		}
		
	}
	
}
