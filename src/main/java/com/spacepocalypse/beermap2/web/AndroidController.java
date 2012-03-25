package com.spacepocalypse.beermap2.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedValue;
import com.spacepocalypse.beermap2.domain.json.JSONArray;
import com.spacepocalypse.beermap2.domain.json.JSONException;
import com.spacepocalypse.beermap2.domain.json.JSONObject;
import com.spacepocalypse.beermap2.service.BeerSearchEngine;
import com.spacepocalypse.beermap2.service.BeerService;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.beermap2.service.IBeerService;
import com.spacepocalypse.beermap2.service.LoginService;
import com.spacepocalypse.beermap2.service.LoginService.AuthData;
import com.spacepocalypse.beermap2.service.LoginService.AuthState;
import com.spacepocalypse.util.Conca;
import com.spacepocalypse.util.StrUtl;

@Controller
public class AndroidController {
	private IBeerService beerService;
	private LoginService loginService;
	private Logger log4jLogger;
	
	@Autowired
	public AndroidController(IBeerService beerService, LoginService loginService) {
		log4jLogger = Logger.getLogger(getClass());
		this.beerService = beerService;
		this.loginService = loginService;
	}
	
	@RequestMapping(value = {"/android", "/android/"}, method=RequestMethod.GET)
	public String androidWelcome(Model model) {
		return "androidwelcome";
	}
	
	@RequestMapping(value = {"/android/beersearch/", "/android/beersearch"}/*, method=RequestMethod.POST*/)
	public String getBeersByNameJSON(@RequestParam(Constants.KEY_QUERY) String query, Model model) {
	    log4jLogger.trace(Conca.t("beer search request. query=[", query, "]"));
	    
		List<MappedBeer> results = beerService.findAllBeers(query);
		
		convertToJSONAndAddToModel(model, results);
		
		log4jLogger.info(StrUtl.trunc(Conca.t("Returning beers:[", results.toString(), "]"), 200));
		
		return Constants.BM4A_JSON_RESULT;
	}
	
	@RequestMapping(value = {"/android/beerupdate/", "/android/beerupdate"}/*, method=RequestMethod.POST*/)
	public String updateBeer(@RequestParam(Constants.KEY_MAPPED_BEER) String beerToUpdate, Model model) {
		final boolean result = beerService.updateBeer(beerToUpdate);
		
		JSONObject jsonResultObj = new JSONObject();
		try {
			jsonResultObj.put(Constants.KEY_BM4A_JSON_RESULT, result);
		
		} catch (Exception e) {
			log4jLogger.error("Error putting beer update result into json object", e);
		}
		
		model.addAttribute(Constants.KEY_BM4A_JSON_RESULT, jsonResultObj.toString());
		
		Logger.getLogger(getClass()).info(Conca.t("Beer update for beer json [", beerToUpdate, "] success:[", result, "]"));
		
		return Constants.BM4A_JSON_RESULT;
	}
	
	@RequestMapping(value = {"/android/beerinsert/", "/android/beerinsert"}/*, method=RequestMethod.POST*/)
	public String insertBeer(@RequestParam(Constants.KEY_MAPPED_BEER) String beerToInsert, Model model) {
		final boolean result = beerService.insertBeer(beerToInsert);
		
		JSONObject jsonResultObj = new JSONObject();
		try {
			jsonResultObj.put(Constants.KEY_BM4A_JSON_RESULT, result);
		
		} catch (Exception e) {
			log4jLogger.error("Error putting beer update result into json object", e);
		}
		
		model.addAttribute(Constants.KEY_BM4A_JSON_RESULT, jsonResultObj.toString());
		
		Logger.getLogger(getClass()).info(Conca.t("Beer update for beer json [", beerToInsert, "] success:[", result, "]"));
		
		return Constants.BM4A_JSON_RESULT;
	}
	
	
	@RequestMapping(value = {"/android/allratingtypes/", "/android/allratingtypes"})
	public String getAllRatingTypes(Model model) {
		List<MappedValue> allRatingTypes = beerService.findAllRatingTypes();
		
		convertToJSONAndAddToModel(model, allRatingTypes);
		
		Logger.getLogger(getClass()).info(
				StrUtl.trunc(
						Conca.t("Returning rating types:[", model.asMap().toString(), "]"), 
						500
				)
		);
		
		return Constants.BM4A_JSON_RESULT;
	}
			
	
	@RequestMapping(value = {"/android/ratingsearch/", "/android/ratingsearch"}/*, method=RequestMethod.POST*/)
	public String getRatingsByNameJSON(
			@RequestParam(Constants.KEY_BEER_ID) String beerId,
			@RequestParam(Constants.QUERY_KEY_USER_ID) String userId,
			Model model) 
	{
		List<MappedBeerRating> ratings = beerService.findAllBeerRatings(beerId, userId);
		convertToJSONAndAddToModel(model, ratings);
		
		return Constants.BM4A_JSON_RESULT;
	}
	
	@RequestMapping(value = {"/android/ratingupdate/", "/android/ratingupdate"}/*, method=RequestMethod.POST*/)
	public String updateBeerRating(
			@RequestParam(Constants.KEY_MAPPED_RATING) String ratingJSON,
			Model model) 
	{
		boolean result = beerService.updateBeerRating(ratingJSON);
		
		JSONObject jsonResultObj = new JSONObject();
		try {
			jsonResultObj.put(Constants.KEY_BM4A_JSON_RESULT, result);
		
		} catch (Exception e) {
			log4jLogger.error("Error putting beer rating update result into json object", e);
		}
		
		model.addAttribute(Constants.KEY_BM4A_JSON_RESULT, jsonResultObj.toString());
		return Constants.BM4A_JSON_RESULT;
	}
	
	@RequestMapping(value = {"/android/ratinginsert/", "/android/ratinginsert"}/*, method=RequestMethod.POST*/)
	public String insertBeerRating(
			@RequestParam(Constants.KEY_MAPPED_RATING) String ratingJSON,
			Model model) 
	{
		boolean result = beerService.insertBeerRating(ratingJSON);
		
		JSONObject jsonResultObj = new JSONObject();
		try {
			jsonResultObj.put(Constants.KEY_BM4A_JSON_RESULT, result);
		
		} catch (Exception e) {
			log4jLogger.error("Error putting beer rating update result into json object", e);
		}
		
		model.addAttribute(Constants.KEY_BM4A_JSON_RESULT, jsonResultObj.toString());
		return Constants.BM4A_JSON_RESULT;
	}
	
	@RequestMapping(value = {"/android/login/", "/android/login"}/*, method=RequestMethod.POST*/)
	public String handleLogin(
			@RequestParam(Constants.KEY_USERNAME) String username, 
			@RequestParam(Constants.KEY_PASSWORD) String password, 
			Model model) 
	{
		log4jLogger.info(Conca.t("received login request of username=[", username, "] hashpass=[", password, "]"));
		
		AuthData data = loginService.authUser(username, password);
		
		log4jLogger.info(Conca.t("authUser check returned AuthData: [", data.toString(), "]"));
		
		JSONObject obj = new JSONObject();

		boolean authSuccess = (data.getState() == AuthState.SUCCESS);
		
		try {
			obj.put(Constants.RESULT_SUCCESS, authSuccess);
			
			if (authSuccess) {
				obj.put(Constants.KEY_USER, new JSONObject(data.getUser()));
				obj.put(Constants.KEY_TIMEOUT_MS, data.getAuthTimeoutMs() + System.currentTimeMillis());
				
			}
		} catch (JSONException e) {
			log4jLogger.error("JSON error occurred while attempting to construct login response.", e);
		}
		
		log4jLogger.info(Conca.t("Writing: [", obj.toString(), "]"));
		
		model.addAttribute(Constants.KEY_BM4A_JSON_RESULT, obj.toString());

		return Constants.BM4A_JSON_RESULT;
	}
	
	private static JSONArray convertToJSONArray(List<?> list) {
		final JSONArray jsonArr = new JSONArray();
		
		for (final Object ea : list) {
			jsonArr.put(new JSONObject(ea));
		}
		
		return jsonArr;
	}
	
	public void convertToJSONAndAddToModel(final Model model, final List<?> list) {
		try {
			model.addAttribute(Constants.KEY_BM4A_JSON_RESULT, convertToJSONArray(list));
			
		} catch (Exception e) {
			log4jLogger.error(
					Conca.t(
						"An error occurred while attempting to convert the list: [",
						StrUtl.trunc((list != null ? list.toString() : "NULL"), 500), 
						"] to a json array"
					),
					e
			);
		}
	}
}
