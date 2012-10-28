/* ===================================================================
 * SearchQueryException.java
 * 
 * Created Jul 30, 2006 5:21:57 PM
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

package magoffin.matt.tidbits.biz;

/**
 * An exception thrown when an invalid search query is entered.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class SearchQueryException extends RuntimeException {

	private static final long serialVersionUID = -4058092861741774608L;

	/**
	 * Default constructor.
	 */
	public SearchQueryException() {
		super();
	}

	/**
	 * Construct with a message and nested exception.
	 * @param msg message
	 * @param t nested exception
	 */
	public SearchQueryException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * Construct with a message.
	 * @param msg the mssage
	 */
	public SearchQueryException(String msg) {
		super(msg);
	}

	/**
	 * Construct with a nested exception.
	 * @param t the nested exception
	 */
	public SearchQueryException(Throwable t) {
		super(t);
	}

}
