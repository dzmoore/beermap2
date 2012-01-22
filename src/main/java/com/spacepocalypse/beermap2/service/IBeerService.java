package com.spacepocalypse.beermap2.service;

import java.util.List;

import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBeerRating;
import com.spacepocalypse.beermap2.domain.MappedValue;

public interface IBeerService {
	public List<MappedBeer> findAllBeers(String beerName);

	public List<MappedValue> findAllRatingTypes();

	public boolean updateBeer(String mappedBeerJSON);

	public List<MappedBeerRating> findAllBeerRatings(String beerId, String userId);

	public boolean updateBeerRating(String ratingJSON);

	public boolean insertBeerRating(String ratingJSON);

	public boolean insertBeer(String mappedBeerJSON);
}
