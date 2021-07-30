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
 * $Id$
 * ===================================================================
 */

package magoffin.matt.tidbits.lucene;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import magoffin.matt.lucene.LuceneService;
import magoffin.matt.lucene.LuceneService.IndexSearcherOp;
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.SearchQueryException;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria;
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

	private String tidbitIndexType = IndexType.TIDBIT.toString();
	private int maxSearchResults = DEFAULT_MAX_SEARCH_RESULTS;

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
