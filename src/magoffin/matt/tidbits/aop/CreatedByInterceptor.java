/* ===================================================================
 * CreatedByInterceptor.java
 * 
 * Created Jul 10, 2006 11:40:53 AM
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

import java.util.Collection;
import java.util.List;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect to inject the "createdBy" property into objects as they are created.
 * 
 * <p>
 * This interceptor will apply the current user's username, if available, to all
 * Tidbit and TidbitKind objects found in the method arguments. If a method
 * argument is a {@link Collection}, then the objects in the Collection are
 * inspected, too.
 * </p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date: 2012-05-07 16:06:13 +1200 (Mon, 07 May 2012)
 *          $
 */
@Aspect
@Component
public class CreatedByInterceptor {
	
	@Before("magoffin.matt.tidbits.aop.Business.saveTidbit(tidbit)")
	public void beforeTidbit(Tidbit tidbit) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ( currentUser != null && tidbit != null && tidbit.getCreatedBy() == null ) {
			tidbit.setCreatedBy(currentUser.getName());
		}
	}

	@Before("magoffin.matt.tidbits.aop.Business.saveTidbitKind(kind)")
	public void beforeTidbitKind(TidbitKind kind) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ( currentUser != null && kind != null && kind.getCreatedBy() == null ) {
			kind.setCreatedBy(currentUser.getName());
		}
	}

	@Before("magoffin.matt.tidbits.aop.Business.saveTidbits(list)")
	public void beforeTidbits(List<Tidbit> list) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ( currentUser != null && list != null ) {
			for ( Tidbit t : list ) {
				if ( t.getCreatedBy() == null ) {
					t.setCreatedBy(currentUser.getName());
				}
				if ( t.getKind() != null && t.getKind().getCreatedBy() == null ) {
					t.getKind().setCreatedBy(currentUser.getName());
				}
			}
		}
	}

}
