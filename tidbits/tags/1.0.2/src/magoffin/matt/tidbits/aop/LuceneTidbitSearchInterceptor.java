/* ===================================================================
 * LuceneTidbitSearchInterceptor.java
 * 
 * Created Jul 9, 2006 1:45:38 PM
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

package magoffin.matt.tidbits.aop;

import magoffin.matt.tidbits.biz.TidbitSearchCriteria;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria.TidbitSearchType;
import magoffin.matt.tidbits.lucene.LuceneBiz;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

/**
 * Aspect to delegate search to Lucene implementation, keeping 
 * main business API free of Lucene code.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class LuceneTidbitSearchInterceptor implements MethodInterceptor {
	
	private LuceneBiz luceneBiz = null;

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation method) throws Throwable {
		Object[] args = method.getArguments();
		if ( args == null || args.length < 1 || !(args[0] instanceof TidbitSearchCriteria) ) {
			return method.proceed();
		}
		
		TidbitSearchCriteria tidbitCriteria = (TidbitSearchCriteria)args[0];
		if ( tidbitCriteria.getSearchType() != TidbitSearchType.FOR_QUERY ) {
			return method.proceed();
		}
		if ( !StringUtils.hasText(tidbitCriteria.getQuery()) ) {
			// just do search for all
			BeanWrapper wrapper = new BeanWrapperImpl(tidbitCriteria);
			wrapper.setPropertyValue("searchType", TidbitSearchType.FOR_TEMPLATE);
			return method.proceed();
		}
		
		return luceneBiz.findTidbits(tidbitCriteria);
	}
	
	/**
	 * @return the luceneBiz
	 */
	public LuceneBiz getLuceneBiz() {
		return luceneBiz;
	}
	
	/**
	 * @param luceneBiz the luceneBiz to set
	 */
	public void setLuceneBiz(LuceneBiz luceneBiz) {
		this.luceneBiz = luceneBiz;
	}

}
