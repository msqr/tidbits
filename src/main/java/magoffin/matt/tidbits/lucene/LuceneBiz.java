/* ===================================================================
 * LuceneBiz.java
 * 
 * Created May 27, 2006 12:10:08 PM
 * 
 * Copyright (c) 2006 Matt Magoffin (spamsqr@msqr.us)
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

package magoffin.matt.tidbits.lucene;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import magoffin.matt.lucene.LuceneService;
import magoffin.matt.lucene.LuceneService.IndexSearcherOp;
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.SearchQueryException;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria;
import magoffin.matt.tidbits.biz.impl.AuthorizationSupport;
import magoffin.matt.tidbits.dao.PermissionGroupDao;
import magoffin.matt.tidbits.domain.PermissionGroup;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;

/**
 * Lucene search implementation for Tidbits.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version 1.1
 */
@Service
public class LuceneBiz implements magoffin.matt.tidbits.biz.LuceneBiz {

	/** Default max number of search results returned. */
	public static final int DEFAULT_MAX_SEARCH_RESULTS = 100000;

	@Autowired
	private LuceneService lucene;

	@Autowired
	private MessageSource messages;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@Autowired
	private PermissionGroupDao permissionGroupDao;

	private String tidbitIndexType = IndexType.TIDBIT.toString();
	private int maxSearchResults = DEFAULT_MAX_SEARCH_RESULTS;

	private Set<String> allowedUsernames() {
		Authentication actor = SecurityContextHolder.getContext().getAuthentication();
		String username = AuthorizationSupport.username(actor);
		if ( AuthorizationSupport.isAdmin(actor) ) {
			// no limit
			return null;
		}
		Set<PermissionGroup> memberships = permissionGroupDao
				.findAllPermissionGroupMemberships(username);
		if ( memberships == null || memberships.isEmpty() ) {
			// not a member of any groups, can only see own
			return Collections.singleton(username);
		}
		Set<String> result = memberships.stream().map(PermissionGroup::getName)
				.collect(Collectors.toSet());
		result.add(username);
		return result;
	}

	/**
	 * Search for Tidbits.
	 * 
	 * @param tidbitCriteria
	 *        the search criteria
	 * @return the results
	 */
	@SuppressWarnings("deprecation")
	@Override
	public SearchResults findTidbits(final TidbitSearchCriteria tidbitCriteria) {
		final Set<String> usernames = allowedUsernames();
		final SearchResults results = domainObjectFactory.newSearchResultsInstance();
		results.setQuery(tidbitCriteria.getQuery());
		lucene.doIndexSearcherOp(tidbitIndexType, new IndexSearcherOp() {

			@Override
			public void doSearcherOp(String type, IndexSearcher searcher) throws IOException {
				Query query = null;
				try {
					query = lucene.parseQuery(type, tidbitCriteria.getQuery());
				} catch ( RuntimeException e ) {
					if ( e.getCause() != null
							&& (e.getCause() instanceof org.apache.lucene.queryParser.ParseException) ) {
						throw new SearchQueryException(e.getCause());
					}
					throw e;
				}

				if ( usernames != null && !usernames.isEmpty() ) {
					BooleanQuery usernameQuery = new BooleanQuery();
					for ( String username : usernames ) {
						usernameQuery.add(
								new TermQuery(new Term(IndexField.CREATED_BY.getFieldName(), username)),
								Occur.SHOULD);
					}

					BooleanQuery searchQuery = new BooleanQuery();
					searchQuery.add(usernameQuery, Occur.MUST);
					searchQuery.add(query, Occur.MUST);
					query = searchQuery;
				}

				org.apache.lucene.search.TopDocCollector col = new org.apache.lucene.search.TopDocCollector(
						maxSearchResults);
				searcher.search(query, col);

				int startIdx = 0;
				int endIdx = col.getTotalHits();
				if ( tidbitCriteria.getPaginationCriteria() != null ) {
					results.setPagination(tidbitCriteria.getPaginationCriteria());
					int pageOffset = (tidbitCriteria.getPaginationCriteria().getPageOffset() == null ? 0
							: tidbitCriteria.getPaginationCriteria().getPageOffset().intValue());
					int pageSize = (tidbitCriteria.getPaginationCriteria().getPageSize() == null ? 0
							: tidbitCriteria.getPaginationCriteria().getPageSize().intValue());
					startIdx = pageOffset * pageSize;
					if ( startIdx >= col.getTotalHits() ) {
						startIdx = 0;
					}
					results.setIsPartialResult(!(startIdx == 0 && endIdx < pageSize));
					endIdx = startIdx + pageSize;
				} else {
					results.setIsPartialResult(false);
				}

				results.setTotalResults(Long.valueOf(col.getTotalHits()));
				List<?> matches = lucene.build(tidbitIndexType, col, startIdx, endIdx);
				results.setReturnedResults(Long.valueOf(matches.size()));
				for ( Object match : matches ) {
					if ( Tidbit.class.isAssignableFrom(match.getClass()) ) {
						results.getTidbit().add((Tidbit) match);
					}
				}
			}
		});

		return results;
	}

	@Override
	public void indexTidbit(Long tidbitId) {
		lucene.indexObjectById(tidbitIndexType, tidbitId);
	}

	@Override
	public void deleteTidbit(Long tidbitId) {
		lucene.deleteObjectById(getTidbitIndexType(), tidbitId);
	}

	@Override
	public void reindexTidbits() {
		lucene.reindex(getTidbitIndexType());
	}

	/**
	 * @return the lucene
	 */
	public LuceneService getLucene() {
		return lucene;
	}

	/**
	 * @param lucene
	 *        the lucene to set
	 */
	public void setLucene(LuceneService lucene) {
		this.lucene = lucene;
	}

	/**
	 * @return the messages
	 */
	public MessageSource getMessages() {
		return messages;
	}

	/**
	 * @param messages
	 *        the messages to set
	 */
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	/**
	 * @return the tidbitIndexType
	 */
	public String getTidbitIndexType() {
		return tidbitIndexType;
	}

	/**
	 * @param tidbitIndexType
	 *        the tidbitIndexType to set
	 */
	public void setTidbitIndexType(String tidbitIndexType) {
		this.tidbitIndexType = tidbitIndexType;
	}

	/**
	 * @return the domainObjectFactory
	 */
	public DomainObjectFactory getDomainObjectFactory() {
		return domainObjectFactory;
	}

	/**
	 * @param domainObjectFactory
	 *        the domainObjectFactory to set
	 */
	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}

	/**
	 * @return the maxSearchResults
	 */
	public int getMaxSearchResults() {
		return maxSearchResults;
	}

	/**
	 * @param maxSearchResults
	 *        the maxSearchResults to set
	 */
	public void setMaxSearchResults(int maxSearchResults) {
		this.maxSearchResults = maxSearchResults;
	}

}
