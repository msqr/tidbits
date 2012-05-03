/* ===================================================================
 * AddTidbitForm.java
 * 
 * Created Jul 3, 2006 10:08:24 PM
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

import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.UiModel;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Add a new Tidbit.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class AddTidbitForm extends AbstractForm {

	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		UiModel model = getDomainObjectFactory().newModelInstance();
		model.getKind().addAll(getTidbitsBiz().getAvailableTidbitKinds());
		Map<String,Object> viewModel = new LinkedHashMap<String,Object>();
		viewModel.put(getModelObjectKey(),model);
		return viewModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command, 
			BindException errors) throws Exception {
		Command cmd = (Command)command;
		Tidbit saved = getTidbitsBiz().saveTidbit(cmd.tidbit);
		UiModel model = getDomainObjectFactory().newModelInstance();
		model.getTidbit().add(saved);
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

		private Tidbit tidbit;
		
		/**
		 * Default constructor.
		 */
		public Command() {
			tidbit = getDomainObjectFactory().newTidbitInstance();
			tidbit.setKind(getDomainObjectFactory().newTidbitKindInstance());
		}

		/**
		 * @return Returns the tidbit.
		 */
		public Tidbit getTidbit() {
			return tidbit;
		}

		/**
		 * @param tidbit The tidbit to set.
		 */
		public void setTidbit(Tidbit tidbit) {
			this.tidbit = tidbit;
		}
		
	}

}
