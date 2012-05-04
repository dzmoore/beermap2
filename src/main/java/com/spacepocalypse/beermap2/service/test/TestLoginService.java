package com.spacepocalypse.beermap2.service.test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.spacepocalypse.beermap2.domain.MappedUser;
import com.spacepocalypse.beermap2.service.Constants;
import com.spacepocalypse.beermap2.service.LoginService;
import com.spacepocalypse.beermap2.service.LoginService.AuthData;
import com.spacepocalypse.beermap2.util.security.SimplePasswordTools;

public class TestLoginService {
    private LoginService loginService;
    private Logger log4jLogger;
    
    @Before
    public void setup() {
        System.setProperty("log4j.configuration", "file:./src/main/resources/log4j.properties");
        
        log4jLogger = Logger.getLogger(getClass());
        log4jLogger.setLevel(Level.INFO);
        
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/android-service.xml");
        loginService = (LoginService) ctx.getBean("loginService");
    }
    
    @Test
    public void createTestUser() {
        MappedUser user = loginService.createUser("dom", "mmmbeer");
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
    }
    
    @Ignore
    @Test
    public void generateSaltAndHashForTestUser() {
        String uniqueSalt = loginService.getUniqueSalt();
        System.out.println("salt="+uniqueSalt);
        
        try {
            String hashPass = SimplePasswordTools.hashPassAndSalt("test", uniqueSalt);
            System.out.println("hashPass="+hashPass);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testTestUserLogin() {
        String username = "test";
        String password = "test";
        
        AuthData authData = loginService.authUser(username, password);
        
        TestCase.assertNotNull(authData);
        
        MappedUser user = authData.getUser();
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
    }
    
}
