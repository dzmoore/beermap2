package com.spacepocalypse.beermap2.service;

import java.util.List;

import com.spacepocalypse.beermap2.dao.BeerDbAccess;
import com.spacepocalypse.beermap2.domain.MappedBeer;



public class AndroidService {
	private BeerDbAccess dbAccess;
	private BeerService beerService;
	
	public AndroidService(BeerDbAccess dbAccess) {
		this.dbAccess = dbAccess;
		this.beerService = new BeerService(dbAccess);
	}
	
	public List<MappedBeer> beersByName(String nameQuery) {
		return beerService.findAllBeers(nameQuery);
	}
	
}
