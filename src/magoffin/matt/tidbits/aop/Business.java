/* ===================================================================
 * Business.java
 * 
 * Created May 8, 2012 9:18:45 AM
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

package magoffin.matt.tidbits.aop;

import java.util.List;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * Commong pointcuts for the application.
 * 
 * @author matt
 * @version 1.0
 */
@Aspect
public class Business {

	/**
	 * Match TidbitsBiz methods saving Tidbit.
	 * 
	 * @param tidbit
	 *        the tidbit being saved
	 */
	@Pointcut("execution(* magoffin.matt.tidbits.biz.TidbitsBiz.saveTidbit(..)) && args(tidbit)")
	public void saveTidbit(Tidbit tidbit) {
	}

	/**
	 * Match TidbitsBiz methods saving a list of Tidbits.
	 * 
	 * @param list
	 *        the list of tidbits being saved
	 */
	@Pointcut("execution(* magoffin.matt.tidbits.biz.TidbitsBiz.saveTidbits(..)) && args(list)")
	public void saveTidbits(List<Tidbit> list) {
	}

	/**
	 * Match TidbitsBiz methods saving TidbitKind.
	 * 
	 * @param kind
	 *        the tidbit kind being saved
	 */
	@Pointcut("execution(* magoffin.matt.tidbits.biz.TidbitsBiz.saveTidbitKind(..)) && args(kind)")
	public void saveTidbitKind(TidbitKind kind) {
	}

	/**
	 * Match TidbitsBiz methods deleting Tidbit.
	 * 
	 * @param id
	 *        the tidbit ID being deleted
	 */
	@Pointcut("execution(* magoffin.matt.tidbits.biz.TidbitsBiz.deleteTidbit(..)) && args(id)")
	public void deleteTidbit(Long id) {
	}

}
