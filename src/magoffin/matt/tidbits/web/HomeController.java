/* ===================================================================
 * HomeController.java
 * 
 * Created May 8, 2012 7:04:16 PM
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
 */

package magoffin.matt.tidbits.web;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.tidbits.domain.UiModel;
import magoffin.matt.xweb.util.XwebConstants;

/**
 * Controller for main home screen.
 * 
 * @author matt
 * @version 1.0
 */
@Controller
public class HomeController {

	@Autowired
	private TidbitsBiz tidbitsBiz;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@RequestMapping(method = RequestMethod.GET, value = "/version.do")
	public String version() {
		return "version";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/home.do")
	public ModelAndView home() {
		UiModel model = new UiModel();
		return new ModelAndView("home", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/export.do")
	@ResponseBody
	public void exportTidbits(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHMM", request.getLocale());
		response.setContentType("text/cvs; charset=utf-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=tidbits_" + sdf.format(new Date()) + ".csv");
		OutputStream out = response.getOutputStream();
		tidbitsBiz.exportCsvData(out);
		out.flush();
		out.close();
	}

}
