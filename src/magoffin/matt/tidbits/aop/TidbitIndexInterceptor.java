/* ===================================================================
 * TidbitIndexInterceptor.java
 * 
 * Created Jul 9, 2006 1:44:32 PM
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
import java.util.List;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.lucene.LuceneBiz;
import org.springframework.aop.AfterReturningAdvice;

/**
 * Aspect to index a Tidbit after it has changed.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class TidbitIndexInterceptor implements AfterReturningAdvice {
	
	private LuceneBiz luceneBiz;

	@Override
	@SuppressWarnings("unchecked")
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {
		if ( returnValue instanceof Tidbit ) {
			Long tidbitId = ((Tidbit) returnValue).getId();
			if ( tidbitId != null ) {
				luceneBiz.indexTidbit(tidbitId);
			}
		} else if ( returnValue instanceof List ) {
			List<Long> resultIds = (List<Long>)returnValue;
			for ( Long tidbitId : resultIds ) {
				luceneBiz.indexTidbit(tidbitId);
			}
		}
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
