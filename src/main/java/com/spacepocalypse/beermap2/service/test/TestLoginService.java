package com.spacepocalypse.beermap2.service.test;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.spacepocalypse.beermap2.dao.BeerDbAccess;
import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.beermap2.service.ILoginService;
import com.spacepocalypse.beermap2.service.LoginService.AuthData;
import com.spacepocalypse.beermap2.service.LoginService.AuthState;
import com.spacepocalypse.util.Conca;
import com.spacepocalypse.util.StrUtl;

public class TestLoginService {
    private ILoginService loginService;
    private BeerDbAccess dbAccess;
    private Logger log4jLogger;

    @Before
    public void setup() {
        System.setProperty("log4j.configuration", "file:./src/main/resources/log4j.properties");
        
        log4jLogger = Logger.getLogger(getClass());
        log4jLogger.setLevel(Level.INFO);
        
        Logger.getLogger(BeerDbAccess.class).setLevel(Level.INFO);
        
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/login-service.xml");
        loginService = (ILoginService) ctx.getBean("loginService");
        
        ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/dbaccess.xml");
        dbAccess = (BeerDbAccess)ctx.getBean("dbAccess");
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
    
    @Test
    public void testDoesUserExist() {
        final MappedUser testUser = createTestUser();
        
        final String username = testUser.getUsername();
        
        TestCase.assertTrue(loginService.doesUserExist(username));
        
        deleteUser(username);
        
        TestCase.assertFalse(loginService.doesUserExist(username));
    }
    
    
    @Test
    public void testUserLogin() {
        final String password = "testpassword";
        final MappedUser testUser = createTestUser(password);
        
        AuthData authData = loginService.authUser(testUser.getUsername(), password);
        TestCase.assertNotNull(authData);
        TestCase.assertTrue(authData.getState() == AuthState.SUCCESS);
        TestCase.assertTrue(authData.getAuthTimeoutMs() > 0L);
        TestCase.assertTrue(authData.getUser().getId() == testUser.getId());

        authData = loginService.authUser(testUser.getUsername(), password+"wrong");
        TestCase.assertNotNull(authData);
        TestCase.assertTrue(authData.getState() == AuthState.ERROR);
        TestCase.assertFalse(authData.getUser().getId() == testUser.getId());
        
        deleteUser(testUser.getUsername());
    }
    
    private MappedUser createTestUser() {
        return createTestUser("test");
    }
    
    private MappedUser createTestUser(final String password) {
        MappedUser user = loginService.createUser(StrUtl.trunc(System.nanoTime()+"testuser", 39), password);
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
        
        return user;
    }
    
    @Test
    public void testAddUserRole() {
        MappedUser user = createTestUser();
        
        TestCase.assertTrue(loginService.addUserRole(user.getId(), "admin"));
        
        user = loginService.findUserByName(user.getUsername());
        
        TestCase.assertNotNull(user.getRoles());
        TestCase.assertTrue(user.getRoles().size() > 1);  // initial role + admin
        TestCase.assertTrue(user.getRoles().contains("admin"));
        
        deleteUser(user.getUsername());
    }
    
    @Test
    public void testRemoveUserRole() {
        MappedUser user = createTestUser();

        TestCase.assertTrue(user.getRoles().size() == 1);
        
        TestCase.assertTrue(loginService.removeUserRole(user.getId(), user.getRoles().get(0)));
        
        user = loginService.findUserByName(user.getUsername());
        
        TestCase.assertTrue(user.getRoles().size() == 0);
        
        deleteUser(user.getUsername());
    }
    
    
    
    @Ignore
    @Test
    public void testGenerateUniqueSalt() {
        final int count = 50;
        final String pw = "test";
        
        final List<MappedUser> testUsers = new ArrayList<MappedUser>(count);
        final Set<String> salts = new HashSet<String>();
        
        boolean failed = false;
        String failedSalt = "";
        for (int i = 0; i < count; i++) {
            final MappedUser testUser = createTestUser(pw);
            testUsers.add(testUser);
            final String currentSalt = dbAccess.findSalt(testUser.getUsername());
           
            if (!salts.add(currentSalt)) {
                failedSalt = currentSalt;
                break;
            }
        }
        
        for (final MappedUser ea : testUsers) {
            deleteUser(ea.getUsername());
        }
        
        if (failed) {
            TestCase.fail(Conca.t("non-unique salt generated.  users [", testUsers, "] salts [", salts, "] current salt [", failedSalt, "]"));
        }
        
    }
    
}
