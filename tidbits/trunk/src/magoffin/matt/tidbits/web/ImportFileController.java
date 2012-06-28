/* ===================================================================
 * ImportFileController.java
 * 
 * Created Jun 28, 2012 2:08:31 PM
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

package magoffin.matt.tidbits.web;

import java.io.IOException;
import java.util.List;
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.UiModel;
import magoffin.matt.util.TemporaryFile;
import magoffin.matt.util.TemporaryFileMultipartFileEditor;
import magoffin.matt.xweb.util.XwebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for importing file data.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
@Controller
@SessionAttributes("importForm")
@RequestMapping("/import.do")
public class ImportFileController {

	@Autowired
	private TidbitsBiz tidbitsBiz;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@ModelAttribute("importForm")
	public ImportFileForm getFormBean() {
		return new ImportFileForm();
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// register our Multipart TemporaryFile binder and validator
		binder.registerCustomEditor(TemporaryFile.class, new TemporaryFileMultipartFileEditor());
		binder.setValidator(new ImportFileValidator());
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=verify")
	public ModelAndView start(ImportFileForm form) throws IOException {
		List<Tidbit> tidbits = tidbitsBiz.parseCsvData(form.getFile().getInputStream());
		form.setTidbits(tidbits);

		UiModel model = new UiModel();
		model.setTidbit(tidbits);

		return new ModelAndView("json-search-results", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
	}

	public void setTidbitsBiz(TidbitsBiz tidbitsBiz) {
		this.tidbitsBiz = tidbitsBiz;
	}

	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}

}
