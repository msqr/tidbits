/* ===================================================================
 * BaseTransactionalTest.java
 * 
 * Created May 4, 2012 4:50:32 PM
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for other transactional unit tests.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
@ContextConfiguration
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public abstract class BaseTransactionalTest extends AbstractTransactionalJUnit4SpringContextTests {

	/** A class-level logger. */
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = "tidbits")
	private EntityManager em;

	/**
	 * Get the configured EntityManager.
	 * 
	 * <p>
	 * This EntityManager is re-created at the start of every test case.
	 * </p>
	 * 
	 * @return EntityManager
	 */
	protected EntityManager getEm() {
		return em;
	}

	/**
	 * Flush the {@link EntityManager} so pushed to database.
	 */
	protected void flushEntityManager() {
		em.flush();
	}

	/**
	 * Flush the {@link EntityManager} as well as refresh an entity.
	 * 
	 * @param entity
	 *        the entity to refresh
	 */
	protected void refreshEntity(Object entity) {
		flushEntityManager();
		em.refresh(entity);
	}

}
