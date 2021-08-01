/* ===================================================================
 * AuthorizationSupport.java
 * 
 * Created 1/08/2021 7:50:28 PM
 * 
 * Copyright (c) 2021 Matt Magoffin.
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

package magoffin.matt.tidbits.biz.impl;

import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import magoffin.matt.tidbits.biz.AuthorizationException;
import magoffin.matt.tidbits.biz.TidbitsBiz;

/**
 * Authorization support.
 *
 * @author matt
 * @version 1.0
 */
public final class AuthorizationSupport {

	/**
	 * Extract the username from an actor.
	 * 
	 * @param actor
	 *        the actor
	 * @return the username, never {@literal null}
	 * @throws AuthorizationException
	 *         if no username can be found
	 */
	public static String username(Authentication actor) {
		if ( actor == null ) {
			return null;
		}
		String username = null;
		Object principal = actor.getPrincipal();
		if ( principal instanceof UserDetails ) {
			username = ((UserDetails) principal).getUsername();
		} else if ( principal instanceof String ) {
			username = (String) principal;
		}
		if ( username == null ) {
			throw new AuthorizationException("Unknown username.");
		}
		return username;
	}

	/**
	 * Test if an actor has an administrative role.
	 * 
	 * @param actor
	 *        the actor
	 * @return {@literal true} if the actor has the
	 *         {@link TidbitsBiz#ROLE_ADMIN} authority
	 */
	public static boolean isAdmin(Authentication actor) {
		if ( actor == null ) {
			return false;
		}
		Collection<? extends GrantedAuthority> auths = actor.getAuthorities();
		return auths.stream().anyMatch(a -> TidbitsBiz.ROLE_ADMIN.equalsIgnoreCase(a.getAuthority()));
	}

}
