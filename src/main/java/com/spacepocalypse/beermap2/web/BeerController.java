/**
 * 
 */
package com.spacepocalypse.beermap2.web;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.service.BeerService;

/**
 * @author dylan
 *
 */
@Controller
public class BeerController {
	private BeerService beerSvc;
	
	@Autowired
	public BeerController(BeerService beerService) {
		this.beerSvc = beerService;
	}
	
	@RequestMapping(value="/")
	public String index() {
		return "index";
	}
	
	@RequestMapping(value = "/welcome", method=RequestMethod.GET)
	public String welcome(Model model) {
		model.addAttribute("today", new Date());
		return "welcome";
	}
	
	@RequestMapping(value = "/search", method=RequestMethod.GET)
	public void setupSearchForm(Model model) {
	}
	
	@RequestMapping(value = "/search", method=RequestMethod.POST)
	public String submitBeerSearch(@RequestParam("beerName") String beerName, Model model) {
		List<MappedBeer> beers = Collections.emptyList();
		
		if (beerName != null) {
			beers = beerSvc.findAllBeers(beerName);
		}
		
		model.addAttribute("beers", beers);
		
		return "search";
	}
}
