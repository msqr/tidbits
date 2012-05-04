/* ===================================================================
 * JpaTidbitKindDao.java
 * 
 * Created May 4, 2012 4:54:03 PM
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

package magoffin.matt.tidbits.dao.jpa;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import magoffin.matt.dao.BasicSortDescriptor;
import magoffin.matt.dao.SortDescriptor;
import magoffin.matt.dao.jpa.GenericJpaDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.domain.TidbitKind;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA implementation of {@link TidbitKindDao}.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
@NamedQueries({ @NamedQuery(name = "KindForName", query = "SELECT k FROM TidbitKind k WHERE k.name = :name") })
public class JpaTidbitKindDao extends GenericJpaDao<TidbitKind, Long> implements TidbitKindDao {

	/**
	 * Default constructor.
	 */
	public JpaTidbitKindDao() {
		super(TidbitKind.class);
	}

	@Override
	protected Long getPrimaryKey(TidbitKind domainObject) {
		return domainObject.getKindId();
	}

	@Override
	@PersistenceContext(unitName = "tidbits")
	public void setEm(EntityManager em) {
		super.setEm(em);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<TidbitKind> getAllTidbitKinds() {
		List<SortDescriptor> sort = Collections.singletonList((SortDescriptor)new BasicSortDescriptor("name", true));
		return getAll(sort);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public TidbitKind getTidbitKindByName(String name) {
		List<TidbitKind> results = findTidbitKindsByName(name);
		if ( results.size() > 0 ) {
			return results.get(0);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<TidbitKind> findTidbitKindsByName(String name) {
		TypedQuery<TidbitKind> q = getEm().createNamedQuery("KindForName", TidbitKind.class);
		q.setParameter("name", name);
		q.setMaxResults(1);
		return q.getResultList();
	}

}
