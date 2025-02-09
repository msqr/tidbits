/* ===================================================================
 * TidbitsBiz.java
 * 
 * Created Jul 1, 2006 9:47:42 PM
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
 */

package magoffin.matt.tidbits.biz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * API for Tidbits application.
 * 
 * @author matt.magoffin
 * @version 2.0
 */
public interface TidbitsBiz {

	/**
	 * The role name for a normal user.
	 * 
	 * @since 2.0
	 */
	String ROLE_USER = "ROLE_USER";

	/**
	 * The role name for an administrative user.
	 * 
	 * @since 2.0
	 */
	String ROLE_ADMIN = "ROLE_ADMINISTRATOR";

	/**
	 * Search for Tidbits.
	 * 
	 * @param searchCriteria
	 *        the search criteria
	 * @return the search results
	 */
	SearchResults findTidbits(TidbitSearchCriteria searchCriteria);

	/**
	 * Get a Tidbit by it's ID.
	 * 
	 * @param id
	 *        the ID of the Tidbit to get
	 * @return the Tidbit, or <em>null</em> if not available
	 */
	Tidbit getTidbit(Long id);

	/**
	 * Save a Tidbit.
	 * 
	 * @param tidbit
	 *        the tidbit to save
	 * @return the saved Tidbit
	 */
	Tidbit saveTidbit(Tidbit tidbit);

	/**
	 * Delete a Tidbit.
	 * 
	 * @param id
	 *        the ID of the Tidbit to delete
	 */
	void deleteTidbit(Long id);

	/**
	 * Save a List of Tidbit objects.
	 * 
	 * @param tidbits
	 *        the Tidbits to save
	 * @return the saved tidbit IDs
	 */
	List<Long> saveTidbits(List<Tidbit> tidbits);

	/**
	 * Delete a TidbitKind.
	 * 
	 * @param id
	 *        the ID of the TidbitKind to delete
	 * @param reassignId
	 *        the TidbitKind ID to reassign tidbits to
	 */
	void deleteTidbitKind(Long id, Long reassignId);

	/**
	 * Save a TidbitKind.
	 * 
	 * @param kind
	 *        the tidbit kind to save
	 * @return the saved kind
	 */
	TidbitKind saveTidbitKind(TidbitKind kind);

	/**
	 * Get all available TidbitKind instances.
	 * 
	 * @return all TidbitKind objects available
	 */
	List<TidbitKind> getAvailableTidbitKinds();

	/**
	 * Parse a CSV formatted data file.
	 * 
	 * @param input
	 *        the input
	 * @return the parsed results (not persisted)
	 */
	List<Tidbit> parseCsvData(InputStream input);

	/**
	 * Export all Tidbits as a CSV data file.
	 * 
	 * <p>
	 * The UTF-8 character encoding will be used.
	 * </p>
	 * 
	 * @param out
	 *        the output stream to export to
	 * @throws IOException
	 *         if any IO error occurs
	 */
	void exportCsvData(OutputStream out) throws IOException;
}
