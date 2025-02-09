/* ===================================================================
 * DatatablesSearchCommand.java
 * 
 * Created Jun 21, 2009 2:54:32 PM
 * 
 * Copyright (c) 2009 Matt Magoffin.
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

package magoffin.matt.tidbits.support;

/**
 * Support for jQuery DataTables plugin.
 * 
 * @author matt
 * @version 1.0
 */
public class DatatablesSearchCommand extends SearchCommand {

	private static final long serialVersionUID = -1468648318018752620L;

	/**
	 * Set the query text.
	 * 
	 * @param query
	 *        the query
	 */
	public void setsSearch(String query) {
		setQuery(query);
	}

	/**
	 * Set the starting offset.
	 * 
	 * @param offset
	 *        the offset
	 */
	public void setiDisplayStart(int offset) {
		// hmm, this depends on iDisplayLength having already been set. yuck
		int page = (int) Math.floor((double) offset / (double) getPageSize());
		setPage(page);
	}

	/**
	 * Set the page size.
	 * 
	 * @param pageSize
	 *        the page size
	 */
	public void setiDisplayLength(int pageSize) {
		setPageSize(pageSize);
	}

}
