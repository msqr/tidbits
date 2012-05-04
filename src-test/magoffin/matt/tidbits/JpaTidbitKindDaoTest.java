/* ===================================================================
 * JpaTidbitKindDaoTest.java
 * 
 * Created May 4, 2012 5:16:27 PM
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

package magoffin.matt.tidbits;

import static org.junit.Assert.assertNotNull;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitKindDao;
import magoffin.matt.tidbits.domain.TidbitKind;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link JpaTidbitKindDao} class.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public class JpaTidbitKindDaoTest extends BaseTransactionalTest {

	private JpaTidbitKindDao dao;

	@Before
	public void setup() {
		dao = new JpaTidbitKindDao();
		dao.setEm(getEm());
	}

	/**
	 * Test able to persist a new group.
	 */
	@Test
	public void storeEntity() {
		TidbitKind obj = new TidbitKind();
		obj.setComment("comment");
		obj.setName("name");
		Long pk = dao.store(obj);
		assertNotNull("Primary key must not be null", pk);
	}
}
