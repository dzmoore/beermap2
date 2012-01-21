package com.spacepocalypse.beermap2.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.domain.json.JSONArray;
import com.spacepocalypse.beermap2.domain.json.JSONException;
import com.spacepocalypse.beermap2.domain.json.JSONObject;
import com.spacepocalypse.beermap2.service.AndroidBeerQueryServlet;
import com.spacepocalypse.beermap2.service.BeerService;
import com.spacepocalypse.beermap2.service.LoginService;
import com.spacepocalypse.beermap2.service.LoginService.AuthData;
import com.spacepocalypse.beermap2.service.LoginService.AuthState;
import com.spacepocalypse.util.Conca;

@Controller
public class AndroidController {
	private BeerService beerService;
	private LoginService loginService;
	private Logger log4jLogger;
	
	@Autowired
	public AndroidController(BeerService beerService, LoginService loginService) {
		log4jLogger = Logger.getLogger(getClass());
		this.beerService = beerService;
		this.loginService = loginService;
	}
	
	@RequestMapping(value = {"/android", "/android/"}, method=RequestMethod.GET)
	public String androidWelcome(Model model) {
		
		return "androidwelcome";
	}
	
	@RequestMapping(value = {"/android/beer/", "/android/beer"}/*, method=RequestMethod.POST*/)
	public String getBeersByNameJSON(@RequestParam("q") String query, Model model) {
		List<MappedBeer> results = beerService.query(query);
		
		JSONArray jsonArr = new JSONArray();
		for (final MappedBeer ea : results) {
			JSONObject obj = new JSONObject(ea);
			jsonArr.put(obj);
		}
		
		Logger.getLogger(getClass()).info(Conca.t("Returning beers:[", results.toString(), "]"));
		
		model.addAttribute(AndroidBeerQueryServlet.KEY_BM4A_JSON_RESULT, jsonArr.toString());
		
		return AndroidBeerQueryServlet.BM4A_JSON_RESULT;
	}
	
	@RequestMapping(value = {"/android/login/", "/android/login"}/*, method=RequestMethod.POST*/)
	public String handleLogin(
			@RequestParam(AndroidBeerQueryServlet.KEY_USERNAME) String username, 
			@RequestParam(AndroidBeerQueryServlet.KEY_PASSWORD) String password, 
			Model model) 
	{
		log4jLogger.info(Conca.t("received login request of username=[", username, "] hashpass=[", password, "]"));
		
		AuthData data = loginService.authUser(username, password);
		
		log4jLogger.info(Conca.t("authUser check returned AuthData: [", data.toString(), "]"));
		
		JSONObject obj = new JSONObject();

		boolean authSuccess = (data.getState() == AuthState.SUCCESS);
		
		try {
			obj.put(AndroidBeerQueryServlet.RESULT_SUCCESS, authSuccess);
			
			if (authSuccess) {
				obj.put(AndroidBeerQueryServlet.KEY_USER, new JSONObject(data.getUser()));
				obj.put(AndroidBeerQueryServlet.KEY_TIMEOUT_MS, data.getAuthTimeoutMs() + System.currentTimeMillis());
				
			}
		} catch (JSONException e) {
			log4jLogger.error("JSON error occurred while attempting to construct login response.", e);
		}
		
		log4jLogger.info(Conca.t("Writing: [", obj.toString(), "]"));
		
		model.addAttribute(AndroidBeerQueryServlet.KEY_BM4A_JSON_RESULT, obj.toString());

		return AndroidBeerQueryServlet.BM4A_JSON_RESULT;
	}
}
