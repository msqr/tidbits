/* ===================================================================
 * AbstractEatCommandController.java
 * 
 * Created Sep 19, 2004 4:52:17 PM
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

import org.springframework.util.StringUtils;

import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.xweb.util.MessagesSource;
import magoffin.matt.xweb.util.XwebConstants;

/**
 * Abstract base class for command controllers.
 * 
 * <p>The configurable properties of this class are:</p>
 * 
 * <dl>
 *   <dt>domainObjectFactory</dt>
 *   <dd>The {@link magoffin.matt.tidbits.biz.DomainObjectFactory} implementation
 *   to use for creating instances of our domain objects.</dd>
 *   
 *   <dt>messagesSource</dt>
 *   <dd>A {@link magoffin.matt.xweb.util.MessagesSource} instance.</dd>
 *   
 *   <dt>successView</dt>
 *   <dd>The name of the view to go to if the form is completed successfully.</dd>
 *   
 *   <dt>tidbitsBiz</dt>
 *   <dd>An implementation of {@link magoffin.matt.tidbits.biz.TidbitsBiz} to use.</dd>
 *   
 *   <dt>modelObjectKey</dt>
 *   <dd>The view model key for the model object.</dd>
 *   
 * </dl>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public abstract class AbstractCommandController 
extends org.springframework.web.servlet.mvc.AbstractCommandController {

	private String successView = null;
	private String errorView = null;
	private MessagesSource messagesSource = null;
	private DomainObjectFactory domainObjectFactory = null;
	private TidbitsBiz tidbitsBiz = null;
	private String modelObjectKey = XwebConstants.DEFALUT_MODEL_OBJECT;
	
	/**
	 * Default constructor.
	 */
	public AbstractCommandController() {
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
		if ( !StringUtils.hasText(getCommandName()) 
				|| "command".equals(getCommandName()) ) {
			setCommandName(XwebConstants.DEFALUT_MODEL_OBJECT);
		}
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
	 * @return Returns the errorView.
	 */
	public String getErrorView() {
		return errorView;
	}
	
	/**
	 * @param errorView The errorView to set.
	 */
	public void setErrorView(String errorView) {
		this.errorView = errorView;
	}
	
	/**
	 * @return Returns the messagesSource.
	 */
	public MessagesSource getMessagesSource() {
		return messagesSource;
	}
	
	/**
	 * @param messagesSource The messagesSource to set.
	 */
	public void setMessagesSource(MessagesSource messagesSource) {
		this.messagesSource = messagesSource;
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
	 * @return the modelObjectKey
	 */
	public String getModelObjectKey() {
		return modelObjectKey;
	}
	
	/**
	 * @param modelObjectKey the modelObjectKey to set
	 */
	public void setModelObjectKey(String modelObjectKey) {
		this.modelObjectKey = modelObjectKey;
	}
	
}
