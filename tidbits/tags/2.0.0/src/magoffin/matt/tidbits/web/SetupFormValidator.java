/* ===================================================================
 * SetupFormValidator.java
 * 
 * Created Jun 5, 2012 5:29:14 PM
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
 * $Id$
 * ===================================================================
 */

package magoffin.matt.tidbits.web;

import java.io.File;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for the SetupForm.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public class SetupFormValidator implements Validator {

	@Override
	public void validate(Object obj, Errors errors) {
		SetupForm command = (SetupForm) obj;

		if ( "filesystem".equals(command.getPage()) ) {
			// verify index directory
			String indexPath = command.getSettings().get("lucene.index.base.path");
			File indexDir = new File(indexPath);
			if ( !(indexDir.exists() || indexDir.mkdirs())
					|| !(indexDir.isDirectory() && indexDir.canWrite()) ) {
				errors.rejectValue("settings", "setup.fs.index.dir.error", null,
						"Unable to verify index directory.");
			}
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return SetupForm.class.isAssignableFrom(clazz);
	}

}
