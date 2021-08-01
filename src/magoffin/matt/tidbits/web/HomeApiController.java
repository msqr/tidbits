/* ===================================================================
 * HomeApiController.java
 * 
 * Created 1/08/2021 7:30:15 PM
 * 
 * Copyright (c) 2021 Matt Magoffin.
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

package magoffin.matt.tidbits.web;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import magoffin.matt.tidbits.biz.AuthorizationException;
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria.TidbitSearchType;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import magoffin.matt.tidbits.domain.UiModel;
import magoffin.matt.tidbits.support.BasicTidbitSearchCriteria;
import magoffin.matt.xweb.util.XwebConstants;

/**
 * Home API controller.
 *
 * @author matt
 * @version 1.0
 */
@Controller
public class HomeApiController {

	private static final Long DEFAULT_MAX_RESULTS = 25L;

	private static final Logger log = LoggerFactory.getLogger(HomeApiController.class);

	@Autowired
	private TidbitsBiz tidbitsBiz;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	/**
	 * Handle an {@link AuthorizationException}.
	 * 
	 * @param e
	 *        the exception
	 * @return an error response object
	 */
	@ExceptionHandler(AuthorizationException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public Object handleAuthorizationException(AuthorizationException e) {
		log.debug("AuthorizationException in {} controller: {}", getClass().getSimpleName(),
				e.getMessage());
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("success", Boolean.FALSE);
		response.put("message", "Not authorized.");
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/search.json")
	public ModelAndView search(BasicTidbitSearchCriteria criteria) {
		if ( StringUtils.hasText(criteria.getQuery()) ) {
			criteria.setSearchType(TidbitSearchType.FOR_QUERY);
		} else {
			criteria.setSearchType(TidbitSearchType.FOR_TEMPLATE);
		}
		if ( criteria.getPaginationCriteria() == null ) {
			criteria.setPaginationCriteria(new PaginationCriteria());
		}
		if ( criteria.getPaginationCriteria().getPageSize() == null ) {
			criteria.getPaginationCriteria().setPageSize(DEFAULT_MAX_RESULTS);
		}
		if ( criteria.getPaginationCriteria().getPageOffset() == null ) {
			criteria.getPaginationCriteria().setPageOffset(0L);
		}
		UiModel model = new UiModel();
		model.setSearchResults(tidbitsBiz.findTidbits(criteria));

		return new ModelAndView("json-search-results", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/kinds.json")
	public ModelAndView kinds() {
		UiModel model = new UiModel();
		model.setKind(tidbitsBiz.getAvailableTidbitKinds());
		return new ModelAndView("json-kinds", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/saveTidbit.do")
	public ModelAndView saveTidbit(Tidbit form) {
		Tidbit result = tidbitsBiz.saveTidbit(form);
		UiModel model = new UiModel();
		model.setTidbit(Collections.singletonList(result));
		return new ModelAndView("json-search-results", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/deleteTidbit.do")
	public ModelAndView deleteTidbit(Long id) {
		tidbitsBiz.deleteTidbit(id);
		return new ModelAndView("json-service-result");
	}

	@RequestMapping(method = RequestMethod.POST, value = "/saveKind.do")
	public ModelAndView saveTidbitKind(TidbitKind form) {
		TidbitKind kind = tidbitsBiz.saveTidbitKind(form);
		UiModel model = new UiModel();
		model.getKind().add(kind);
		return new ModelAndView("json-kinds", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/deleteKind.do")
	public ModelAndView saveTidbitKind(@RequestParam("id") Long kindId,
			@RequestParam("reassign") Long reassignId) {
		tidbitsBiz.deleteTidbitKind(kindId, reassignId);
		return new ModelAndView("json-service-result");
	}

	@RequestMapping(method = RequestMethod.GET, value = "/messages.json")
	public ModelAndView messages() {
		UiModel model = new UiModel();
		return new ModelAndView("json-messages", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
	}

}
