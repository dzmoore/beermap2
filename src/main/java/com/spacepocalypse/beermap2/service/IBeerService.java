package com.spacepocalypse.beermap2.service;

import java.util.List;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedBrewery;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.domain.MappedValue;

public interface IBeerService {
	public List<MappedBeer> findAllBeers(String beerName);
	
	public List<MappedBeer> findBeersForUserId(int userId);
	
	public List<MappedBeer> findBeersByIds(int[] beerIds);
	
	public List<MappedValue> findAllRatingTypes();

	public boolean updateBeer(final MappedBeer beer, final MappedUser user);

	public List<MappedBeerRating> findAllBeerRatings(String beerId, String userId);

	public boolean updateBeerRating(String ratingJSON);

	public boolean insertBeerRating(String ratingJSON);

	public boolean insertBeer(String mappedBeerJSON, int userId);

    List<MappedBrewery> findAllBreweries();

    public List<MappedBrewery> findAllBreweries(String query);

    MappedBeer findBeerById(int id);

    int insertBeer(MappedBeer beer, int userId);

    MappedBrewery findBreweryById(int id);
}
