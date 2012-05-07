/* ===================================================================
 * ReindexInterceptor.java
 * 
 * Created Jul 30, 2006 7:01:46 PM
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

import java.lang.reflect.Method;
import magoffin.matt.tidbits.biz.LuceneBiz;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Aspect to trigger a complete re-index of Lucene.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ReindexInterceptor implements AfterReturningAdvice {

	@Autowired
	private LuceneBiz luceneBiz;

	@Override
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {
		Long tidbitId = null;
		for ( Object arg : args ) {
			if ( arg instanceof Long ) {
				tidbitId = (Long)arg;
				break;
			}
		}
		if ( tidbitId != null ) {
			luceneBiz.reindexTidbits();
		}
	}
	
	public LuceneBiz getLuceneBiz() {
		return luceneBiz;
	}
	
	public void setLuceneBiz(LuceneBiz luceneBiz) {
		this.luceneBiz = luceneBiz;
	}

}
