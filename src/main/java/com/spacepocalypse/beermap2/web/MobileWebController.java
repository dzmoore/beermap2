package com.spacepocalypse.beermap2.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.beermap2.service.IBeerService;
import com.spacepocalypse.beermap2.service.LoginService;
import com.spacepocalypse.beermap2.service.LoginService.AuthData;
import com.spacepocalypse.beermap2.service.LoginService.AuthState;
import com.spacepocalypse.util.Conca;
import com.spacepocalypse.util.StrUtl;

@Controller
public class MobileWebController {
    private static final int DEFAULT_MAX_SEARCH_RESULTS_SIZE = 45;
    private static final int DEFAULT_MAX_SEARCH_RESULTS_OF_IDS_SIZE = 250;
    
    private IBeerService beerService;
    private LoginService loginService;
    private Logger log4jLogger;
    
    @Autowired
    private int maxSearchResultsSize;
    
    @Autowired
    private int maxSearchResultsOfIdsSize;

    @Autowired
    public MobileWebController(IBeerService beerService, LoginService loginService) {
        log4jLogger = Logger.getLogger(getClass());
        this.beerService = beerService;
        this.loginService = loginService;
        this.maxSearchResultsSize = DEFAULT_MAX_SEARCH_RESULTS_SIZE;
        this.maxSearchResultsOfIdsSize = DEFAULT_MAX_SEARCH_RESULTS_OF_IDS_SIZE;
    }
    
    @RequestMapping(value = {"/m/main", "/m/main/"}, method=RequestMethod.GET)
    public String mobileMain(HttpSession session) {
        return "main";
    }
    
    @RequestMapping(value = {"/m/search", "/m/search/"}, method=RequestMethod.GET)
    public String search(HttpSession session) {
        return "search";
    }
    
    @RequestMapping(value = {"/m/search_params", "/m/search_params/"}, method=RequestMethod.GET)
    public String searchWithParams(
            @RequestParam(Constants.KEY_QUERY) final String query,
            final HttpSession session) 
    {
        session.setAttribute(Constants.KEY_QUERY, query);
        return "search";
    }

    @RequestMapping(value = {"/m/beersearch/", "/m/beersearch"})
    public String getBeersByNameJSON(
            @RequestParam(Constants.KEY_QUERY) String query, 
            @RequestParam(Constants.KEY_GET_RESULTS_AS_IDS) String useIds,
            Model model) 
    {
        log4jLogger.trace(Conca.t("beer search request. query=[", query, "] useIds[", useIds, "]"));

        List<MappedBeer> results = beerService.findAllBeers(query);

        if (useIds.equals(Constants.VALUE_FALSE)) {
            if (results.size() > maxSearchResultsSize) {
                final List<MappedBeer> temp = new ArrayList<MappedBeer>();

                int i = 0;
                for (final MappedBeer ea : results) {
                    if (++i > maxSearchResultsSize) {
                        break;
                    }

                    temp.add(ea);
                }

                results = temp;
            }

            convertToJSONAndAddToModel(model, results);

            log4jLogger.trace(StrUtl.trunc(Conca.t("Returning beers:[", results.toString(), "]"), 200));

        } else {
            final JSONArray arr = new JSONArray();

            int i = 0;
            for (final MappedBeer ea : results) {
                arr.put(ea.getId());

                if (++i >= maxSearchResultsOfIdsSize) {
                    break;
                }
            }


            model.addAttribute(Constants.RESULT, arr.toString());

            log4jLogger.trace(StrUtl.trunc(Conca.t("Returning beer ids:[", arr.toString(), "]"), 200));
        }

        return Constants.JSON_RESULT;
    }
    
    @RequestMapping(value = {"/m/login/", "/m/login"}, method = RequestMethod.POST)
    public String userLogin(
            @RequestParam(Constants.KEY_USERNAME) String username, 
            @RequestParam(Constants.KEY_PASSWORD) String password,
            HttpSession session,
            Model model) 
    {
        log4jLogger.trace(Conca.t("login: username=[", username, "] password length[", StringUtils.length(password), "]"));

        final AuthData authData = loginService.authUser(username, password);
        String isLoggedInString = Constants.VALUE_FALSE;
        boolean isLoggedIn = false;
        if (authData.getState() == AuthState.SUCCESS) {
            session.setAttribute(Constants.KEY_USERNAME, username);
            session.setAttribute(Constants.KEY_PASSWORD, password);
            isLoggedInString = Constants.VALUE_TRUE;
            isLoggedIn = true;
            
        } else {
            session.setAttribute(Constants.KEY_USERNAME, StringUtils.EMPTY);
            session.setAttribute(Constants.KEY_PASSWORD, StringUtils.EMPTY);
        }

        session.setAttribute(Constants.KEY_LOGGED_IN, isLoggedInString);
        
        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.RESULT, isLoggedIn);
            
        } catch (JSONException e) {
            log4jLogger.error("error occurred while attempting to put result for login call", e);
        }
        
        model.addAttribute(Constants.RESULT, jsonObj.toString());
        
        return Constants.JSON_RESULT;
    }
    
    @RequestMapping(value = {"/m/logout/", "/m/logout"}, method = RequestMethod.POST)
    public String userLogin(
            HttpSession session,
            Model model) 
    {
        session.setAttribute(Constants.KEY_USERNAME, null);
        session.setAttribute(Constants.KEY_PASSWORD, null);

        session.setAttribute(Constants.KEY_LOGGED_IN, Constants.VALUE_FALSE);
        
        
        return Constants.JSON_RESULT;
    }
    
    @RequestMapping(value = {"/m/logindialog/", "/m/logindialog"}, method = RequestMethod.GET)
    public String loginDialog(@RequestParam("to") final String redirectLocOnSuccess, HttpSession session, Model model) {
        model.addAttribute("to", redirectLocOnSuccess);
        return "login";
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
            model.addAttribute(Constants.RESULT, convertToJSONArray(list));
            
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
    
    @RequestMapping(value = {"/m/beer", "/m/beer/"})
    public String displayBeer(@RequestParam(Constants.KEY_BEER_ID) String id, HttpSession session, Model model) {
        final MappedBeer result = beerService.findBeerById(id);
        
        if (result.getId() == Constants.INVALID_ID) {
            model.addAttribute(Constants.RESULT, Constants.UNABLE_TO_FIND);
            
        } else {
            model.addAttribute(Constants.RESULT, result.getName());
        }
        
        model.addAttribute(Constants.KEY_MAPPED_BEER, result);
        
        return "beer";
    }
    
    @RequestMapping(value = {"/m/beerupdate/", "/m/beerupdate"})
    public String updateBeer(
            @RequestParam(Constants.KEY_MAPPED_BEER) String beerToUpdate, 
            @RequestParam(Constants.KEY_USERNAME) String username, 
            @RequestParam(Constants.KEY_PASSWORD) String password,
            Model model) 
    {
        boolean result = false;
        
        final AuthData authData = loginService.authUser(username, password);
        if (authData.getState() == AuthState.SUCCESS) {
            result = beerService.updateBeer(beerToUpdate, authData.getUser());
        }
        
        JSONObject jsonResultObj = new JSONObject();
        try {
            jsonResultObj.put(Constants.RESULT, result);
        
        } catch (Exception e) {
            log4jLogger.error("Error putting beer update result into json object", e);
        }
        
        model.addAttribute(Constants.RESULT, jsonResultObj.toString());
        
        Logger.getLogger(getClass()).debug(Conca.t("Beer update for beer json [", beerToUpdate, "] success:[", result, "]"));
        
        return Constants.JSON_RESULT;
    }
    
    @RequestMapping(value = {"/m/createbeer/", "/m/createbeer"}, method = RequestMethod.GET)
    public String createBeerPage(
            final Model model) 
    {
        return "createbeer";
    }
    
    @RequestMapping(value = {"/m/createbeer/", "/m/createbeer"}, method = RequestMethod.POST)
    public String insertBeer(
            @RequestParam(Constants.KEY_MAPPED_BEER) final String beerToInsert, 
            @RequestParam(Constants.KEY_USER_ID) final String userId, 
            final Model model) 
    {
        int intUserId = Constants.INVALID_ID;
        try {
            intUserId = Integer.parseInt(userId);
        } catch (Exception e) {
            log4jLogger.error(Conca.t(
                    "Parsing error occurred while attempting parse user id on insert beer[", 
                    beerToInsert, "] userid[", userId, "]"
            ));
            
        }
        
        final boolean result = beerService.insertBeer(beerToInsert, intUserId);
        
        JSONObject jsonResultObj = new JSONObject();
        try {
            jsonResultObj.put(Constants.RESULT, result);
        
        } catch (Exception e) {
            log4jLogger.error("Error putting beer update result into json object", e);
        }
        
        model.addAttribute(Constants.JSON_RESULT, jsonResultObj.toString());
        
        Logger.getLogger(getClass()).info(Conca.t("Beer update for beer json [", beerToInsert, "] success:[", result, "]"));
        
        return Constants.JSON_RESULT;
    }
    
    @RequestMapping(value = {"/m/createacct/", "/m/createacct"}, method = RequestMethod.GET)
    public String createAcctGet(final Model model) {
        return "createacct";
    }
    
    @RequestMapping(value = {"/m/createacct/", "/m/createacct"}, method = RequestMethod.POST)
    public String createAcctPost(
            @RequestParam(Constants.KEY_USERNAME) final String username, 
            @RequestParam(Constants.KEY_PASSWORD) final String password, 
            final Model model) 
    {
        final MappedUser user = loginService.createUser(username, password);
        
        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.RESULT, user.getId() != Constants.INVALID_ID);
            
        } catch (Exception e) {
            log4jLogger.error(Conca.t(
                "Error occurred while attempting to create user [", username, "]"),
                e
            );
            
            try {
                jsonObj.put(Constants.RESULT, false);
                
            } catch (JSONException e1) {}
        }
        
        final String jsonResult = jsonObj.toString();
        
        model.addAttribute(Constants.RESULT, jsonResult);
        
        Logger.getLogger(getClass()).info(Conca.t("create user method: user [", username, "] json:[", jsonResult, "]"));
        
        return Constants.JSON_RESULT;
    }
    
    @RequestMapping(value = {"/m/userexists/", "/m/userexists"}, method = RequestMethod.POST)
    public String userExists( 
            @RequestParam(Constants.KEY_QUERY) final String query,
            final Model model) 
    {
        final boolean userExists = loginService.doesUserExist(query);
        
        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Constants.RESULT, userExists);
            
        } catch (Exception e) {
            log4jLogger.error("Error occurred while attempting to put result value when calling userexists", e);
        }
        
        final String jsonResult = jsonObj.toString();
        
        if (log4jLogger.isDebugEnabled()) {
            log4jLogger.debug(Conca.t("userExists returning: [", jsonResult, "]"));
        }
        
        model.addAttribute(Constants.RESULT, jsonResult);
        
        return Constants.JSON_RESULT;
    }
    
    public int getMaxSearchResultsSize() {
        return maxSearchResultsSize;
    }

    public void setMaxSearchResultsSize(int maxSearchResultsSize) {
        this.maxSearchResultsSize = maxSearchResultsSize;
    }

    public int getMaxSearchResultsOfIdsSize() {
        return maxSearchResultsOfIdsSize;
    }

    public void setMaxSearchResultsOfIdsSize(int maxSearchResultsOfIdsSize) {
        this.maxSearchResultsOfIdsSize = maxSearchResultsOfIdsSize;
    }
}
