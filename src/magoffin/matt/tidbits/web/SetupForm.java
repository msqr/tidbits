/* ===================================================================
 * SetupForm.java
 * 
 * Created Jun 5, 2012 5:11:39 PM
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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Form object for setup.
 * 
 * @author matt
 * @version 1.0
 */
public class SetupForm implements Serializable {

	private static final long serialVersionUID = -2260773056388671033L;

	private String page;
	private Map<String, String> settings = new LinkedHashMap<String, String>();
	private boolean changedJpaPlatform = false;

	public String getPage() {
		return page;
	}

	public Map<String, String> getSettings() {
		return settings;
	}

	public boolean isChangedJpaPlatform() {
		return changedJpaPlatform;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public void setChangedJpaPlatform(boolean changedJpaPlatform) {
		this.changedJpaPlatform = changedJpaPlatform;
	}

}
