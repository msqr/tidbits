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

import magoffin.matt.tidbits.biz.LuceneBiz;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Aspect to remove a Tidbit from the Lucene index after it has changed.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
@Aspect
@Component
public class IndexDeleteInterceptor {

	@Autowired
	private LuceneBiz luceneBiz;

	/**
	 * Match TidbitsBiz methods deleting Tidbit.
	 * 
	 * @param tidbitId
	 *        the ID of the tidbit being deleted
	 */
	@Pointcut("execution(* magoffin.matt.tidbits.biz.TidbitsBiz.deleteTidbit(..)) && args(tidbitId)")
	@AfterReturning("deleteTidbit(tidbitId)")
	public void deleteTidbit(Long tidbitId) {
		if ( tidbitId != null ) {
			luceneBiz.deleteTidbit(tidbitId);
		}
	}

	public LuceneBiz getLuceneBiz() {
		return luceneBiz;
	}
	
	public void setLuceneBiz(LuceneBiz luceneBiz) {
		this.luceneBiz = luceneBiz;
	}

}
