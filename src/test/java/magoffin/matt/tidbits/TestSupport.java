/* ===================================================================
 * TestSupport.java
 * 
 * Created 1/08/2021 3:09:41 PM
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

package magoffin.matt.tidbits;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * Test support.
 *
 * @author matt
 * @version 1.0
 */
public class TestSupport {

	/**
	 * Set the current actor.
	 * 
	 * @param auth
	 *        the authentication
	 */
	public static void setActor(Authentication auth) {
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	/**
	 * Clear the current actor.
	 */
	public static void clearActor() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	/**
	 * Become an authenticated user with the given roles.
	 * 
	 * @param userName
	 *        the user name
	 * @param roles
	 *        the roles
	 */
	public static void becomeUser(String userName, String... roles) {
		User userDetails = new User(userName, "foobar", AuthorityUtils.NO_AUTHORITIES);
		TestingAuthenticationToken auth = new TestingAuthenticationToken(userDetails, userDetails,
				roles);
		setActor(auth);
	}

}
