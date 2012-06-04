/* ===================================================================
 * HomeController.java
 * 
 * Created May 8, 2012 7:04:16 PM
 * 
 * Copyright (c) 2012 Matt Magoffin.
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
import magoffin.matt.tidbits.biz.DomainObjectFactory;
import magoffin.matt.tidbits.biz.TidbitSearchCriteria.TidbitSearchType;
import magoffin.matt.tidbits.biz.TidbitsBiz;
import magoffin.matt.tidbits.domain.PaginationCriteria;
import magoffin.matt.tidbits.domain.Tidbit;
import magoffin.matt.tidbits.domain.TidbitKind;
import magoffin.matt.tidbits.domain.UiModel;
import magoffin.matt.tidbits.support.BasicTidbitSearchCriteria;
import magoffin.matt.xweb.util.XwebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for main home screen.
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
@Controller
public class HomeController {

	private static final Long DEFAULT_MAX_RESULTS = 25L;

	@Autowired
	private TidbitsBiz tidbitsBiz;

	@Autowired
	private DomainObjectFactory domainObjectFactory;

	@RequestMapping(method = RequestMethod.GET, value = "/home.do")
	public ModelAndView home() {
		UiModel model = new UiModel();
		return new ModelAndView("home", XwebConstants.DEFALUT_MODEL_OBJECT,
				domainObjectFactory.newRootElement(model));
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
