/* ===================================================================
 * ExportCallback.java
 * 
 * Created Jan 15, 2013 7:27:49 AM
 * 
 * Copyright (c) 2013 Matt Magoffin.
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

package magoffin.matt.tidbits.dao;

/**
 * API for exporting data.
 * 
 * @author matt
 * @version 1.0
 */
public interface ExportCallback {

	/**
	 * Handle a single row of data to export.
	 * 
	 * @param data
	 *        the data to export
	 * @param row
	 *        the row, starting at 0
	 * @return <em>true</em> to keep exporting, <em>false</em> to stop
	 */
	boolean handleRow(Object[] data, int row);

}
