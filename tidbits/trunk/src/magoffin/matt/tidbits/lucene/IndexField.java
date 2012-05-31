/* ===================================================================
 * IndexField.java
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

/**
 * An enumeration of Tidbits Lucene index fields.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public enum IndexField {
	
	/** The index field for an item's unique ID. */
	ITEM_ID,
	
	/** The index field for an item's name. */
	ITEM_NAME,
	
	/** The index field for an item's comment. */
	ITEM_COMMENT,
	
	/** The index field for an item's data. */
	ITEM_DATA,
	
	/** An index key appropriate for this item. */
	ITEM_INDEX_KEY,
	
	/** The Tidbit kind. */
	KIND_NAME,

	/** The Tidbit kind ID. */
	KIND_ID,

	/** The index field for an item's creation date. */
	CREATED_DATE,
	
	/** The created by login. */
	CREATED_BY,

	/** An item's modified date. */
	MODIFIED_DATE,
	
	/** The index field (not stored) for tokenized free-form text. */
	GENERAL_TEXT;

	/**
	 * Get a Lucene field name for this enum.
	 * @return field name
	 * @see #fromFieldName(String)
	 */
	public String getFieldName() {
		switch ( this ) {
			case CREATED_BY: return "cby";
			case CREATED_DATE: return "cdate";
			case GENERAL_TEXT: return "Gtext";
			case ITEM_COMMENT: return "cmnt";
			case ITEM_DATA: return "data";
			case ITEM_ID: return "id";
			case ITEM_INDEX_KEY: return "idx";
			case ITEM_NAME: return "name";
			case KIND_NAME: return "kname";
			case KIND_ID:
				return "kid";
			case MODIFIED_DATE: return "mdate";
		}
		throw new AssertionError(this);
	}
	
	/**
	 * Get an IndexField from a Lucene field name.
	 * @param field field name
	 * @return the IndexField
	 * @throws IllegalArgumentException if the field name is not supported
	 * @see IndexField#getFieldName()
	 */
	public static IndexField fromFieldName(String field) {
		if ( "cby".equals(field) ) return CREATED_BY;
		if ( "cdate".equals(field) ) return CREATED_DATE;
		if ( "cmnt".equals(field) ) return ITEM_COMMENT;
		if ( "data".equals(field) ) return ITEM_DATA;
		if ( "Gtext".equals(field) ) return GENERAL_TEXT;
		if ( "id".equals(field) ) return ITEM_ID;
		if ( "idx".equals(field) ) return ITEM_INDEX_KEY;
		if ( "kname".equals(field) ) return KIND_NAME;
		if ( "kid".equals(field) )
			return KIND_ID;
		if ( "name".equals(field) ) return ITEM_NAME;
		if ( "mdate".equals(field) ) return MODIFIED_DATE;
		throw new IllegalArgumentException(field);
	}
	
}
