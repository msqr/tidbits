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
 */

package magoffin.matt.tidbits.web;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
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
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.dao.jpa.support.JPASupport;
import magoffin.matt.xweb.XwebParameter;
import magoffin.matt.xweb.util.XwebParamDao;

/**
 * Controller for setup tasks.
 * 
 * @author matt
 * @version 1.0
 */
@Controller
@SessionAttributes("setupForm")
@RequestMapping("/setup.do")
public class SetupController {

	/** The XwebParameter key for the "setup complete" boolean flag. */
	public static final String SETTING_KEY_SETUP_COMPLETE = "app.setup.complete";

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@Autowired
	private XwebParamDao settingDao;

	@Value("${jpa.platform}")
	private String originalJpaPlatform;

	@Autowired
	private JPASupport jpaSupport;

	@Resource
	private Map<String, String> env = new LinkedHashMap<String, String>();

	@ModelAttribute("setupForm")
	public SetupForm getFormBean() {
		SetupForm form = new SetupForm();
		// copy defaults properties into settings
		form.getSettings().putAll(this.env);
		return form;
	}

	@ModelAttribute("jpaDrivers")
	public Map<String, String> getJpaDrivers() {
		return jpaSupport.availableDrivers();
	}

	@InitBinder("setupForm")
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new SetupFormValidator());
	}

	@RequestMapping(method = RequestMethod.GET)
	public String start() {
		return "setup-welcome";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=welcome")
	public String welcome(@SuppressWarnings("unused") @ModelAttribute("setupForm") SetupForm form) {
		return "setup-welcome";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=db")
	public String setupDatabase(
			@SuppressWarnings("unused") @ModelAttribute("setupForm") SetupForm form) {
		return "setup-db";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=filesystem")
	public String setupFilesystem(@ModelAttribute("setupForm") SetupForm form) {
		if ( form.getSettings().containsKey("jpa.platform")
				&& !originalJpaPlatform.equals(form.getSettings().get("jpa.platform")) ) {
			form.setChangedJpaPlatform(true);
		} else {
			form.setChangedJpaPlatform(false);
		}
		return "setup-filesystem";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=confirm")
	public String setupConfirm(
			@SuppressWarnings("unused") @ModelAttribute("setupForm") @Validated SetupForm form,
			BindingResult bindingResult) {
		if ( bindingResult.hasErrors() ) {
			return "setup-filesystem";
		}
		return "setup-confirm";
	}

	@RequestMapping(method = RequestMethod.POST, params = "_to=complete")
	public String setupComplete(SetupForm form) {
		for ( Map.Entry<String, String> me : form.getSettings().entrySet() ) {
			String key = me.getKey();
			if ( !env.containsKey(key) || !me.getValue().equals(env.get(key)) ) {
				XwebParameter setting = domainObjectFactory.newXwebParameterInstance();
				setting.setKey(key);
				setting.setValue(me.getValue());
				this.settingDao.updateParameter(setting);
			} else {
				this.settingDao.removeParameter(key);
			}
		}

		// add the "we're setup" key into the database
		XwebParameter setting = domainObjectFactory.newXwebParameterInstance();
		setting.setKey(SETTING_KEY_SETUP_COMPLETE);
		setting.setValue(Boolean.TRUE.toString());
		this.settingDao.updateParameter(setting);

		return "setup-complete";
	}

	public void setSettingDao(XwebParamDao settingDao) {
		this.settingDao = settingDao;
	}

	public void setOriginalJpaPlatform(String originalJpaPlatform) {
		this.originalJpaPlatform = originalJpaPlatform;
	}

	public void setEnv(Map<String, String> defaultSettings) {
		this.env = defaultSettings;
	}

	public void setJpaSupport(JPASupport jpaSupport) {
		this.jpaSupport = jpaSupport;
	}

	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}

}
