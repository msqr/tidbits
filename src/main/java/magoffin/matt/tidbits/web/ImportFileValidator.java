/* ===================================================================
 * ImportFileValidator.java
 * 
 * Created Jun 28, 2012 2:13:20 PM
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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for the {@link ImportFileForm}.
 * 
 * @author matt
 * @version 1.0
 */
public class ImportFileValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ImportFileForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ImportFileForm form = (ImportFileForm) target;
		if ( "import".equals(form.getPage()) ) {
			if ( form.getFile() == null ) {
				errors.rejectValue("file", "error.import.file.required", null,
						"The import file is required.");
			}
		}

	}

}
