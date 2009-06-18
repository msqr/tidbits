/* ===================================================================
 * SetupFilter.java
 * 
 * Created Jul 22, 2006 10:33:29 PM
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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.xweb.XwebParameter;
import magoffin.matt.xweb.util.XwebParamDao;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Servlet filter to check for initial setup of Tidbits, and to force
 * a redirect to the setup wizard if not set up for the first time.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class SetupFilter extends GenericFilterBean {
	
	private String setupPath = "/setupWizard.do";
	private boolean setupComplete = false;
	private String keyPrefix = "";

	@Override
	public void destroy() {
		setupComplete = false;
	}

	public void doFilter(ServletRequest request, 
			ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		if ( setupComplete || httpRequest.getRequestURI().contains(setupPath) 
				|| !httpRequest.getRequestURI().endsWith(".do")) {
			chain.doFilter(request, response);
			return;
		}
		
		// redirect
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		String path = httpRequest.getScheme() +"://" +httpRequest.getServerName()
			+":" +httpRequest.getServerPort()
			+httpRequest.getContextPath() +setupPath;
		httpResponse.sendRedirect(path);
	}

	@Override
	protected void initFilterBean() throws ServletException {
		WebApplicationContext webApp = WebApplicationContextUtils.getWebApplicationContext(
				getServletContext());
		Assert.notNull(webApp, "WebApplicationContext not available");
		XwebParamDao settingDao = (XwebParamDao)BeanFactoryUtils.beanOfTypeIncludingAncestors(
				webApp, XwebParamDao.class, false, false);
		Assert.notNull(settingDao, "XwebParamDao not available");
		XwebParameter setupParam = settingDao.getParameter(
				keyPrefix+SetupWizard.SETTING_KEY_SETUP_COMPLETE);
		if ( setupParam != null ) {
			setupComplete = Boolean.parseBoolean(setupParam.getValue());
		}
	}
	
	/**
	 * @return the keyPrefix
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}
	
	/**
	 * @param keyPrefix the keyPrefix to set
	 */
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
	
	/**
	 * @return the setupComplete
	 */
	public boolean isSetupComplete() {
		return setupComplete;
	}
	
	/**
	 * @param setupComplete the setupComplete to set
	 */
	public void setSetupComplete(boolean setupComplete) {
		this.setupComplete = setupComplete;
	}
	
	/**
	 * @return the setupPath
	 */
	public String getSetupPath() {
		return setupPath;
	}
	
	/**
	 * @param setupPath the setupPath to set
	 */
	public void setSetupPath(String setupPath) {
		this.setupPath = setupPath;
	}
	
}
