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

import java.lang.reflect.Method;
import java.util.Collection;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Aspect to inject the "createdBy" property into objects as they are created.
 * 
 * <p>This interceptor will apply the Acegi current user's username, if 
 * available, to all Tidbit and TidbitKind objects found in the method arguments.
 * If a method argument is a {@link Collection}, then the objects in the Collection
 * are inspected, too.</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class CreatedByInterceptor implements MethodBeforeAdvice {
	
	private String createdByPropertyName = "createdBy";

	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		if ( args == null ) return;
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ( currentUser == null ) {
			return;
		}

		for ( Object arg : args ) {
			if ( arg instanceof Tidbit || arg instanceof TidbitKind ) {
				applyCreatedBy(arg, currentUser);
			} else if ( arg instanceof Collection<?> ) {
				Collection<?> collection = (Collection<?>)arg;
				for ( Object collectionObj : collection ) {
					if ( collectionObj instanceof Tidbit || arg instanceof TidbitKind ) {
						applyCreatedBy(collectionObj, currentUser);
					}
				}
			}
		}
		
	}

	private void applyCreatedBy(Object obj, Authentication currentUser) {
		BeanWrapper wrapper = new BeanWrapperImpl(obj);
		if ( wrapper.isWritableProperty(createdByPropertyName) 
				&& wrapper.isReadableProperty(createdByPropertyName)) {
			Object val = wrapper.getPropertyValue(createdByPropertyName);
			if ( val == null ) {
				Object userDetails = currentUser.getPrincipal();
				if ( userDetails instanceof UserDetails ) {
					UserDetails details = (UserDetails)userDetails;
					wrapper.setPropertyValue(createdByPropertyName, details.getUsername());
				}
			}
		}
		if ( obj instanceof Tidbit ) {
			// make sure TidbitKind has created by, too
			Tidbit t = (Tidbit)obj;
			if ( t.getKind() != null ) {
				applyCreatedBy(t.getKind(), currentUser);
			}
		}
	}
	
	/**
	 * @return the createdByPropertyName
	 */
	public String getCreatedByPropertyName() {
		return createdByPropertyName;
	}
	
	/**
	 * @param createdByPropertyName the createdByPropertyName to set
	 */
	public void setCreatedByPropertyName(String createdByPropertyName) {
		this.createdByPropertyName = createdByPropertyName;
	}

}
