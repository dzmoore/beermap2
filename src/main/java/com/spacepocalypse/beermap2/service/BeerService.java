package com.spacepocalypse.beermap2.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedValue;
import com.spacepocalypse.util.Conca;

public class BeerService implements IBeerService {
	private BeerDbAccess dbAccess;
	private Logger log4jLogger;
	
	public BeerService(BeerDbAccess dbAccess) {
		log4jLogger = Logger.getLogger(getClass());
		this.dbAccess = dbAccess;
	}
	
	public List<MappedBeer> findAllBeers(String beerName) {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put(Constants.QUERY_KEY_NAME, new String[]{beerName});
		List<MappedBeer> results = Collections.emptyList();
		
		try {
			results = dbAccess.findAllBeers(params);
			
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Error while accessing dao for findallbeers", e);
		}
		
		return results;
	}
	
	public List<MappedValue> findAllRatingTypes() {
		List<MappedValue> ret = Collections.emptyList();

		try {
			ret = dbAccess.findAllBeerRatingLegalValues();
			
		} catch (Exception e) {
			log4jLogger.error("An error occurred while attempting to find all beer rating LVs", e);
		}
		
		return ret;
	}
	
	public List<MappedBeerRating> findAllBeerRatings(String beerId, String userId) {
		List<MappedBeerRating> ret = Collections.emptyList();

		final Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.QUERY_KEY_BEER_ID, beerId);
		params.put(Constants.QUERY_KEY_USER_ID, userId);
		
		try {
			ret = dbAccess.findAllBeerRatings(params);
			
		} catch (Exception e) {
			log4jLogger.error(
					Conca.t(
							"An error occurred while attempting to find all beer ratings for beerid=[",
							beerId, "] and userid=[", userId, "]"
					), 
					e
			);
		}
		
		return ret;
	}
	
	public boolean updateBeer(final String mappedBeerJSON) {
		try {
			final MappedBeer beerToUpdate = MappedBeer.createMappedBeer(mappedBeerJSON);
			return dbAccess.updateById(beerToUpdate);
			
		} catch (Exception e) {
			log4jLogger.error(Conca.t("Error occurred while attempting to update beer [", mappedBeerJSON, "]"), e);
		}
		
		return false;
	}
	
	public boolean insertBeer(final String mappedBeerJSON) {
		try {
			final MappedBeer beerToInsert = MappedBeer.createMappedBeer(mappedBeerJSON);
			return dbAccess.insertBeer(beerToInsert);
			
		} catch (Exception e) {
			log4jLogger.error(Conca.t("Error occurred while attempting to insert beer [", mappedBeerJSON, "]"), e);
		}
		
		return false;
	}
	
	public boolean updateBeerRating(final String ratingJSON) {
		try {
			final MappedBeerRating ratingToUpdate = MappedBeerRating.createMappedBeerRating(ratingJSON);
			return dbAccess.updateBeerRating(ratingToUpdate);
			
		} catch (Exception e) {
			log4jLogger.error(Conca.t("Error occurred while attempting to update beer rating [", ratingJSON, "]"), e);
		}
		
		return false;
	}
	
	public boolean insertBeerRating(final String ratingJSON) {
		try {
			final MappedBeerRating ratingToInsert = MappedBeerRating.createMappedBeerRating(ratingJSON);
			return dbAccess.insertRating(ratingToInsert);
			
		} catch (Exception e) {
			log4jLogger.error(Conca.t("Error occurred while attempting to insert beer rating [", ratingJSON, "]"), e);
		}
		
		return false;
	}

}