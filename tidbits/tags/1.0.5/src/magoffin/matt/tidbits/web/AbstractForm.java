/* ===================================================================
 * AbstractEatForm.java
 * 
 * Created Aug 9, 2004 1:16:48 PM
 * 
 * Copyright (c) 2004 Matt Magoffin (spamsqr@msqr.us)
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.xweb.util.XwebConstants;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.util.WebUtils;

/**
 * Abstract base class for form controllers.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public abstract class AbstractForm extends SimpleFormController {
	
	/**
	 * Parameter triggering the cancel action.
	 * Can be called from any wizard page!
	 */
	public static final String PARAM_CANCEL = "_cancel";

	private String cancelView = null;
	private DomainObjectFactory domainObjectFactory;
	private TidbitsBiz tidbitsBiz = null;
	private String modelObjectKey = XwebConstants.DEFALUT_MODEL_OBJECT;
   
	@Override
	protected void initApplicationContext() {
		super.initApplicationContext();
		String cmdName = getCommandName();
		if ( !StringUtils.hasText(cmdName) || cmdName.equals("command") ) {
			// default to own command name
			setCommandName(modelObjectKey);
		}
	}

	/**
	 * Return if cancel action is specified in the request.
	 * 
	 * <p>Default implementation looks for "_cancel" parameter in the request.</p>
	 * 
	 * @param request current HTTP request
	 * @return <em>true</em> if user canceled action
	 * @see #PARAM_CANCEL
	 */
	protected boolean isCancel(HttpServletRequest request) {
		return WebUtils.hasSubmitParameter(request, PARAM_CANCEL);
	}

	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		// check for 'cancel' request, otherwise defer to super implementation
		if ( isCancel(request) ) {
			return processCancel(request,response,command,errors);
		}
		return super.processFormSubmission(request, response, command, errors);
	}
	
	/**
	 * Perform a cancel form submit request.
	 * 
	 * <p>This method is called by {@link #processFormSubmission(HttpServletRequest, HttpServletResponse, Object, BindException)}
	 * if the {@link #isCancel(HttpServletRequest)} method returns <em>true</em>.</p>
	 * 
	 * @param request the current request
	 * @param response the response
	 * @param command the command
	 * @param errors the errors
	 * @return a ModelAndView for handling the cancel request
	 */
	protected ModelAndView processCancel(HttpServletRequest request, 
			HttpServletResponse response, Object command, BindException errors) {
		// default implementation is to simply return cancel view
		return new ModelAndView(getCancelView());
	}

	/**
	 * @return Returns the cancelView.
	 */
	public String getCancelView() {
		return cancelView;
	}

	/**
	 * @param cancelView The cancelView to set.
	 */
	public void setCancelView(String cancelView) {
		this.cancelView = cancelView;
	}

	/**
	 * @return Returns the domainObjectFactory.
	 */
	public DomainObjectFactory getDomainObjectFactory() {
		return domainObjectFactory;
	}

	/**
	 * @param domainObjectFactory The domainObjectFactory to set.
	 */
	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}

	/**
	 * @return Returns the modelObjectKey.
	 */
	public String getModelObjectKey() {
		return modelObjectKey;
	}

	/**
	 * @param modelObjectKey The modelObjectKey to set.
	 */
	public void setModelObjectKey(String modelObjectKey) {
		this.modelObjectKey = modelObjectKey;
	}

	/**
	 * @return Returns the tidbitsBiz.
	 */
	public TidbitsBiz getTidbitsBiz() {
		return tidbitsBiz;
	}

	/**
	 * @param tidbitsBiz The tidbitsBiz to set.
	 */
	public void setTidbitsBiz(TidbitsBiz tidbitsBiz) {
		this.tidbitsBiz = tidbitsBiz;
	}

}
