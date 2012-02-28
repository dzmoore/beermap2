package com.spacepocalypse.beermap2.web;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.beermap2.service.IBeerService;
import com.spacepocalypse.util.Conca;

@Controller
@RequestMapping("/search")
public class SearchController {
	private IBeerService beerSvc;
	private Logger log4jLogger;
	
	@Autowired
	public SearchController(IBeerService beerService) {
		this.beerSvc = beerService;
		this.log4jLogger = Logger.getLogger(getClass());
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String setUpSearchForm(Model model, HttpSession session) {
		if (log4jLogger.isTraceEnabled()) {
			log4jLogger.trace(Conca.t("search; model attrs: {", model.asMap().toString(), "}"));
		}
		
		return "search";
	}	
	
	@RequestMapping(method = RequestMethod.POST)
	public String submitBeerSearch(@RequestParam("beerName") String beerName, HttpSession session) {
		
		List<MappedBeer> beers = Collections.emptyList();
		
		if (beerName != null) {
			beers = beerSvc.findAllBeers(Conca.t("%", beerName, "%"));
		}
		
		session.setAttribute(Constants.KEY_MAPPED_BEERS, beers);
		return "redirect:search";
	}
}
