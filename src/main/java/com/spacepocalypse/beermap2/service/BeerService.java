package com.spacepocalypse.beermap2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.spacepocalypse.beermap2.dao.BeerDbAccess;
import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedBrewery;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.domain.MappedValue;
import com.spacepocalypse.util.Conca;
import com.spacepocalypse.util.StrUtl;

public class BeerService implements IBeerService {
	private BeerDbAccess dbAccess;
	private Logger log4jLogger;
	
	public BeerService(BeerDbAccess dbAccess) {
		log4jLogger = Logger.getLogger(getClass());
		this.dbAccess = dbAccess;
	}
	
	public List<MappedBeer> findAllBeers(String beerName) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.QUERY_KEY_BEER_OR_BREWERY, Conca.t("%", StringUtils.lowerCase(beerName), "%"));
		List<MappedBeer> results = Collections.emptyList();
		
		try {
			results = dbAccess.findAllBeers(params);
			
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Error while accessing dao for findallbeers", e);
		}
		
		return results;
	}
	
	@Override
	public MappedBrewery findBreweryById(int id) {
	    return dbAccess.findBreweryById(id);
	}
	
	@Override
	public MappedBeer findBeerById(int id) {
		return dbAccess.findBeerById(id);
	}
	
	public List<MappedBrewery> findAllBreweries() {
	    return dbAccess.findAllBreweries();
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
		params.put(Constants.KEY_BEER_ID, beerId);
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
	
	@Override
	public boolean updateBeer(final MappedBeer beer, final MappedUser user) {
	    // TODO: add support for change log linking back to the user
	    boolean success = false;
		try {
			success = dbAccess.updateBeer(beer, user.getId());
			
		} catch (Exception e) {
			log4jLogger.error(Conca.t("Error occurred while attempting to update beer [", beer, "]"), e);
		}
		
		return success;
	}
	
	@Override
	public int insertBeer(final MappedBeer beer, final int userId) {
	    return dbAccess.insertBeer(beer, userId);
	}
	
	public boolean insertBeer(final String mappedBeerJSON, final int userId) {
		try {
			final MappedBeer beerToInsert = MappedBeer.createMappedBeer(mappedBeerJSON);
			return dbAccess.insertBeer(beerToInsert, userId) != Constants.INVALID_ID;
			
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

    @Override
    public List<MappedBrewery> findAllBreweries(final String query) {
        return dbAccess.findAllBreweries(query);
    }

    @Override
    public List<MappedBeer> findBeersByIds(int[] beerIds) {
        final List<MappedBeer> beers = dbAccess.findBeersByIds(beerIds);

        // order the beers according to the id order
        final Map<Integer, MappedBeer> idToBeerMap = new HashMap<Integer, MappedBeer>();
        
        for (final MappedBeer ea : beers) {
            idToBeerMap.put(ea.getId(), ea);
        }
        
        final List<MappedBeer> toReturn = new ArrayList<MappedBeer>();
        
        for (final Integer eaId : beerIds) {
            toReturn.add(idToBeerMap.get(eaId));
        }
        
        return toReturn;
    }

    @Override
    public List<MappedBeer> findBeersForUserId(int userId) {
        return dbAccess.findBeersForUserId(userId);
    }   

}