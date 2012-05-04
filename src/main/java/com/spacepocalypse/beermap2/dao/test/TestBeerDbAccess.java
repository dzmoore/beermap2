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
import org.junit.Ignore;
import org.junit.Test;

import com.spacepocalypse.beermap2.dao.BeerDbAccess;
import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedBrewery;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.util.Conca;


public class TestBeerDbAccess  {
	private BeerDbAccess dbAccess;
	private Logger log4jLogger;
	
	@Before
	public void setup() {
		log4jLogger = Logger.getLogger(getClass());
		log4jLogger.setLevel(Level.INFO);
		dbAccess = new BeerDbAccess("beerdb", "root", "password");
	}
	
	@Ignore
	@Test
	public void testFindBrewery() throws InvalidParameterException, SQLException {
		Map<String, String> params = new HashMap<String, String>();
		params.put(Constants.QUERY_KEY_BREWERY_NAME, "%head%");
		
		List<MappedBeer> beers = dbAccess.findAllBeers(params);
		
		TestCase.assertNotNull(beers);
		TestCase.assertTrue(beers.size() > 0);
		
		log4jLogger.info(Conca.t("beers returned: {", beers.toString(), "}"));
	}
	
	@Test
    public void testFindAllBeersForUser() {
        final List<MappedBeer> beers = dbAccess.findBeersForUserId(1);
        
        TestCase.assertNotNull(beers);
        TestCase.assertNotNull(beers.size() > 0);
        
        log4jLogger.info(Conca.t("beers returned from query: ", beers.size(), " result: [", beers, "]"));
    }
	
	@Ignore
	@Test
	public void testFindAllBreweries() {
	    final List<MappedBrewery> breweries = dbAccess.findAllBreweries();
	    
	    TestCase.assertNotNull(breweries);
	    TestCase.assertNotNull(breweries.size() > 0);
	    
	    log4jLogger.info(Conca.t("breweries returned from query: ", breweries.size()));
	}
}
