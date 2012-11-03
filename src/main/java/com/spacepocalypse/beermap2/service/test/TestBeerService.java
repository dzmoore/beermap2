package com.spacepocalypse.beermap2.service.test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.spacepocalypse.beermap2.dao.BeerDbAccess;
import com.spacepocalypse.beermap2.domain.MappedBeer;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.beermap2.service.IBeerService;
import com.spacepocalypse.util.Conca;
import com.spacepocalypse.util.StrUtl;

public class TestBeerService {
    private IBeerService beerService;
    private BeerDbAccess dbAccess;
    private Logger log4jLogger;

    @Before
    public void setup() {
        System.setProperty("log4j.configuration", "file:./src/main/resources/log4j.properties");
        
        log4jLogger = Logger.getLogger(getClass());
        log4jLogger.setLevel(Level.INFO);
        
        Logger.getLogger(BeerDbAccess.class).setLevel(Level.INFO);
        
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/beer-service.xml");
        beerService = (IBeerService) ctx.getBean("beerService");
        
        ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/dbaccess.xml");
        dbAccess = (BeerDbAccess)ctx.getBean("dbAccess");
    }
    
    @Test
    public void testFindBeer() {
        int id = 1;
        final MappedBeer beer = beerService.findBeerById(id);
        
        TestCase.assertNotNull(beer);
        TestCase.assertTrue(beer.getId() == id);
    }
    
    @Test
    public void testInsertBeer() {
        final int userId = 1;
        final String beerName = "TestBeer123";
        final float abv = 7.1f;
        final String descript = "Description here.";

        MappedBeer beer = new MappedBeer();
        beer.setName(beerName);
        beer.setAbv(abv);
        beer.setDescript(descript);
        
        final int newBeerId = beerService.insertBeer(beer, userId);
        
        TestCase.assertTrue(newBeerId != Constants.INVALID_ID);
        
        beer = beerService.findBeerById(newBeerId);
        
        TestCase.assertTrue(beer.getId() == newBeerId);
        TestCase.assertTrue(StringUtils.equals(beer.getName(), beerName));
        TestCase.assertTrue(beer.getAbv() == abv);
        TestCase.assertTrue(StringUtils.equals(beer.getDescript(), descript));
    }
    
    private void deleteUser(final String username) {
        final String roleDeleteQuery = Conca.t(
            "delete from user_roles ",
            "using user_roles join user on (user_roles.userFk = user.id) ",
            "where lower(user.username) = lower(?)"
        );
        
        PreparedStatement stmt;
        
        try {
            stmt = dbAccess.getDbConnection().prepareStatement(roleDeleteQuery);
            stmt.setString(1, username);
            final int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated != 1) {
                log4jLogger.warn("deleteUser may have failed. while deleting role row: rowsUpdated == " + rowsUpdated + " for username [" + username + "]");
                
            } 

        } catch (Exception e) {
            log4jLogger.error("Error occurred while attempting to delete user [" + username + "]", e);
        }
        
        final String query = 
                "delete from user " +
                "where lower(user.username) = lower(?)";
        
        try {
            stmt = dbAccess.getDbConnection().prepareStatement(query);
            stmt.setString(1, username);
            final int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated != 1) {
                log4jLogger.warn("deleteUser may have failed. rowsUpdated == " + rowsUpdated + " for username [" + username + "]");
                
            } else {
                log4jLogger.debug("successfully deleted user [" + username + "]");
            }

        } catch (Exception e) {
            log4jLogger.error("Error occurred while attempting to delete user [" + username + "]", e);
        }
        
    }
    
    private MappedUser createTestUser(final String password) {
        final MappedUser user = dbAccess.createUser(StrUtl.trunc(System.nanoTime()+"testuser", 39), "salt", password);
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
        
        return user;
    }
    
    @Test
    public void testUpdateBeer() {
        final MappedUser user = createTestUser("password");
            
        final String beerName = "TestBeer1234";
        final float abv = 7.1f;
        final String descript = "Description here.";

        MappedBeer beer = new MappedBeer();
        beer.setName(beerName);
        beer.setAbv(abv);
        beer.setDescript(descript);
        
        final int newBeerId = beerService.insertBeer(beer, user.getId());
        
        TestCase.assertTrue(newBeerId != Constants.INVALID_ID);
        
        beer = beerService.findBeerById(newBeerId);
        
        TestCase.assertTrue(beer.getId() == newBeerId);
        TestCase.assertTrue(StringUtils.equals(beer.getName(), beerName));
        TestCase.assertTrue(beer.getAbv() == abv);
        TestCase.assertTrue(StringUtils.equals(beer.getDescript(), descript));
        
        float abv2 = 5.5f;
        String name2 = "new name";
        String descript2 = "changed it";

        beer.setAbv(abv2);
        beer.setDescript(descript2);
        beer.setName(name2);
        
        beerService.updateBeer(beer, user);
        
        TestCase.assertTrue(beer.getId() == newBeerId);
        TestCase.assertTrue(StringUtils.equals(beer.getName(), name2));
        TestCase.assertTrue(beer.getAbv() == abv2);
        TestCase.assertTrue(StringUtils.equals(beer.getDescript(), descript2));
        
        deleteBeer(newBeerId);
        deleteUser(user.getUsername());
        
    }
    
    private void deleteBeer(final int beerId) {
        try {
            final PreparedStatement ps = dbAccess.getDbConnection().prepareStatement("delete from beers where id = ?");
            ps.setInt(1, beerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
            
    }
}
