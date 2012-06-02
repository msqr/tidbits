/* ===================================================================
 * AbstractWizardForm.java
 * 
 * Created Jul 6, 2006 9:44:46 PM
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

import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.xweb.util.XwebConstants;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

/**
 * Abstract base class for wizard form controllers.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public abstract class AbstractWizardForm extends AbstractWizardFormController {

	private String successView = null;
	private String cancelView = null;
	private DomainObjectFactory domainObjectFactory;
	private TidbitsBiz tidbitsBiz = null;
	private String modelObjectKey = XwebConstants.DEFALUT_MODEL_OBJECT;
	private String messageObjectKey = XwebConstants.ALERT_MESSAGES_OBJECT;

	/**
	 * Default constructor.
	 */
	public AbstractWizardForm() {
		String myClass = getClass().getName()+"$Command";
		try {
			setCommandClass(Class.forName(myClass));
		} catch ( Exception e ) {
			// ignore
		}
	}
	
	@Override
	protected void initApplicationContext() {
		super.initApplicationContext();
		String cmdName = getCommandName();
		if ( !StringUtils.hasText(cmdName) || cmdName.equals("command") ) {
			// default to own command name
			setCommandName(XwebConstants.DEFALUT_MODEL_OBJECT);
		}
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
	 * @return Returns the successView.
	 */
	public String getSuccessView() {
		return successView;
	}
	/**
	 * @param successView The successView to set.
	 */
	public void setSuccessView(String successView) {
		this.successView = successView;
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
	 * @return Returns the messageObjectKey.
	 */
	public String getMessageObjectKey() {
		return messageObjectKey;
	}

	/**
	 * @param messageObjectKey The messageObjectKey to set.
	 */
	public void setMessageObjectKey(String messageObjectKey) {
		this.messageObjectKey = messageObjectKey;
	}
	
}
