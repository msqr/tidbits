/* ===================================================================
 * UserBiz.java
 * 
 * Created Jul 1, 2006 8:45:14 PM
 * 
 * Copyright (c) 2006 Matt Magoffin.
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

package magoffin.matt.tidbits.biz;

/**
 * API for user related tasks.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public interface UserBiz {

	/** 
	 * Flag for a String value that should not change.
	 * 
	 * <p>For example, when updating a User, the password field can be 
	 * left unchagned when set to this value.</p>
	 */
	public static final String DO_NOT_CHANGE_VALUE = "**DO_NOT_CHANGE**";
	
}
