/* ===================================================================
 * BrowserThemeResolver.java
 * 
 * Created Oct 23, 2006 2:10:14 PM
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

package magoffin.matt.tidbits.web;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.theme.AbstractThemeResolver;

/**
 * Theme resolver for based on browser detection.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class BrowserThemeResolver extends AbstractThemeResolver {
	
	private Map<Pattern, String> userAgentMap = Collections.emptyMap();
	
	private final Logger log = Logger.getLogger(getClass());

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.ThemeResolver#resolveThemeName(javax.servlet.http.HttpServletRequest)
	 */
	public String resolveThemeName(HttpServletRequest request) {
		String client = request.getHeader("user-agent");
		if ( !StringUtils.hasText(client) ) {
			log.debug("User agent not available, returning default theme.");
			return getDefaultThemeName();
		}
		for ( Pattern p : userAgentMap.keySet() ) {
			if ( p.matcher(client).find() ) {
				if ( log.isDebugEnabled() ) {
					log.debug("User agent [" +client +"] matched regex ["
							+p.toString() +"], resolving to [" 
							+userAgentMap.get(p) +"]");
				}
				return userAgentMap.get(p);
			}
		}
		if ( log.isDebugEnabled() ) {
			log.debug("User agent [" +client +"] not known, returning default theme");
		}
		return getDefaultThemeName();
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.ThemeResolver#setThemeName(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	public void setThemeName(HttpServletRequest request,
			HttpServletResponse response, String themeName) {
		throw new UnsupportedOperationException("Cannot change theme");
	}

	/**
	 * @param userAgentMap the userAgentMap to set
	 */
	public void setUserAgentMap(Map<String, String> userAgentMap) {
		Map<Pattern, String> patternMap = new LinkedHashMap<Pattern, String>();
		for ( String patStr : userAgentMap.keySet() ) {
			Pattern pat = Pattern.compile(patStr);
			patternMap.put(pat, userAgentMap.get(patStr));
		}
		this.userAgentMap = patternMap;
	}

}
