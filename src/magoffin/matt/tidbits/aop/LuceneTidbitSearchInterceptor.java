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

import magoffin.matt.tidbits.biz.LuceneBiz;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria.TidbitSearchType;
import magoffin.matt.tidbits.domain.SearchResults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Aspect to delegate search to Lucene implementation, keeping 
 * main business API free of Lucene code.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
@Aspect
@Component
public class LuceneTidbitSearchInterceptor {
	
	@Autowired
	private LuceneBiz luceneBiz = null;

	@Pointcut("execution(magoffin.matt.tidbits.domain.SearchResults magoffin.matt.tidbits.biz.TidbitsBiz.findTidbits(..))")
	public void findTidbits() {
	}

	@Around("findTidbits() && args(searchCriteria)")
	public SearchResults findTidbits(ProceedingJoinPoint pjp, TidbitSearchCriteria searchCriteria)
			throws Throwable {
		if ( searchCriteria.getSearchType() != TidbitSearchType.FOR_QUERY ) {
			return (SearchResults) pjp.proceed();
		}
		if ( !StringUtils.hasText(searchCriteria.getQuery()) ) {
			// just do search for all
			BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(searchCriteria);
			wrapper.setPropertyValue("searchType", TidbitSearchType.FOR_TEMPLATE);
			return (SearchResults) pjp.proceed();
		}
		
		return luceneBiz.findTidbits(searchCriteria);
	}
	
	public LuceneBiz getLuceneBiz() {
		return luceneBiz;
	}
	
	public void setLuceneBiz(LuceneBiz luceneBiz) {
		this.luceneBiz = luceneBiz;
	}

}
