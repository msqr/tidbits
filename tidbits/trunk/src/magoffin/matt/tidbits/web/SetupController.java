/* ===================================================================
 * SetupController.java
 * 
 * Created Jun 5, 2012 5:08:49 PM
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

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import magoffin.matt.tidbits.dao.jpa.support.JPASupport;
import magoffin.matt.xweb.util.XwebParamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller for setup tasks.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
@Controller
@SessionAttributes("setupForm")
@RequestMapping("/setup.do")
public class SetupController {

	/** The XwebParameter key for the "setup complete" boolean flag. */
	public static final String SETTING_KEY_SETUP_COMPLETE = "app.setup.complete";

	@Autowired
	private XwebParamDao settingDao;

	@Value("${jpa.platform}")
	private String originalJpaPlatform;

	@Autowired
	private JPASupport jpaSupport;

	@Resource
	private Map<String, String> defaultSettings = new LinkedHashMap<String, String>();

	@ModelAttribute("setupForm")
	public SetupForm getFormBean() {
		SetupForm form = new SetupForm();
		// copy defaults properties into settings
		form.getSettings().putAll(this.defaultSettings);
		return form;
	}

	@ModelAttribute("jpaDrivers")
	public Map<String, String> getJpaDrivers() {
		return jpaSupport.availableDrivers();
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new SetupFormValidator());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String start() {
		return "setup-welcome";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=welcome")
	public String welcome(SetupForm form) {
		return "setup-welcome";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=db")
	public String setupDatabase(SetupForm form) {
		return "setup-db";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=filesystem")
	public String setupFilesystem(SetupForm form) {
		return "setup-filesystem";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=confirm")
	public String setupConfirm(@Validated SetupForm form, BindingResult bindingResult) {
		if ( bindingResult.hasErrors() ) {
			return "setup-filesystem";
		}
		return "setup-confirm";
	}

	public void setSettingDao(XwebParamDao settingDao) {
		this.settingDao = settingDao;
	}

	public void setOriginalJpaPlatform(String originalJpaPlatform) {
		this.originalJpaPlatform = originalJpaPlatform;
	}

	public void setDefaultSettings(Map<String, String> defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	public void setJpaSupport(JPASupport jpaSupport) {
		this.jpaSupport = jpaSupport;
	}

}
