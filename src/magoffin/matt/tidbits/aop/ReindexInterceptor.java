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
 */

package magoffin.matt.tidbits.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import magoffin.matt.tidbits.biz.LuceneBiz;

/**
 * Aspect to trigger a complete re-index of Lucene.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version 1.0
 */
@Aspect
@Component
public class ReindexInterceptor {

	@Autowired
	private LuceneBiz luceneBiz;

	@Pointcut("execution(* magoffin.matt.tidbits.biz.TidbitsBiz.deleteTidbitKind(..)) && args(tidbitKindId,reassignId)")
	@AfterReturning("deleteTidbitKind(tidbitKindId, reassignId)")
	public void deleteTidbitKind(Long tidbitKindId, Long reassignId) {
		if ( tidbitKindId != null && reassignId != null ) {
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
