/* ===================================================================
 * HibernateUserDao.java
 * 
 * Created Jul 2, 2006 1:15:27 PM
 * 
 * Copyright (c) 2006 Matt Magoffin.
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

package magoffin.matt.tidbits.dao.hbm;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import magoffin.matt.dao.hbm.GenericHibernateDao;
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * Hibernate implementation of TidbitDao.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class HibernateTidbitDao extends GenericHibernateDao<Tidbit,Long> 
implements TidbitDao {
	
	/** The query to find all Tidbit objects. */
	public static final String FIND_ALL = "TidbitAll";
	
	/** The query to find the count of all Tidbit objects. */
	public static final String FIND_ALL_COUNT = "TidbitAllCount";
	
	/** The query to find the count of all Tidbit objects. */
	public static final String UPDATE_KIND_REASSIGN = "TidbitUpdateKindReassign";
	
	private DomainObjectFactory domainObjectFactory;

	/**
	 * Default constructor.
	 */
	public HibernateTidbitDao() {
		super(Tidbit.class);
	}

	@Override
	protected Long getPrimaryKey(Tidbit domainObject) {
		if ( domainObject == null ) return null;
		return domainObject.getTidbitId();
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.dao.TidbitDao#reassignTidbitKinds(magoffin.matt.tidbits.domain.TidbitKind, magoffin.matt.tidbits.domain.TidbitKind)
	 */
	public int reassignTidbitKinds(final TidbitKind original, final TidbitKind reassign) {
		return (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws SQLException {
				Query query = session.getNamedQuery(UPDATE_KIND_REASSIGN);
				query.setLong("originalKindId", original.getKindId());
				query.setLong("reassignKindId", reassign.getKindId());
				int result = query.executeUpdate();
				session.flush();
				session.clear();
				return result;
			}
		});
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.dao.TidbitDao#getAllTidbitKinds(magoffin.matt.tidbits.domain.PaginationCriteria)
	 */
	@SuppressWarnings("unchecked")
	public SearchResults getAllTidbits(final PaginationCriteria pagination) {
		SearchResults results = domainObjectFactory.newSearchResultsInstance();
		if ( pagination == null ) {
			// return all available
			List<Tidbit> allTidbits = findByNamedQuery(FIND_ALL, (Object[])null);
			results.getTidbit().addAll(allTidbits);
			results.setIsPartialResult(false);
			results.setReturnedResults(BigInteger.valueOf(allTidbits.size()));
			results.setTotalResults(results.getReturnedResults());
			return results;
		}
		
		Integer count = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.getNamedQuery(FIND_ALL_COUNT);
				return ((Integer)query.iterate().next() ).intValue();
			}
		});
		
		List<Tidbit> pagedList = findByNamedQuery(FIND_ALL, (Object[])null, 
				pagination.getPageOffset().intValue(), 
				pagination.getPageSize().intValue());
		
		results.getTidbit().addAll(pagedList);
		results.setReturnedResults(BigInteger.valueOf(pagedList.size()));
		results.setTotalResults(BigInteger.valueOf(count.longValue()));
		results.setIsPartialResult(count.longValue()>pagedList.size());
		results.setPagination(pagination);
		return results;
	}

	/**
	 * @return Returns the domainObjectFactory.
	 */
	public DomainObjectFactory getDomainObjectFactory() {
		return domainObjectFactory;
	}

	/**
	 * @param domainObjectFactory The domainObjectFactory to set.
	 */
	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}

}
