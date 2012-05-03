/* ===================================================================
 * SearchForm.java
 * 
 * Created Oct 23, 2006 3:43:44 PM
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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magoffin.matt.tidbits.biz.SearchQueryException;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria.TidbitSearchType;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.SearchResults;
import magoffin.matt.tidbits.domain.UiModel;
import magoffin.matt.tidbits.support.BasicTidbitSearchCriteria;
import magoffin.matt.tidbits.support.SearchCommand;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

/**
 * Search form.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class SearchForm extends AbstractForm {
	
	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		if ( "home".equals(getFormView()) ) {
			Map<String, Object> viewModel = new LinkedHashMap<String, Object>();
			handleSearch(command, errors, viewModel);
			return viewModel;
		}
		return null;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		String key = "magoffin.matt.tidbits.SEARCH_FORM:" +getCommandClass().getSimpleName();
		SearchCommand cmd = (SearchCommand)request.getSession().getAttribute(key);
		if ( cmd == null ) {
			cmd = (SearchCommand)createCommand();
			request.getSession().setAttribute(key, cmd);
		}
		return cmd;
	}

	private void handleSearch(Object command, 
			Errors errors, Map<String, Object> viewModel) {
		SearchCommand cmd = (SearchCommand)command;
		UiModel model = getDomainObjectFactory().newModelInstance();
		
		// get all available tidbits unless query specified
		BasicTidbitSearchCriteria criteria = new BasicTidbitSearchCriteria();
		if ( StringUtils.hasText(cmd.getQuery()) ) {
			criteria.setSearchType(TidbitSearchType.FOR_QUERY);
			criteria.setQuery(cmd.getQuery());
		} else {
			criteria.setSearchType(TidbitSearchType.FOR_TEMPLATE);
		}
		PaginationCriteria pagination = getDomainObjectFactory().newPaginationCriteriaInstance();
		pagination.setPageSize(Long.valueOf(cmd.getPageSize()));
		pagination.setPageOffset(Long.valueOf(cmd.getPage()));
		criteria.setPaginationCriteria(pagination);
		
		try {
			SearchResults results = getTidbitsBiz().findTidbits(criteria);
			model.setSearchResults(results);
		} catch ( SearchQueryException e ) {
			BindException be = new BindException(command, getModelObjectKey());
			ObjectError err = new ObjectError(getModelObjectKey(), 
					new String[]{"error.search.query.invalid"}, null,
					"The search query is not valid.");
			be.addError(err);
			errors.addAllErrors(be);
		}
		
		viewModel.put(getModelObjectKey(), model);
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, 
			HttpServletResponse response, Object command, 
			BindException errors) throws Exception {
		
		Map<String, Object> viewModel = new LinkedHashMap<String, Object>();
		handleSearch(command, errors, viewModel);
		return new ModelAndView(getSuccessView(), viewModel);
	}

}
