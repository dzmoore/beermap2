package com.spacepocalypse.beermap2.service;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedValue;

public class BeerSearchEngine implements IStoppable {
	private static BeerSearchEngine instance;
	
//	public static final String QUERY_KEY_NAME = "_name";
//	public static final String QUERY_KEY_ABV = "_abv";
//	public static final String QUERY_KEY_UPC = "_upc";
//	public static final String QUERY_KEY_BEER_ID = "_beerid";
//	public static final String QUERY_KEY_USER_ID = "_userid";
//	private static final String[] VALID_BEER_SEARCH_KEYS = {
//		QUERY_KEY_NAME,
//		QUERY_KEY_ABV,
//		QUERY_KEY_UPC
//	};
//	
//	private static final String[] VALID_RATING_SEARCH_KEYS = {
//		QUERY_KEY_BEER_ID,
//		QUERY_KEY_USER_ID
//	};
	
	private Logger log4jLogger;
	private BeerSearchEngine() {
		log4jLogger = Logger.getLogger(getClass());
	}
	
	public static BeerSearchEngine getInstance() {
		if (instance == null) {
			instance = new BeerSearchEngine();
		}
		return instance;
	}
	
//	public boolean doInsertBeer(MappedBeer beer) {
//		return getBeerDbAccess().insertBeer(beer);
//	}
//	
//	public boolean doUpdateBeer(MappedBeer beer)  {
//		return getBeerDbAccess().updateById(beer);
//	}
//	
//	public boolean doInsertRating(MappedBeerRating rating)  {
//		return getBeerDbAccess().insertRating(rating);
//	}
//	
//	public boolean doUpdateRating(MappedBeerRating rating) {
//		return getBeerDbAccess().updateBeerRating(rating);
//	}
//	
//	public void doSearchByBeerName(PrintWriter out, String beerName) {
//		List<MappedBeer> results = getBeerDbAccess().findAllBeersByName(beerName);
//		printResults(out, beerName, results);
//	}
//	
//	public List<MappedBeer> doBeerSearch(Map<String, String[]> parameters) {
//		for (String key : VALID_BEER_SEARCH_KEYS) {
//			if (parameters.containsKey(key)) {
//				try {
//					return getBeerDbAccess().findAllBeers(parameters);
//				} catch (Exception e) {
//					log4jLogger.error(e.getMessage(), e);
//				}
//			}
//		}
//		
//		return new ArrayList<MappedBeer>();
//	}
	
//	public List<MappedBeerRating> doRatingSearch(Map<String, String[]> parameters) {
//		for (String key : VALID_RATING_SEARCH_KEYS) {
//			if (parameters.containsKey(key)) {
//				return getBeerDbAccess().findAllBeerRatings(parameters);
//			}
//		}
//		
//		return new ArrayList<MappedBeerRating>();
//	}
//	
//	public List<MappedValue> doFindAllRatingTypes() {
//		return getBeerDbAccess().findAllBeerRatingLegalValues();
//	}

	private void printResults(PrintWriter out, String beerName,
			List<MappedBeer> results) {
		out.println("<b>Results for \"" + beerName + "\":</b><br /> <br />");
		out.println("<hr />");

		for (MappedBeer beer : results) {
			out.print("<h3><b>"+beer.getName() + "</b></h3>");
			
			out.print("<b>abv</b>: "+beer.getAbv());
			out.print("<br />");
			
			out.print("<b>UPC</b>: " + beer.getUpc());
			out.print("<br />");
			
			out.println("<b>descript</b>: "+beer.getDescript());
			out.println("<hr />");
		}
	}
	
	public void doSearchByUpc(PrintWriter out, String upcData) {
//		List<MappedBeer> results = getBeerDbAccess().findAllBeersByUpc(upcData);
//		printResults(out, upcData, results);
//	}
//	
//	public List<MappedBeer> doSearchByUpc(String upcData) {
//		return getBeerDbAccess().findAllBeersByUpc(upcData);
	}
	
	public void stop() {
//		if (instance != null) {
//			try {
//				getBeerDbAccess().close();
//			} catch (SQLException e) {
//				log4jLogger.error("SQLException occurred while trying to stop BeerSearchEngine", e);
//			}
//		}
	}

}
