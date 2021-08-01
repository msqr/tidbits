/* ===================================================================
 * JpaPermissionGroupDao.java
 * 
 * Created 31/07/2021 12:03:58 PM
 * 
 * Copyright (c) 2021 Matt Magoffin.
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

package magoffin.matt.tidbits.dao.jpa;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import magoffin.matt.dao.BasicSortDescriptor;
import magoffin.matt.dao.SortDescriptor;
import magoffin.matt.dao.jpa.GenericJpaDao;
import magoffin.matt.tidbits.dao.PermissionGroupDao;
import magoffin.matt.tidbits.domain.Permission;
import magoffin.matt.tidbits.domain.PermissionGroup;

/**
 * JPA implementation of {@link PermissionGroupDao}.
 *
 * @author matt
 * @version 1.0
 */
@Repository
public class JpaPermissionGroupDao extends GenericJpaDao<PermissionGroup, Long>
		implements PermissionGroupDao {

	/**
	 * Default constructor.
	 */
	public JpaPermissionGroupDao() {
		super(PermissionGroup.class);
		setRefreshOnPersist(true);
	}

	/**
	 * Construct with an {@link EntityManager}.
	 * 
	 * @param em
	 *        the EntityManager
	 */
	public JpaPermissionGroupDao(EntityManager em) {
		this();
		setEm(em);
	}

	@Override
	protected final void prePersist(PermissionGroup domainObject) {
		if ( domainObject.getCreationDate() == null ) {
			domainObject.setCreationDateItem(new Date());
		}
		List<Permission> perms = domainObject.getPermission();
		if ( perms != null ) {
			for ( Permission p : perms ) {
				if ( p.getCreationDate() == null ) {
					p.setCreationDate(domainObject.getCreationDate());
				}
			}
		}
	}

	@Override
	protected final void preUpdate(PermissionGroup domainObject) {
		domainObject.setModifyDateItem(new Date());
	}

	@Override
	protected Long getPrimaryKey(PermissionGroup domainObject) {
		return domainObject.getId();
	}

	@Override
	@PersistenceContext(unitName = "tidbits")
	public void setEm(EntityManager em) {
		super.setEm(em);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<PermissionGroup> getAllPermissionGroups() {
		List<SortDescriptor> sort = Collections
				.singletonList((SortDescriptor) new BasicSortDescriptor("name", true));
		return getAll(sort);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PermissionGroup getPermissionGroupByName(String name) {
		TypedQuery<PermissionGroup> q = getEm().createNamedQuery("PermissionGroupForName",
				PermissionGroup.class);
		q.setParameter("name", name);
		q.setMaxResults(1);
		List<PermissionGroup> results = q.getResultList();
		if ( results.size() > 0 ) {
			return results.get(0);
		}
		return null;
	}

}
