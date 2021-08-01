/* ===================================================================
 * SecurityInterceptor.java
 * 
 * Created 1/08/2021 12:05:00 PM
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

package magoffin.matt.tidbits.aop;

import java.util.List;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import magoffin.matt.tidbits.biz.AuthorizationException;
import magoffin.matt.tidbits.biz.impl.AuthorizationSupport;
import magoffin.matt.tidbits.dao.PermissionGroupDao;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.domain.Permission;
import magoffin.matt.tidbits.domain.PermissionGroup;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * Enforce permission group security for tidbit maintenance.
 *
 * @author matt
 * @version 1.0
 */
@Aspect
@Component
public class SecurityInterceptor {

	@Autowired
	private PermissionGroupDao permissionGroupDao;

	@Autowired
	private TidbitDao tidbitDao;

	@Autowired
	private TidbitKindDao tidbitKindDao;

	private void enforcePermissions(Authentication actor, String owner) {
		if ( actor == null || owner == null ) {
			return;
		}
		String actorUsername = AuthorizationSupport.username(actor);
		if ( actorUsername.equalsIgnoreCase(owner) ) {
			// can always edit own entity
			return;
		}

		boolean admin = AuthorizationSupport.isAdmin(actor);
		if ( admin ) {
			// admin can edit anything
			return;
		}

		PermissionGroup group = permissionGroupDao.getPermissionGroupByName(owner);
		if ( group == null || group.getPermission() == null || group.getPermission().isEmpty() ) {
			throw new AuthorizationException("Not authorized.");
		}
		for ( Permission p : group.getPermission() ) {
			if ( p != null && p.getName() != null && p.isWrite()
					&& p.getName().equalsIgnoreCase(actorUsername) ) {
				//  permission granted
				return;
			}
		}
		throw new AuthorizationException("Not authorized.");
	}

	private void handleBeforeTidbit(Authentication actor, Tidbit tidbit) {
		if ( actor != null && tidbit != null ) {
			if ( tidbit.getId() != null ) {
				Tidbit existing = tidbitDao.get(tidbit.getId());
				if ( existing != null ) {
					tidbit.setCreatedBy(existing.getCreatedBy());
				}
			}
			if ( tidbit.getCreatedBy() == null ) {
				tidbit.setCreatedBy(actor.getName());
			} else {
				enforcePermissions(actor, tidbit.getCreatedBy());
			}
			if ( tidbit.getKind() != null && tidbit.getKind().getCreatedBy() == null ) {
				tidbit.getKind().setCreatedBy(actor.getName());
			}
		}
	}

	@Before("magoffin.matt.tidbits.aop.Business.saveTidbit(tidbit)")
	public void beforeTidbit(Tidbit tidbit) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		handleBeforeTidbit(currentUser, tidbit);
	}

	@Before("magoffin.matt.tidbits.aop.Business.saveTidbits(list)")
	public void beforeTidbits(List<Tidbit> list) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ( currentUser != null && list != null ) {
			for ( Tidbit t : list ) {
				handleBeforeTidbit(currentUser, t);
			}
		}
	}

	@Before("magoffin.matt.tidbits.aop.Business.saveTidbitKind(kind)")
	public void beforeTidbitKind(TidbitKind kind) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ( currentUser != null && kind != null ) {
			if ( kind.getId() != null ) {
				TidbitKind existing = tidbitKindDao.get(kind.getId());
				if ( existing != null ) {
					kind.setCreatedBy(existing.getCreatedBy());
				}
			}
			if ( kind.getCreatedBy() == null ) {
				kind.setCreatedBy(currentUser.getName());
			} else {
				enforcePermissions(currentUser, kind.getCreatedBy());
			}
		}
	}

	@Before("magoffin.matt.tidbits.aop.Business.deleteTidbitKind(id)")
	public void beforeDeleteTidbitKind(Long id) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if ( currentUser != null && id != null ) {
			TidbitKind kind = tidbitKindDao.get(id);
			if ( kind != null ) {
				enforcePermissions(currentUser, kind.getCreatedBy());
			}
		}
	}

	public PermissionGroupDao getPermissionGroupDao() {
		return permissionGroupDao;
	}

	public void setPermissionGroupDao(PermissionGroupDao permissionGroupDao) {
		this.permissionGroupDao = permissionGroupDao;
	}

	public TidbitDao getTidbitDao() {
		return tidbitDao;
	}

	public void setTidbitDao(TidbitDao tidbitDao) {
		this.tidbitDao = tidbitDao;
	}

	public TidbitKindDao getTidbitKindDao() {
		return tidbitKindDao;
	}

	public void setTidbitKindDao(TidbitKindDao tidbitKindDao) {
		this.tidbitKindDao = tidbitKindDao;
	}

}
