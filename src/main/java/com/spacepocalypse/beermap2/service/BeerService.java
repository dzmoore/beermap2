package com.spacepocalypse.beermap2.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.spacepocalypse.beermap2.domain.MappedBeer;

public class BeerService implements IBeerService {
	private List<MappedBeer> beerList;
	
	public BeerService() {
		beerList = new ArrayList<MappedBeer>();
		beerList.add(new MappedBeer() {
			@Override
			public int getId() {
				return 1;
			}
			
			@Override
			public String getName() {
				return "TestBeer1";
			}

			@Override
			public float getAbv() {
				return 7.2f;
			}
			
			@Override
			public String getDescript() {
				return "This is a test beer description.";
			}
			
		});
		
		beerList.add(new MappedBeer() {
			@Override
			public int getId() {
				return 2;
			}
			
			@Override
			public String getName() {
				return "TestBeer2";
			}

			@Override
			public float getAbv() {
				return 10.5f;
			}
			
			@Override
			public String getDescript() {
				return "This is a test beer (2) description.";
			}
			
		});
		
		beerList.add(new MappedBeer() {
			@Override
			public int getId() {
				return 3;
			}
			
			@Override
			public String getName() {
				return "TestBeer3";
			}

			@Override
			public float getAbv() {
				return 4.4f;
			}
			
			@Override
			public String getDescript() {
				return "This is a test beer (3) description.";
			}
			
		});
		
	}
	
	public List<MappedBeer> query(String beerName) {
		List<MappedBeer> beers = new ArrayList<MappedBeer>();
		for (final MappedBeer ea : beerList) {
			if (StringUtils.containsIgnoreCase(ea.getName(), beerName)) {
				beers.add(ea);
			}
		}
		
		return beers;
	}

}
