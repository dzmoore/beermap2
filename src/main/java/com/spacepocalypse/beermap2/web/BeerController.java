/**
 * 
 */
package com.spacepocalypse.beermap2.web;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.service.BeerService;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.util.Conca;

/**
 * @author dylan
 *
 */
@Controller
public class BeerController {
	private BeerService beerSvc;
	private static Logger log4jLogger = Logger.getLogger(BeerController.class);
	
	@Autowired
	public BeerController(BeerService beerService) {
		this.beerSvc = beerService;
	}
	
	@RequestMapping(value="/")
	public String index() {
		return "index";
	}
	
	@RequestMapping(value={"/m"})
    public String mobileMain() {
        return "redirect:/m/main";
    }
	
	@RequestMapping(value = "/beer", method=RequestMethod.GET)
	public String displayBeer(@RequestParam(Constants.KEY_BEER_ID) String id, Model model) {
	    int intId = Constants.INVALID_ID;
	    try {
	        intId = Integer.parseInt(id);
	        
	    } catch (Exception e) {
	        log4jLogger.error("Error parsing beer id into int[" + id + "]", e);
	    }
	    
	    MappedBeer result = new MappedBeer();
	    if (intId > Constants.INVALID_ID) {
	        result = beerSvc.findBeerById(intId);

	        if (result.getId() == Constants.INVALID_ID) {
	            model.addAttribute(Constants.RESULT, Constants.UNABLE_TO_FIND);

	        } else {
	            model.addAttribute(Constants.RESULT, result.getName());
	        } 
	    
	    } else {
	        model.addAttribute(Constants.RESULT, Constants.UNABLE_TO_FIND);
	    }
		
		model.addAttribute(Constants.KEY_MAPPED_BEER, result);
		return "beer";
	}
	
	@RequestMapping(value = "/update", method=RequestMethod.GET)
	public void setupUpdateForm(Model model) {
	}
	
}
