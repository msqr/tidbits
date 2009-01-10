/* ===================================================================
 * TidbitsBizImpl.java
 * 
 * Created Jul 2, 2006 8:22:55 PM
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

package magoffin.matt.tidbits.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glowacki.CalendarParser;
import org.glowacki.CalendarParserException;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import com.Ostermiller.util.CSVParser;

/**
 * Implementation of TidbtsBiz API.
 * 
 * @author matt.magoffin
 * @version $Revision$ $Date$
 */
public class TidbitsBizImpl implements TidbitsBiz {

	private TidbitDao tidbitDao;
	private TidbitKindDao tidbitKindDao;
	private MessageSource messages;
	private DomainObjectFactory domainObjectFactory;
	
	private final Log log = LogFactory.getLog(TidbitsBizImpl.class);
	
	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#findTidbits(magoffin.matt.tidbits.biz.TidbitSearchCriteria)
	 */
	public SearchResults findTidbits(TidbitSearchCriteria searchCriteria) {
		switch ( searchCriteria.getSearchType() ) {
			case FOR_TEMPLATE:
				return findTidbitsForTemplate(searchCriteria);
				
			// TODO case FOR_INDEX:
				
			default:
				throw new UnsupportedOperationException();
		}
	}

	private SearchResults findTidbitsForTemplate(
			TidbitSearchCriteria searchCriteria) {
		Tidbit template = searchCriteria.getTidbitTemplate();
		if ( template == null ) {
			// find all
			return tidbitDao.getAllTidbits(
					searchCriteria.getPaginationCriteria());
		}
		// TODO
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#parseCsvData(java.io.InputStream)
	 */
	public List<Tidbit> parseCsvData(InputStream input) {
		List<Tidbit> results = new LinkedList<Tidbit>();
		CSVParser parser = new CSVParser(input);
		// look for data in form:
		// 0: tidbit kind name
		// 1: tidbit name
		// 2: tidbit data
		// 3: tidbit create date (optional)
		// 4: tidbit modify date (optional)
		// 5: tidbit comment (optional)
		// 6: tidbit kind comment (optional)
		try {
			for ( String[] line = parser.getLine(); line != null; 
				line = parser.getLine() ) {
				if ( line.length < 3 ) continue;
				if ( !StringUtils.hasText(line[0]) ) continue;
				if ( !StringUtils.hasText(line[1]) ) continue;
				TidbitKind kind = tidbitKindDao.getTidbitKindByName(line[0]);
				if ( kind == null ) {
					List<TidbitKind> kinds = tidbitKindDao.findTidbitKindsByName(
							line[0]);
					if ( kinds.size() > 0 ) {
						kind = kinds.get(0);
					} else {
						// create new TidbitKind
						kind = domainObjectFactory.newTidbitKindInstance();
						kind.setName(line[0]);
						if ( line.length > 6 && StringUtils.hasText(line[6]) ) {
							kind.setComment(line[6]);
						}
					}
				}
				
				Tidbit tidbit = domainObjectFactory.newTidbitInstance();
				tidbit.setKind(kind);
				tidbit.setName(line[1]);
				tidbit.setData(line[2]);
				if ( line.length > 3 && StringUtils.hasText(line[3]) ) {
					try {
						tidbit.setCreationDate(CalendarParser.parse(line[3]));
					} catch (CalendarParserException e) {
						throw new RuntimeException(e);
					}
				} else {
					tidbit.setCreationDate(Calendar.getInstance());
				}
				if ( line.length > 4 && StringUtils.hasText(line[4]) ) {
					try {
						tidbit.setModifyDate(CalendarParser.parse(line[4]));
					} catch (CalendarParserException e) {
						throw new RuntimeException(e);
					}
				}
				if ( line.length > 5 && StringUtils.hasText(line[5]) ) {
					tidbit.setComment(line[5]);
				}
				results.add(tidbit);
			}
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#saveTidbits(java.util.List)
	 */
	public List<Long> saveTidbits(List<Tidbit> tidbits) {
		List<Long> resultIds = new ArrayList<Long>(tidbits.size());
		for ( Tidbit tidbit : tidbits ) {
			if ( tidbit.getKind().getKindId() == null ) {
				// try to find by name, if that fails create new
				TidbitKind kind = tidbitKindDao.getTidbitKindByName(tidbit.getKind().getName());
				if ( kind == null ) {
					kind = saveTidbitKind(tidbit.getKind());
				}
				tidbit.setKind(kind);
			} else {
				tidbit.setKind(tidbitKindDao.get(tidbit.getKind().getKindId()));
			}
			resultIds.add(tidbitDao.store(tidbit));
		}
		return resultIds;
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#getTidbit(java.lang.Long)
	 */
	public Tidbit getTidbit(Long id) {
		return tidbitDao.get(id);
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#getTidbitKind(java.lang.Long)
	 */
	public TidbitKind getTidbitKind(Long id) {
		return tidbitKindDao.get(id);
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#deleteTidbit(java.lang.Long)
	 */
	public void deleteTidbit(Long id) {
		Tidbit t = tidbitDao.get(id);
		if ( t != null ) {
			tidbitDao.delete(t);
		}
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#deleteTidbitKind(java.lang.Long, java.lang.Long)
	 */
	public void deleteTidbitKind(Long id, Long reassignId) {
		TidbitKind k = tidbitKindDao.get(id);
		TidbitKind r = tidbitKindDao.get(reassignId);
		if ( k != null && r != null ) {
			int updated = tidbitDao.reassignTidbitKinds(k, r);
			if ( log.isDebugEnabled() ) {
				log.debug("Reassigned " +updated +" tidbits to kind [" +r.getName() +"]");
			}
			tidbitKindDao.delete(k);
		}
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#saveTidbit(magoffin.matt.tidbits.domain.Tidbit)
	 */
	public Tidbit saveTidbit(Tidbit tidbit) {
		prepareTidbitForStorage(tidbit);
		Long id = tidbitDao.store(tidbit);
		return tidbitDao.get(id);
	}
	
	private void prepareTidbitForStorage(Tidbit tidbit) {
		if ( tidbit.getCreationDate() == null ) {
			tidbit.setCreationDate(Calendar.getInstance());
		}
		if ( tidbit.getTidbitId() != null ) {
			tidbit.setModifyDate(Calendar.getInstance());
		}
		if ( tidbit.getKind() != null ) {
			// make sure tidbit kind is a persistant instance
			if ( tidbit.getKind().getKindId() == null ) {
				tidbit.setKind(null);
			} else {
				tidbit.setKind(tidbitKindDao.get(tidbit.getKind().getKindId()));
			}
		}
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#saveTidbitKind(magoffin.matt.tidbits.domain.TidbitKind)
	 */
	public TidbitKind saveTidbitKind(TidbitKind kind) {
		prepareTidbitKindForStorage(kind);
		Long id = tidbitKindDao.store(kind);
		return tidbitKindDao.get(id);
	}

	private void prepareTidbitKindForStorage(TidbitKind kind) {
		if ( kind.getCreationDate() == null ) {
			kind.setCreationDate(Calendar.getInstance());
		}
		if ( kind.getKindId() != null ) {
			kind.setModifyDate(Calendar.getInstance());
		}
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.tidbits.biz.TidbitsBiz#getAvailableTidbitKinds()
	 */
	public List<TidbitKind> getAvailableTidbitKinds() {
		return tidbitKindDao.getAllTidbitKinds();
	}

	/**
	 * @return Returns the tidbitDao.
	 */
	public TidbitDao getTidbitDao() {
		return tidbitDao;
	}

	/**
	 * @param tidbitDao The tidbitDao to set.
	 */
	public void setTidbitDao(TidbitDao tidbitDao) {
		this.tidbitDao = tidbitDao;
	}

	/**
	 * @return Returns the tidbitKindDao.
	 */
	public TidbitKindDao getTidbitKindDao() {
		return tidbitKindDao;
	}

	/**
	 * @param tidbitKindDao The tidbitKindDao to set.
	 */
	public void setTidbitKindDao(TidbitKindDao tidbitKindDao) {
		this.tidbitKindDao = tidbitKindDao;
	}

	/**
	 * @return Returns the messages.
	 */
	public MessageSource getMessages() {
		return messages;
	}

	/**
	 * @param messages The messages to set.
	 */
	public void setMessages(MessageSource messages) {
		this.messages = messages;
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
