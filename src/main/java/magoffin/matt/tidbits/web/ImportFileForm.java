/* ===================================================================
 * ImportFileForm.java
 * 
 * Created Jun 28, 2012 1:52:18 PM
 * 
 * Copyright (c) 2012 Matt Magoffin.
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

package magoffin.matt.tidbits.web;

import java.util.List;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.util.TemporaryFile;

/**
 * Form for importing file data into Tidbits.
 * 
 * @author matt
 * @version 1.0
 */
public class ImportFileForm {

	private String page;
	private TemporaryFile file;
	private List<Tidbit> tidbits;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public TemporaryFile getFile() {
		return file;
	}

	public void setFile(TemporaryFile file) {
		this.file = file;
	}

	public List<Tidbit> getTidbits() {
		return tidbits;
	}

	public void setTidbits(List<Tidbit> tidbits) {
		this.tidbits = tidbits;
	}

}
