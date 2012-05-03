/* ===================================================================
 * ImportCsvForm.java
 * 
 * Created Jul 6, 2006 9:44:22 PM
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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.UiModel;
import magoffin.matt.util.TemporaryFile;
import magoffin.matt.util.TemporaryFileMultipartFileEditor;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

/**
 * Upload a CSV tidbits data file.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class ImportCsvForm extends AbstractWizardForm {
	
	@Override
	protected ModelAndView processFinish(HttpServletRequest request, 
			HttpServletResponse response, Object command, 
			BindException errors) throws Exception {
		Command cmd = (Command)command;
		getTidbitsBiz().saveTidbits(cmd.getTidbits());

		Map<String,Object> model = new LinkedHashMap<String,Object>();
		MessageSourceResolvable msg = new DefaultMessageSourceResolvable(
				new String[] {"import.tidbits.saved"}, 
				new Object[] {cmd.getTidbits().size()},
				"The tidbits have been saved.");
		model.put(getMessageObjectKey(),msg);
		
		return new ModelAndView(getSuccessView(),model);
	}

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
		
		// register our Multipart TemporaryFile binder...
		binder.registerCustomEditor(TemporaryFile.class, 
				new TemporaryFileMultipartFileEditor());
	}
	
	/*@Override
	protected void validatePage(Object command, Errors errors, int page) {
		Command cmd = (Command)command;
		if ( cmd.getFile() != null ) {
			validateImport(cmd);
		}
	}*/

	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors, int page) throws Exception {
		Command cmd = (Command)command;
		UiModel model = getDomainObjectFactory().newModelInstance();
		if ( cmd.getTidbits() != null ) {
			model.getTidbit().addAll(cmd.getTidbits());
		}
		Map<String, Object> viewModel = new LinkedHashMap<String, Object>();
		viewModel.put("ui", model);
		return viewModel;
	}

	private void validateImport(Command cmd) {
		try {
			List<Tidbit> tidbits = getTidbitsBiz().parseCsvData(
					cmd.getFile().getInputStream());
			cmd.setTidbits(tidbits);
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void onBindOnNewForm(HttpServletRequest request, Object command, BindException errors) throws Exception {
		Command cmd = (Command)command;
		if ( cmd.getFile() != null ) {
			validateImport(cmd);
		}
	}

	@Override
	protected int getInitialPage(HttpServletRequest request, Object command) {
		if ( request.getParameter("_target1") != null ) {
			return 1;
		}
		return super.getInitialPage(request, command);
	}
	
	@Override
	protected ModelAndView processCancel(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		return new ModelAndView(getCancelView());
	}

	/** Class command. */
	public static class Command {
		
		private TemporaryFile file = null;
		private List<Tidbit> tidbits = null;
		
		/**
		 * @return Returns the tidbits.
		 */
		public List<Tidbit> getTidbits() {
			return tidbits;
		}

		/**
		 * @param tidbits The tidbits to set.
		 */
		public void setTidbits(List<Tidbit> tidbits) {
			this.tidbits = tidbits;
		}

		/**
		 * @return Returns the file.
		 */
		public TemporaryFile getFile() {
			return file;
		}

		/**
		 * @param file The file to set.
		 */
		public void setFile(TemporaryFile file) {
			this.file = file;
		}
		
	}

}
