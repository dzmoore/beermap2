package com.spacepocalypse.beermap2.service;

import java.util.List;

import com.spacepocalypse.beermap2.domain.MappedBeer;

public interface IBeerService {
	public List<MappedBeer> query(String beerName);
}
