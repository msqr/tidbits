/* ===================================================================
 * SetupWizard.java
 * 
 * Created Aug 16, 2005 10:33:29 PM
 * 
 * Copyright (c) 2005 Matt Magoffin (spamsqr@msqr.us)
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

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.xweb.XwebParameter;
import magoffin.matt.xweb.util.XwebParamDao;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;

/**
 * Wizard for configuring Tidbits options, when running for the first time.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class SetupWizard extends AbstractWizardForm {
	
	/** The XwebParameter key for the "setup complete" boolean flag. */
	public static final String SETTING_KEY_SETUP_COMPLETE = "app.setup.complete";
	
	private XwebParamDao settingDao;
	private String originalHibernateDialect;
    private Map<String, String> defaultSettings = new LinkedHashMap<String, String>();
    
    /** Command object for SetupWizard. */
	public static class Command {
		private int page = 0;
		private Map<String, String> settings = new LinkedHashMap<String, String>();
		private boolean changedHibernateDialect = false;
		
		/**
		 * @return the changedHibernateDialect
		 */
		public boolean isChangedHibernateDialect() {
			return changedHibernateDialect;
		}
		
		/**
		 * @param changedHibernateDialect the changedHibernateDialect to set
		 */
		public void setChangedHibernateDialect(boolean changedHibernateDialect) {
			this.changedHibernateDialect = changedHibernateDialect;
		}
		
		/**
		 * @return the settings
		 */
		public Map<String, String> getSettings() {
			return settings;
		}
		
		/**
		 * @param settings the settings to set
		 */
		public void setSettings(Map<String, String> settings) {
			this.settings = settings;
		}
		
	}
	
	/** Validator class for SetupCommand. */
	public static class SetupCommandValidator implements Validator {

	    private int fileSystemPage = 3;

		public void validate(Object obj, Errors errors) {
			Command command = (Command)obj;
			
			if ( command.page == fileSystemPage ) {
				// verify index directory
				String indexPath = command.getSettings().get("lucene.index.base.path");
				File indexDir = new File(indexPath);
				if ( !(indexDir.exists() || indexDir.mkdirs()) || 
						!(indexDir.isDirectory() && indexDir.canWrite() ) ) {
					errors.rejectValue("settings", "setup.fs.index.dir.error", null, "Unable to verify index directory.");
				}
			}
		}

		/* (non-Javadoc)
		 * @see org.springframework.validation.Validator#supports(java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return Command.class.isAssignableFrom(clazz);
		}
		
		/**
		 * @return the fileSystemPage
		 */
		public int getFileSystemPage() {
			return fileSystemPage;
		}
		
		/**
		 * @param fileSystemPage the fileSystemPage to set
		 */
		public void setFileSystemPage(int fileSystemPage) {
			this.fileSystemPage = fileSystemPage;
		}
		
	}

	@Override
	protected void validatePage(Object command, Errors errors, int page) {
		Validator v = getValidator();
		Command cmd = (Command)command;
		cmd.page = page;
		v.validate(cmd, errors);
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		
		Command command = (Command) super.formBackingObject(request);
		
		// copy defaults properties into settings
		command.settings.putAll(this.defaultSettings);
		
		return command;
	}
	
	@Override
	protected int getTargetPage(HttpServletRequest request, Object command,
			Errors errors, int currentPage) {
		int targetPage = getTargetPage(request,currentPage);
		Command cmd = (Command)command;
		if ( cmd.settings.containsKey("hibernate.dialect")
				&& !originalHibernateDialect.equals(
						cmd.settings.get("hibernate.dialect")) ) {
			cmd.changedHibernateDialect = true;
		} else {
			cmd.changedHibernateDialect = false;
		}
		return targetPage;
	}

	@Override
	protected ModelAndView processFinish(HttpServletRequest request, 
			HttpServletResponse response, Object command, BindException errors) 
	throws Exception {
		Command setup = (Command)command;
		for ( Map.Entry<String, String> me : setup.getSettings().entrySet() ) {
			String key = me.getKey();
			XwebParameter setting = getDomainObjectFactory().newXwebParameterInstance();
			setting.setKey(key);
			setting.setValue(me.getValue());
			this.settingDao.updateParameter(setting);
		}
		
		
		if ( setup.changedHibernateDialect ) {
			// add "we're NOT setup" into db
			XwebParameter setting = getDomainObjectFactory().newXwebParameterInstance();
			setting.setKey(SETTING_KEY_SETUP_COMPLETE);
			setting.setValue(Boolean.FALSE.toString());
			this.settingDao.updateParameter(setting);
		} else {
			// add the "we're setup" key into the database
			XwebParameter setting = getDomainObjectFactory().newXwebParameterInstance();
			setting.setKey(SETTING_KEY_SETUP_COMPLETE);
			setting.setValue(Boolean.TRUE.toString());
			this.settingDao.updateParameter(setting);
		}
		
		Map<String, Object> model = new LinkedHashMap<String, Object>();
		model.put(getCommandName(),setup);
		return new ModelAndView(getSuccessView(),model);
	}

	/**
	 * @return the defaultSettings
	 */
	public Map<String, String> getDefaultSettings() {
		return defaultSettings;
	}
	
	/**
	 * @param defaultSettings the defaultSettings to set
	 */
	public void setDefaultSettings(Map<String, String> defaultSettings) {
		this.defaultSettings = defaultSettings;
	}
	
	/**
	 * @return the originalHibernateDialect
	 */
	public String getOriginalHibernateDialect() {
		return originalHibernateDialect;
	}
	
	/**
	 * @param originalHibernateDialect the originalHibernateDialect to set
	 */
	public void setOriginalHibernateDialect(String originalHibernateDialect) {
		this.originalHibernateDialect = originalHibernateDialect;
	}
	
	/**
	 * @return the settingDao
	 */
	public XwebParamDao getSettingDao() {
		return settingDao;
	}
	
	/**
	 * @param settingDao the settingDao to set
	 */
	public void setSettingDao(XwebParamDao settingDao) {
		this.settingDao = settingDao;
	}
	
}
