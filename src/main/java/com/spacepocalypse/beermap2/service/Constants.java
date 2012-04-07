package com.spacepocalypse.beermap2.service;

import java.util.Map;

import org.apache.log4j.Logger;

public class Constants {
	public static final String RESULT_SUCCESS = "success";
	public static final long serialVersionUID = -6893360066632654398L;
	
	public static final String KEY_QUERY = "q";
	
	public static final String BM4A_JSON_RESULT = "bm4a_json_result";
	
	// param keys
	public static final String KEY_BM4A_JSON_RESULT = "result";
	public static final String RESULT = "result";
	public static final String KEY_MAPPED_BEER = "mb";
	public static final String KEY_MAPPED_BEERS = "beers";
	public static final String KEY_USERNAME = "un";
	public static final String KEY_PASSWORD = "pw";
	public static final String KEY_MAPPED_RATING = "mr";
	public static final String KEY_USER = "user";
	public static final String KEY_TIMEOUT_MS = "TOms";
	public static final String KEY_BEER_ID = "id";
	public static final String KEY_BEER_IDS = "ids";
	public static final String KEY_USER_ID = "_uid";
	public static final String KEY_GET_RESULTS_AS_IDS = "_ids";
	public static final String VALUE_GET_RESULTS_AS_IDS_FALSE = "0";
	public static final String VALUE_GET_RESULTS_AS_IDS_TRUE = "1";
	
	public static int INVALID_ID = -1;
	
	public static String UNABLE_TO_FIND = "Unable to Find Item";
	
	// search
	public static final String QUERY_TYPE_SEARCH = "s";
	public static final String KEY_SEARCH_TYPE = "st";
	public static final String SEARCH_TYPE_BEER = "beer";
	public static final String SEARCH_TYPE_RATING = "rating";
	public static final String SEARCH_TYPE_ALL_RATING_TYPES = "art";
	
	public static final String QUERY_KEY_BEER_NAME = "_beername";
	public static final String QUERY_KEY_BEER_OR_BREWERY = "_beerorbrewery";
	public static final String QUERY_KEY_BREWERY_NAME = "_breweryname";
	public static final String QUERY_KEY_ABV = "_abv";
	public static final String QUERY_KEY_UPC = "_upc";
	public static final String QUERY_KEY_USER_ID = "_userid";
	
	// update
	public static final String QUERY_TYPE_UPDATE = "u";

	
	// logon
	public static final String QUERY_TYPE_LOGON = "l";

	
	// insert
	public static final String QUERY_TYPE_INSERT = "i";
	
	private Logger log4jLogger;

//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		super.init(config);
//		log4jLogger = Logger.getLogger(AndroidBeerQueryServlet.class);
//		log4jLogger.info("Initializing.");
//	}
	
//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		Map<String, String[]> parameterMap = req.getParameterMap();
//		logParameterMap(parameterMap);
//		
//		String[] queryTypeArr = parameterMap.get(KEY_QUERY);
//		
//		if (queryTypeArr == null || queryTypeArr.length <= 0) {
//			log4jLogger.error("Query type not specified! Request map:{" + parameterMap.toString() + "}");
//			return;
//		}
//		
//		String queryType = queryTypeArr[0];
//		
//		if (queryType.equalsIgnoreCase(QUERY_TYPE_SEARCH)) {
//			handleSearch(resp, parameterMap);
//			
//		} else if(queryType.equalsIgnoreCase(QUERY_TYPE_UPDATE)) {
//			handleUpdate(resp, parameterMap);
//			
//		} else if (queryType.equalsIgnoreCase(QUERY_TYPE_LOGON)) {
//			handleLogon(resp, parameterMap);
//		} else if (queryType.equalsIgnoreCase(QUERY_TYPE_INSERT)) {
//			handleInsertRating(resp, parameterMap);
//		}
//	}

//	private void handleInsertRating(HttpServletResponse resp,
//			Map<String, String[]> parameterMap) {
//		if (parameterMap.containsKey(KEY_MAPPED_RATING)) {
//			String ratingString = getFirstOrNullTrying(parameterMap, KEY_MAPPED_RATING);
//			if (ratingString == null) {
//				log4jLogger.warn("Request to insert rating aborted due to MappedRating being null.  Parameter map: {"
//						+ parameterMap.toString() + "}");
//				return;
//			}
//			boolean result = false;
//			MappedBeerRating rating = null;
//			try {
//				rating = MappedBeerRating.createMappedBeerRating(ratingString);
//			} catch (JSONException e1) {
//				log4jLogger.error(
//						"Error parsing JSON string: [" +
//						ratingString + 
//						"].",
//						e1
//				);
//				return;
//			}
//			result = BeerSearchEngine.getInstance().doInsertRating(rating);
//			
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put(RESULT_SUCCESS, result);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			resp.setContentType("text/plain");
//			log4jLogger.info("Writing: [" + obj.toString() + "]");
//			try {
//				resp.getWriter().println(obj.toString());
//			} catch (IOException e) {
//				log4jLogger.error("Error writing response: [" + obj.toString() + "]", e);
//			}
//		} else if (parameterMap.containsKey(KEY_MAPPED_BEER)) {
//			String beerString = getFirstOrNullTrying(parameterMap, KEY_MAPPED_BEER);
//			if (beerString == null) {
//				log4jLogger.warn("Request to insert beer aborted due to MappedBeer being null.  Parameter map: {"
//						+ parameterMap.toString() + "}");
//				return;
//			}
//			boolean result = false;
//			MappedBeer beer = null;
//			try {
//				beer = MappedBeer.createMappedBeer(beerString);
//			} catch (JSONException e1) {
//				log4jLogger.error(
//						"Error parsing JSON string: [" +
//						beerString + 
//						"].",
//						e1
//				);
//				return;
//			}
//			result = BeerSearchEngine.getInstance().doInsertBeer(beer);
//			
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put(RESULT_SUCCESS, result);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			resp.setContentType("text/plain");
//			log4jLogger.info("Writing: [" + obj.toString() + "]");
//			try {
//				resp.getWriter().println(obj.toString());
//			} catch (IOException e) {
//				log4jLogger.error("Error writing response: [" + obj.toString() + "]", e);
//			}
//		}
//	}
	
//	private void handleLogon(HttpServletResponse resp,
//			Map<String, String[]> parameterMap) throws IOException {
//		String username = getFirstOrNullTrying(parameterMap, KEY_USERNAME);
//		String hashPass = getFirstOrNullTrying(parameterMap, KEY_PASSWORD);
//		AuthData data = LogonEngine.getInstance().authUser(username, hashPass);
//		
//		log4jLogger.info("authUser check returned AuthData: [" + data.toString() + "]");
//		
//		resp.setContentType("text/plain");
//		JSONObject obj = new JSONObject();
//		
//		boolean authSuccess = data.getState() == AuthState.SUCCESS;
//		
//		try {
//			obj.put(RESULT_SUCCESS, authSuccess);
//			if (authSuccess) {
//				obj.put("user", new JSONObject(data.getUser()));
//				obj.put("timeoutAbsMs", data.getAuthTimeoutMs() + System.currentTimeMillis());
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		log4jLogger.info("Writing: [" + obj.toString() + "]");
//		resp.getWriter().println(obj.toString());
//	}

//	private void handleUpdate(HttpServletResponse resp,
//			Map<String, String[]> parameterMap) throws IOException {
//		log4jLogger.info("Detected update query.");
//
//		if (parameterMap.containsKey(KEY_MAPPED_BEER)) {
//			String beerParam = getFirstOrNullTrying(parameterMap, KEY_MAPPED_BEER);
//			if (beerParam == null) {
//				return;
//			}
//			boolean result = false;
//			try {
//				MappedBeer beer = MappedBeer.createMappedBeer(beerParam);
//				result = BeerSearchEngine.getInstance().doUpdateBeer(beer);
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put(RESULT_SUCCESS, result);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			resp.setContentType("text/plain");
//			log4jLogger.info("Writing: [" + obj.toString() + "]");
//			resp.getWriter().println(obj.toString());
//		} else if (parameterMap.containsKey(KEY_MAPPED_RATING)) {
//			String ratingParam = getFirstOrNullTrying(parameterMap, KEY_MAPPED_RATING);
//			if (ratingParam == null) {
//				return;
//			}
//			
//			boolean result = false;
//			try {
//				MappedBeerRating rating = MappedBeerRating.createMappedBeerRating(ratingParam);
//				result = BeerSearchEngine.getInstance().doUpdateRating(rating);
//				JSONObject obj = new JSONObject();
//				obj.put(RESULT_SUCCESS, result);
//				
//				resp.setContentType("text/plain");
//				log4jLogger.info("Writing: [" + obj.toString() + "]");
//				resp.getWriter().println(obj.toString());
//			} catch (JSONException e) {
//				log4jLogger.error("Error creating JSON object. parameter=[" + ratingParam + "]", e);
//			}
//		}
//	}

//	private void handleSearch(HttpServletResponse resp,
//			Map<String, String[]> parameterMap) throws IOException {
//		log4jLogger.info("Detected search query.");
//		String[] typeArr = parameterMap.get(KEY_SEARCH_TYPE);
//		String type = null;
//		if (typeArr != null && typeArr.length > 0) {
//			type = typeArr[0];
//		}
//		
//		if (type == null) {
//			log4jLogger.warn("Search failed due to no type detected. parameterMap={" + parameterMap.toString() + "}");
//			return;
//		}
//		
//		if (type.equalsIgnoreCase(SEARCH_TYPE_BEER)) {
//			doBeerSearch(resp, parameterMap);
//		} else if (type.equalsIgnoreCase(SEARCH_TYPE_RATING)) {
//			doRatingsSearch(resp, parameterMap);
//		} else if (type.equalsIgnoreCase(SEARCH_TYPE_ALL_RATING_TYPES)) {
//			doRatingTypesSearch(resp);
//		}
//		
//	}
//	
//	private void doRatingTypesSearch(HttpServletResponse resp) throws IOException {
//		List<MappedValue> vals = BeerSearchEngine.getInstance().doFindAllRatingTypes();
//		JSONArray jsonArr = new JSONArray();
//		for (MappedValue ea : vals) {
//			JSONObject obj = new JSONObject(ea);
//			jsonArr.put(obj);
//		}
//		log4jLogger.info("Writing response: " + jsonArr.toString());
//		resp.setContentType("text/plain");
//		resp.getWriter().println(jsonArr.toString());
//	}
//
//	private void doRatingsSearch(HttpServletResponse resp,
//			Map<String, String[]> parameterMap) throws IOException {
//		List<MappedBeerRating> ratings = BeerSearchEngine.getInstance().doRatingSearch(parameterMap);
//		JSONArray jsonArr = new JSONArray();
//		for (MappedBeerRating ea : ratings) {
//			JSONObject obj = new JSONObject(ea);
//			jsonArr.put(obj);
//		}
//		log4jLogger.info("Writing response: " + jsonArr.toString());
//		resp.setContentType("text/plain");
//		resp.getWriter().println(jsonArr.toString());
//	}
//
//	private void doBeerSearch(HttpServletResponse resp,
//			Map<String, String[]> parameterMap) throws IOException {
//		List<MappedBeer> beers = BeerSearchEngine.getInstance().doBeerSearch(parameterMap);
//		JSONArray jsonArr = new JSONArray();
//		for (MappedBeer ea : beers) {
//			JSONObject obj = new JSONObject(ea);
//			jsonArr.put(obj);
//		}
//		log4jLogger.info("Writing response: " + jsonArr.toString());
//		resp.setContentType("text/plain");
//		resp.getWriter().println(jsonArr.toString());
//	}
//	
//	private String getFirstOrNullTrying(Map<String, String[]> map, String key) {
//		if (map.containsKey(key)) {
//			String[] val = map.get(key);
//			if (val != null && val.length > 0) {
//				return val[0];
//			}
//		}
//		return null;
//	}

	private void logParameterMap(Map<String, String[]> parameterMap) {
		StringBuilder sb = new StringBuilder();
		boolean notFirst = false;
		for (String key : parameterMap.keySet()) {
			if (notFirst) {
				sb.append(", ");
			}
			sb.append(key);
			sb.append("=[");
			boolean innerNotFirst = false;
			for (String value : parameterMap.get(key)) {
				if (innerNotFirst) {
					sb.append(", ");
				}
				sb.append(value);
				innerNotFirst = true;
			}
			sb.append("]");
			notFirst = true;
		}
		log4jLogger.info("Request's parameter map=[" + sb.toString() + "]");
	}
	
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		resp.setContentType("text/html");
//		resp.getWriter().println("beermap4android");
//	}
	
//	@Override
//	public void destroy() {
//	}
}
