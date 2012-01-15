package com.spacepocalypse.beermap2.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.spacepocalypse.beermap2.domain.MappedBeer;

public class BeerService implements IBeerService {
	private BeerDbAccess dbAccess;
	
	public BeerService(BeerDbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
	public List<MappedBeer> query(String beerName) {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put(BeerSearchEngine.QUERY_KEY_NAME, new String[]{beerName});
		List<MappedBeer> results = Collections.emptyList();
		
		try {
			results = dbAccess.findAllBeers(params);
			
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Error while accessing dao for findallbeers", e);
		}
		
		return results;
	}

}