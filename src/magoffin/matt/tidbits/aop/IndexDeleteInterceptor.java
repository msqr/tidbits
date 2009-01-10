/* ===================================================================
 * IndexDeleteInterceptor.java
 * 
 * Created Jul 30, 2006 3:48:47 PM
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

import magoffin.matt.tidbits.lucene.IndexType;
import magoffin.matt.tidbits.lucene.LuceneBiz;

import org.springframework.aop.AfterReturningAdvice;

/**
 * Aspect to remove a Tidbit from the Lucene index after it has changed.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class IndexDeleteInterceptor implements AfterReturningAdvice {

	private LuceneBiz luceneBiz;
	private String indexType = IndexType.TIDBIT.toString();

	@SuppressWarnings("unchecked")
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
			luceneBiz.getLucene().deleteObjectById(
					this.indexType, tidbitId);
		}
	}
	
	/**
	 * @return the indexType
	 */
	public String getIndexType() {
		return indexType;
	}
	
	/**
	 * @param indexType the indexType to set
	 */
	public void setIndexType(String indexType) {
		this.indexType = indexType;
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
