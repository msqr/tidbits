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
 */

package magoffin.matt.tidbits.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.glowacki.CalendarParser;
import org.glowacki.CalendarParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.CSVPrinter;
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.tidbits.dao.ExportCallback;
import magoffin.matt.tidbits.dao.TidbitDao;
import magoffin.matt.tidbits.dao.TidbitKindDao;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;

/**
 * Implementation of TidbtsBiz API.
 * 
 * @author matt
 * @version 2.0
 */
@Service
public class TidbitsBizImpl implements TidbitsBiz {

	@Autowired
	private TidbitDao tidbitDao;

	@Autowired
	private TidbitKindDao tidbitKindDao;

	@Autowired
	private MessageSource messages;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	private final Logger log = LoggerFactory.getLogger(TidbitsBizImpl.class);

	private static final Pattern ISO_DT_SEP_PAT = Pattern.compile("(\\d{2})T(\\d{2})");

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public SearchResults findTidbits(TidbitSearchCriteria searchCriteria) {
		switch (searchCriteria.getSearchType()) {
			case FOR_TEMPLATE:
				return findTidbitsForTemplate(searchCriteria);

			default:
				throw new UnsupportedOperationException();
		}
	}

	private SearchResults findTidbitsForTemplate(TidbitSearchCriteria searchCriteria) {
		Tidbit template = searchCriteria.getTidbitTemplate();
		if ( template == null ) {
			// find all
			return tidbitDao.getAllTidbits(searchCriteria.getPaginationCriteria());
		}
		throw new UnsupportedOperationException();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Tidbit> parseCsvData(InputStream input) {
		List<Tidbit> results = new LinkedList<Tidbit>();
		DatatypeFactory df;
		try {
			df = DatatypeFactory.newInstance();
		} catch ( DatatypeConfigurationException e ) {
			throw new RuntimeException(e);
		}
		CSVParser parser = new CSVParser(input);
		// look for data in form:
		// 0: tidbit name
		// 1: tidbit kind name
		// 2: tidbit data
		// 3: tidbit create date (optional)
		// 4: tidbit modify date (optional)
		// 5: tidbit comment (optional)
		// 6: tidbit kind comment (optional)
		try {
			for ( String[] line = parser.getLine(); line != null; line = parser.getLine() ) {
				if ( line.length < 3 )
					continue;
				if ( !StringUtils.hasText(line[0]) )
					continue;
				if ( !StringUtils.hasText(line[1]) )
					continue;
				TidbitKind kind = tidbitKindDao.getTidbitKindByName(line[0]);
				if ( kind == null ) {
					List<TidbitKind> kinds = tidbitKindDao.findTidbitKindsByName(line[1]);
					if ( kinds.size() > 0 ) {
						kind = kinds.get(0);
					} else {
						// create new TidbitKind
						kind = domainObjectFactory.newTidbitKindInstance();
						kind.setName(line[1]);
						if ( line.length > 6 && StringUtils.hasText(line[6]) ) {
							kind.setComment(line[6]);
						}
					}
				}

				Tidbit tidbit = domainObjectFactory.newTidbitInstance();
				tidbit.setKind(kind);
				tidbit.setName(line[0]);
				tidbit.setData(line[2]);
				if ( line.length > 3 && StringUtils.hasText(line[3]) ) {
					tidbit.setCreationDate(parseDateString(df, line[3]));
				} else {
					tidbit.setCreationDate(df.newXMLGregorianCalendar(new GregorianCalendar()));
				}
				if ( line.length > 4 && StringUtils.hasText(line[4]) ) {
					tidbit.setModifyDate(parseDateString(df, line[4]));
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

	private XMLGregorianCalendar parseDateString(DatatypeFactory df, String dtStr) {
		try {
			GregorianCalendar gc = new GregorianCalendar();

			// CalendarParser doesn't handle the 'T' date/time sep, so strip that
			Matcher matcher = ISO_DT_SEP_PAT.matcher(dtStr);
			if ( matcher.find() ) {
				dtStr = matcher.replaceFirst("$1 $2");
			}
			if ( dtStr.endsWith("Z") ) {
				dtStr = dtStr.substring(0, dtStr.length() - 1);
			}
			gc.setTimeInMillis(CalendarParser.parse(dtStr).getTimeInMillis());
			return df.newXMLGregorianCalendar(gc);
		} catch ( CalendarParserException e ) {
			throw new RuntimeException(e);
		}

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public void exportCsvData(OutputStream out) throws IOException {
		final CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out, "UTF-8"));
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		tidbitDao.exportAllTidbits(new ExportCallback() {

			@Override
			public boolean handleRow(Object[] data, int row) {
				printer.print((String) data[0]);
				printer.print((String) data[1]);
				printer.print((String) data[2]);
				printer.print(sdf.format((Date) data[3]));
				printer.print(data[4] == null ? "" : sdf.format((Date) data[4]));
				printer.print((String) data[5]);
				printer.print((String) data[6]);
				printer.print(data[7].toString());
				printer.print((String) data[8]);
				printer.print(data[9].toString());
				printer.println();
				return true;
			}
		});
		printer.flush();
		printer.close();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Long> saveTidbits(List<Tidbit> tidbits) {
		List<Long> resultIds = new ArrayList<Long>(tidbits.size());
		for ( Tidbit tidbit : tidbits ) {
			if ( tidbit.getKind().getId() == null ) {
				// try to find by name, if that fails create new
				TidbitKind kind = tidbitKindDao.getTidbitKindByName(tidbit.getKind().getName());
				if ( kind == null ) {
					kind = saveTidbitKind(tidbit.getKind());
				}
				tidbit.setKind(kind);
			} else {
				tidbit.setKind(tidbitKindDao.get(tidbit.getKind().getId()));
			}
			resultIds.add(tidbitDao.store(tidbit));
		}
		return resultIds;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Tidbit getTidbit(Long id) {
		return tidbitDao.get(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteTidbit(Long id) {
		Tidbit t = tidbitDao.get(id);
		if ( t != null ) {
			tidbitDao.delete(t);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteTidbitKind(Long id, Long reassignId) {
		if ( id.equals(reassignId) ) {
			throw new IllegalArgumentException("Reassign ID must not be the same as the ID to delete.");
		}
		TidbitKind k = tidbitKindDao.get(id);
		TidbitKind r = tidbitKindDao.get(reassignId);
		if ( k != null && r != null ) {
			int updated = tidbitDao.reassignTidbitKinds(k, r);
			if ( log.isDebugEnabled() ) {
				log.debug("Reassigned " + updated + " tidbits to kind [" + r.getName() + "]");
			}

			// re-load k because reassign flushes
			k = tidbitKindDao.get(id);
			tidbitKindDao.delete(k);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Tidbit saveTidbit(Tidbit tidbit) {
		prepareTidbitForStorage(tidbit);
		Long id = tidbitDao.store(tidbit);
		return tidbitDao.get(id);
	}

	private void prepareTidbitForStorage(Tidbit tidbit) {
		DatatypeFactory df;
		try {
			df = DatatypeFactory.newInstance();
		} catch ( DatatypeConfigurationException e ) {
			throw new RuntimeException(e);
		}
		if ( tidbit.getCreationDate() == null ) {
			tidbit.setCreationDate(df.newXMLGregorianCalendar(new GregorianCalendar()));
		}
		if ( tidbit.getId() != null ) {
			tidbit.setModifyDate(df.newXMLGregorianCalendar(new GregorianCalendar()));
		}
		if ( tidbit.getKind() != null ) {
			// make sure tidbit kind is a persistant instance
			if ( tidbit.getKind().getId() == null ) {
				tidbit.setKind(null);
			} else {
				tidbit.setKind(tidbitKindDao.get(tidbit.getKind().getId()));
			}
		}
		if ( tidbit.getComment() != null && tidbit.getComment().length() == 0 ) {
			// set to NULL
			tidbit.setComment(null);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TidbitKind saveTidbitKind(TidbitKind kind) {
		prepareTidbitKindForStorage(kind);
		Long id = tidbitKindDao.store(kind);
		return tidbitKindDao.get(id);
	}

	private void prepareTidbitKindForStorage(TidbitKind kind) {
		DatatypeFactory df;
		try {
			df = DatatypeFactory.newInstance();
		} catch ( DatatypeConfigurationException e ) {
			throw new RuntimeException(e);
		}
		if ( kind.getCreationDate() == null ) {
			kind.setCreationDate(df.newXMLGregorianCalendar(new GregorianCalendar()));
		}
		if ( kind.getId() != null ) {
			kind.setModifyDate(df.newXMLGregorianCalendar(new GregorianCalendar()));
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
	 * @param tidbitDao
	 *        The tidbitDao to set.
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
	 * @param tidbitKindDao
	 *        The tidbitKindDao to set.
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
	 * @param messages
	 *        The messages to set.
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
	 * @param domainObjectFactory
	 *        The domainObjectFactory to set.
	 */
	public void setDomainObjectFactory(DomainObjectFactory domainObjectFactory) {
		this.domainObjectFactory = domainObjectFactory;
	}

}
