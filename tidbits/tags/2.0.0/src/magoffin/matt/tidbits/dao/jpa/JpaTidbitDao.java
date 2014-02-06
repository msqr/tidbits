/* ===================================================================
 * JpaTidbitDao.java
 * 
 * Created May 6, 2012 4:33:47 PM
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
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import magoffin.matt.dao.BasicSortDescriptor;
import magoffin.matt.dao.SortDescriptor;
import magoffin.matt.dao.jpa.GenericJpaDao;
import magoffin.matt.tidbits.dao.ExportCallback;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * JPA implementation of {@link TidbitDao}.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
@Repository
public class JpaTidbitDao extends GenericJpaDao<Tidbit, Long> implements TidbitDao {

	/**
	 * Default constructor.
	 */
	public JpaTidbitDao() {
		super(Tidbit.class);
		setRefreshOnPersist(true);
	}

	/**
	 * Construct with an {@link EntityManager}.
	 * 
	 * @param em
	 *        the EntityManager
	 */
	public JpaTidbitDao(EntityManager em) {
		this();
		setEm(em);
	}

	@Override
	protected final void prePersist(Tidbit domainObject) {
		if ( domainObject.getCreationDate() == null ) {
			domainObject.setCreationDateItem(new Date());
		}
	}

	@Override
	protected final void preUpdate(Tidbit domainObject) {
		domainObject.setModifyDateItem(new Date());
	}

	@Override
	protected Long getPrimaryKey(Tidbit domainObject) {
		return domainObject.getId();
	}

	@Override
	@PersistenceContext(unitName = "tidbits")
	public void setEm(EntityManager em) {
		super.setEm(em);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public SearchResults getAllTidbits(PaginationCriteria pagination) {
		SearchResults results = new SearchResults();
		if ( pagination == null ) {
			// return all available
			SortDescriptor order = new BasicSortDescriptor("creationDateItem", false);
			List<Tidbit> allTidbits = super.getAll(Collections.singletonList(order));
			results.setTidbit(allTidbits);
			results.setIsPartialResult(false);
			results.setReturnedResults(Long.valueOf(allTidbits.size()));
			results.setTotalResults(results.getReturnedResults());
			return results;
		}

		TypedQuery<Long> countQuery = getEm().createNamedQuery("TidbitCount", Long.class);
		final Long count = countQuery.getSingleResult();

		TypedQuery<Tidbit> allQuery = getEm().createNamedQuery("TidbitsAll", Tidbit.class);
		allQuery.setFirstResult((pagination.getPageOffset() == null ? 0 : pagination.getPageOffset()
				.intValue())
				* (pagination.getPageSize() == null ? 0 : pagination.getPageSize().intValue()));
		if ( pagination.getPageSize() != null ) {
			allQuery.setMaxResults(pagination.getPageSize().intValue());
		}
		List<Tidbit> all = allQuery.getResultList();

		results.setTidbit(all);
		results.setReturnedResults(Long.valueOf(all.size()));
		results.setTotalResults(count);
		results.setIsPartialResult(count.longValue() > all.size());
		results.setPagination(pagination);
		return results;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int reassignTidbitKinds(TidbitKind original, TidbitKind reassign) {
		// make sure changes from native query are immediately picked up
		getEm().flush();
		getEm().clear();

		TypedQuery<TidbitKind> q = getEm()
				.createNamedQuery("TidbitUpdateKindReassign", TidbitKind.class);
		q.setParameter(1, reassign.getId());
		q.setParameter(2, original.getId());
		int result = q.executeUpdate();
		return result;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void exportAllTidbits(ExportCallback callback) {
		// make sure changes from native query are immediately picked up
		getEm().flush();
		getEm().clear();
		TypedQuery<Object[]> q = getEm().createNamedQuery("TidbitsExport", Object[].class);
		int offset = 0;
		boolean keepGoing = true;
		while ( keepGoing ) {
			q.setFirstResult(offset);
			q.setMaxResults(100);
			List<Object[]> results = q.getResultList();
			if ( results.size() < 1 ) {
				break;
			}
			for ( Object[] row : results ) {
				keepGoing = callback.handleRow(row, offset++);
			}
		}
	}

}
