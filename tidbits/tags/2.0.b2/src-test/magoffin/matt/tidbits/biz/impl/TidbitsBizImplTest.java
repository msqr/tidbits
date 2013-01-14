/* ===================================================================
 * TidbitsBizImplTest.java
 * 
 * Created Jul 3, 2012 6:45:56 PM
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

package magoffin.matt.tidbits.biz.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import java.util.List;
import magoffin.matt.tidbits.BaseTransactionalTest;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitDao;
import magoffin.matt.tidbits.dao.jpa.JpaTidbitKindDao;
import magoffin.matt.tidbits.domain.Tidbit;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Unit test for the {@link TidbitsBizImplTest} class.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public class TidbitsBizImplTest extends BaseTransactionalTest {

	private TidbitDao tidbitDao;
	private TidbitKindDao tidbitKindDao;
	private TidbitsBizImpl biz;

	@Before
	public void setup() {
		tidbitDao = new JpaTidbitDao(getEm());
		tidbitKindDao = new JpaTidbitKindDao(getEm());
		biz = new TidbitsBizImpl();
		biz.setDomainObjectFactory(new JAXBDomainObjectFactory());
		biz.setTidbitDao(tidbitDao);
		biz.setTidbitKindDao(tidbitKindDao);
	}

	@Test
	public void importCsv() throws Exception {
		List<Tidbit> tidbits = biz.parseCsvData(new ClassPathResource("tidbits-sample.csv", getClass())
				.getInputStream());
		assertNotNull(tidbits);
		assertEquals("Should have parsed", 24, tidbits.size());
		for ( Tidbit t : tidbits ) {
			assertNull("Primary key should not be set", t.getId());
			assertNotNull("Kind should be available", t.getKind());
		}
	}
}