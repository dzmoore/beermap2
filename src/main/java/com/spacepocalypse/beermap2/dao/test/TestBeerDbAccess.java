package com.spacepocalypse.beermap2.dao.test;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.spacepocalypse.beermap2.dao.BeerDbAccess;
import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.util.Conca;


public class TestBeerDbAccess  {
	private BeerDbAccess dbAccess;
	private Logger log4jLogger;
	
	@Before
	public void setup() {
		log4jLogger = Logger.getLogger(getClass());
		log4jLogger.setLevel(Level.INFO);
		dbAccess = new BeerDbAccess("beerdb", "root", "pwhere");
	}
	
	@Test
	public void testFindBrewery() throws InvalidParameterException, SQLException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.KEY_BREWERY_NAME, "%head%");
		
		List<MappedBeer> beers = dbAccess.findAllBeers(params);
		
		TestCase.assertNotNull(beers);
		TestCase.assertTrue(beers.size() > 0);
		
		log4jLogger.info(Conca.t("beers returned: {", beers.toString(), "}"));
	}
}
