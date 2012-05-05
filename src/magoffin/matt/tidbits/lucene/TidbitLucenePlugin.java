/* ===================================================================
 * TidbitLucenePlugin.java
 * 
 * Created May 25, 2006 9:18:03 PM
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

package magoffin.matt.tidbits.lucene;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import magoffin.matt.lucene.IndexEvent;
import magoffin.matt.lucene.IndexEvent.EventType;
import magoffin.matt.lucene.IndexResults;
import magoffin.matt.lucene.LucenePlugin;
import magoffin.matt.lucene.LuceneService.IndexWriterOp;
import magoffin.matt.lucene.LuceneServiceUtils;
import magoffin.matt.lucene.SearchCriteria;
import magoffin.matt.lucene.SearchMatch;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import magoffin.matt.tidbits.domain.TidbitSearchResult;
import magoffin.matt.util.DelegatingInvocationHandler;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

/**
 * Lucene search plugin implementation for User objects.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class TidbitLucenePlugin extends AbstractLucenePlugin implements LucenePlugin {

	private TidbitDao tidbitDao = null;
	private boolean singleThreaded = false;
	private final Logger log = Logger.getLogger(TidbitLucenePlugin.class);

	/**
	 * Default constructor.
	 */
	public TidbitLucenePlugin() {
		super();
		setIndexType(IndexType.TIDBIT.toString());
	}

	@Override
	public Object getIdForObject(Object object) {
		if ( object instanceof Tidbit ) {
			return ((Tidbit) object).getId();
		}
		return null;
	}

	@Override
	public void index(Object objectId, IndexWriter writer) {
		Tidbit tidbit = tidbitDao.get((Long)objectId);
		indexTidbit(tidbit, writer);
	}

	@Override
	public void indexObject(Object object, IndexWriter writer) {
		indexTidbit((Tidbit)object, writer);
	}
	
	private void doReindexAll(IndexWriter writer, TidbitIndexResults indexResults) {
		SearchResults results = tidbitDao.getAllTidbits(null);
		for ( TidbitSearchResult tidbit : results.getTidbit() ) {
			List<Object> errors = indexTidbit(tidbit, writer);
			if ( errors != null && errors.size() > 0 ) {
				indexResults.errors.put(tidbit.getId(), errors.get(0).toString());
			}
		}
	}
	
	@Override
	public IndexResults reindex() {
		final TidbitIndexResults results = new TidbitIndexResults();
		if ( this.singleThreaded ) {
			try {
				getLucene().doIndexWriterOp(getIndexType(), true, false, true, new IndexWriterOp() {
					@Override
					public void doWriterOp(String type, IndexWriter writer) {
						doReindexAll(writer, results);
					}
				});
			} finally {
				results.finished = true;
			}
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						getLucene().doIndexWriterOp(getIndexType(), true, false, true, new IndexWriterOp() {
							@Override
							public void doWriterOp(String type, IndexWriter writer) {
								doReindexAll(writer, results);
							}
						});
					} finally {
						results.finished = true;
					}
				}
			}, "TidbitsReindex").start();
		}
		return results;
	}

	@Override
	public IndexResults reindex(SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void index(Iterable<?> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public magoffin.matt.lucene.SearchResults find(SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SearchMatch> search(SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getNativeQuery(SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SearchMatch build(Document doc) {
		Tidbit tidbit = getDomainObjectFactory().newTidbitInstance();
		tidbit.setId(Long.valueOf(doc.get(IndexField.ITEM_ID.getFieldName())));
		tidbit.setComment(doc.get(IndexField.ITEM_COMMENT.getFieldName()));
		tidbit.setData(doc.get(IndexField.ITEM_DATA.getFieldName()));
		tidbit.setName(doc.get(IndexField.ITEM_NAME.getFieldName()));
		tidbit.setCreatedBy(doc.get(IndexField.CREATED_BY.getFieldName()));
		
		TidbitKind kind = getDomainObjectFactory().newTidbitKindInstance();
		kind.setName(doc.get(IndexField.KIND_NAME.getFieldName()));
		tidbit.setKind(kind);
		
		String dateStr = doc.get(IndexField.CREATED_DATE.getFieldName());
		if ( dateStr != null ) {
			Date d = getLucene().parseDate(dateStr);
			if ( d != null ) {
				GregorianCalendar c = new GregorianCalendar(getLucene().getIndexTimeZone());
				c.setTime(d);
				XMLGregorianCalendar cal;
				try {
					cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				} catch (DatatypeConfigurationException e) {
					throw new RuntimeException(e);
				}
				tidbit.setCreationDate(cal);
			}
		}
		
		dateStr = doc.get(IndexField.MODIFIED_DATE.getFieldName());
		if ( dateStr != null ) {
			Date d = getLucene().parseDate(dateStr);
			if ( d != null ) {
				GregorianCalendar c = new GregorianCalendar(getLucene().getIndexTimeZone());
				c.setTime(d);
				XMLGregorianCalendar cal;
				try {
					cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				} catch (DatatypeConfigurationException e) {
					throw new RuntimeException(e);
				}
				tidbit.setModifyDate(cal);
			}
		}
		
		if ( SearchMatch.class.isAssignableFrom(tidbit.getClass()) ) {
			return (SearchMatch)tidbit;
		}
		SearchMatch match = (SearchMatch)DelegatingInvocationHandler.wrapObject(
				tidbit, SearchMatch.class);
		return match;
	}

	private List<Object> indexTidbit(Tidbit tidbit, IndexWriter writer) {
		List<Object> errors = new LinkedList<Object>();
		
		if ( tidbit == null || tidbit.getId() == null ) {
			// don't bother trying to index null or empty user
			String msg = "Null Tidbit passed to indexUser()... perhaps not available in transaction?";
			log.debug(msg);
			errors.add(msg);
			return errors;
		}
		
		if ( log.isDebugEnabled() ) {
			log.debug("Indexing Tidbit " + tidbit.getId()
					+" (" +tidbit.getName() +", " 
					+(tidbit.getKind() != null ? tidbit.getKind().getName() : "?") 
					+")");
		}
		
		// index general text for searches
		StringBuilder generalText = new StringBuilder();
		
		Document doc = new Document();
		doc.add(new Field(IndexField.ITEM_ID.getFieldName(), tidbit.getId().toString(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		if ( tidbit.getName() != null ) {
			doc.add(new Field(IndexField.ITEM_NAME.getFieldName(), tidbit.getName(), 
					Field.Store.YES, Field.Index.ANALYZED));
			generalText.append(tidbit.getName()).append(" ");
		}
		
		if ( tidbit.getData() != null ) {
			doc.add(new Field(IndexField.ITEM_DATA.getFieldName(), tidbit.getData(), 
					Field.Store.YES, Field.Index.ANALYZED));
			generalText.append(tidbit.getData()).append(" ");
		}
		
		if ( tidbit.getComment() != null ) {
			doc.add(new Field(IndexField.ITEM_COMMENT.getFieldName(), tidbit.getComment(), 
					Field.Store.YES, Field.Index.ANALYZED));
			generalText.append(tidbit.getComment()).append(" ");
		}
		
		if ( tidbit.getCreatedBy() != null ) {
			doc.add(new Field(IndexField.CREATED_BY.getFieldName(), tidbit.getCreatedBy(), 
					Field.Store.YES, Field.Index.ANALYZED));
			generalText.append(tidbit.getCreatedBy()).append(" ");
		}
		
		if ( tidbit.getCreationDate() != null ) {
			String dateStr = getLucene().formatDateToDay(tidbit.getCreationDate().toGregorianCalendar().getTime());
			doc.add(new Field(IndexField.CREATED_DATE.getFieldName(), dateStr,
					Field.Store.YES, Field.Index.ANALYZED));
		}
		
		if ( tidbit.getModifyDate() != null ) {
			String dateStr = getLucene().formatDateToDay(tidbit.getModifyDate().toGregorianCalendar().getTime());
			doc.add(new Field(IndexField.MODIFIED_DATE.getFieldName(), dateStr,
					Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
		
		if ( tidbit.getKind() != null ) {
			doc.add(new Field(IndexField.KIND_NAME.getFieldName(), tidbit.getKind().getName(), 
					Field.Store.YES, Field.Index.ANALYZED));
			generalText.append(tidbit.getKind().getName()).append(" ");
		}
		
		if ( generalText.length() > 0 ) {
			doc.add(new Field(IndexField.GENERAL_TEXT.getFieldName(), generalText.toString(), 
					Field.Store.NO, Field.Index.ANALYZED));
		}
		
		try {
			writer.addDocument(doc);
			LuceneServiceUtils.publishIndexEvent(new IndexEvent(tidbit,EventType.UPDATE, getIndexType()), 
					getIndexEventListeners());
		} catch ( IOException e ) {
			throw new RuntimeException("IOException adding user to index",e);
		}
		
		return errors;
	}
	
	private final class TidbitIndexResults implements IndexResults {

		private final int count = 0;
		private final Map<Long, String> errors = new LinkedHashMap<Long, String>();
		private boolean finished = false;

		@Override
		public Map<? extends Serializable, String> getErrors() {
			return errors;
		}

		@Override
		public int getNumIndexed() {
			return count - errors.size();
		}

		@Override
		public int getNumProcessed() {
			return count;
		}

		@Override
		public boolean isFinished() {
			return finished;
		}

	}

	/**
	 * @return the singleThreaded
	 */
	public boolean isSingleThreaded() {
		return singleThreaded;
	}
	
	/**
	 * @param singleThreaded the singleThreaded to set
	 */
	public void setSingleThreaded(boolean singleThreaded) {
		this.singleThreaded = singleThreaded;
	}
	
	/**
	 * @return the tidbitDao
	 */
	public TidbitDao getTidbitDao() {
		return tidbitDao;
	}
	
	/**
	 * @param tidbitDao the tidbitDao to set
	 */
	public void setTidbitDao(TidbitDao tidbitDao) {
		this.tidbitDao = tidbitDao;
	}

}
