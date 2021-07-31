/* ===================================================================
 * StandardTidbitsAnalyzer.java
 * 
 * Created May 26, 2006 4:46:30 PM
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

import java.io.Reader;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import magoffin.matt.lucene.KeyTokenizer;

/**
 * Standard implementation of Analyzer for Tidbits.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version 1.1
 */
public class StandardTidbitsAnalyzer extends Analyzer {

	private String snowballStemmerName = "English";
	private Set<String> stopWords = null;
	private int indexKeyLength = 1;

	@SuppressWarnings("deprecation")
	@Override
	public TokenStream tokenStream(String field, Reader reader) {
		TokenStream result = null;

		IndexField idxField = null;
		try {
			idxField = IndexField.fromFieldName(field);
		} catch ( Exception e ) {
			// ignore and fallback to default
		}

		if ( idxField == null ) {
			return standardFilters(reader);
		}

		switch (idxField) {
			case ITEM_ID:
				result = new KeywordTokenizer(reader);
				break;

			case ITEM_INDEX_KEY:
				result = new KeyTokenizer(reader, this.indexKeyLength);
				result = new LowerCaseFilter(result);
				break;

			case ITEM_NAME:
				result = standardFilters(reader);
				if ( this.stopWords == null ) {
					result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS);
				} else {
					result = new StopFilter(result, this.stopWords);
				}
				result = new SnowballFilter(result, snowballStemmerName);
				break;

			default:
				result = standardFilters(reader);
		}

		return result;
	}

	private TokenStream standardFilters(Reader reader) {
		@SuppressWarnings("deprecation")
		TokenStream result = new StandardTokenizer(reader);
		result = new StandardFilter(result);
		result = new LowerCaseFilter(result);
		return result;
	}

	/**
	 * @return the snowballStemmerName
	 */
	public String getSnowballStemmerName() {
		return snowballStemmerName;
	}

	/**
	 * @param snowballStemmerName
	 *        the snowballStemmerName to set
	 */
	public void setSnowballStemmerName(String snowballStemmerName) {
		this.snowballStemmerName = snowballStemmerName;
	}

	/**
	 * @return the stopWords
	 */
	public Set<String> getStopWords() {
		return stopWords;
	}

	/**
	 * @param stopWords
	 *        the stopWords to set
	 */
	public void setStopWords(Set<String> stopWords) {
		this.stopWords = stopWords;
	}

	/**
	 * @return the indexKeyLength
	 */
	public int getIndexKeyLength() {
		return indexKeyLength;
	}

	/**
	 * @param indexKeyLength
	 *        the indexKeyLength to set
	 */
	public void setIndexKeyLength(int indexKeyLength) {
		this.indexKeyLength = indexKeyLength;
	}

}
