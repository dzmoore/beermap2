package com.spacepocalypse.beermap2.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.spacepocalypse.util.Conca;

public class DbExecutor {
	private Logger log4jLogger;
	protected String username;
	protected String password;
	protected String dbName;
	protected DataSource datasource;
	
	public DbExecutor(String dbName, String username, String password) {
		log4jLogger = Logger.getLogger(DbExecutor.class);
		this.username = username;
		this.password = password;
		this.dbName = dbName;
		
		 PoolProperties p = new PoolProperties();
         p.setUrl(Conca.t("jdbc:mysql://localhost/", dbName, "?autoReconnect=true"));
         p.setDriverClassName("com.mysql.jdbc.Driver");
         p.setUsername(username);
         p.setPassword(password);
         p.setJmxEnabled(true);
         p.setTestWhileIdle(false);
         p.setTestOnBorrow(true);
         p.setValidationQuery("SELECT 1");
         p.setTestOnReturn(false);
         p.setValidationInterval(30000);
         p.setTimeBetweenEvictionRunsMillis(30000);
         p.setMaxActive(100);
         p.setInitialSize(10);
         p.setMaxWait(10000);
         p.setRemoveAbandonedTimeout(60);
         p.setMinEvictableIdleTimeMillis(30000);
         p.setMinIdle(10);
         p.setLogAbandoned(false);
         p.setRemoveAbandoned(true);
         p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
           "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
         
         datasource = new DataSource();
         datasource.setPoolProperties(p);
         
	}

	public synchronized Connection getDbConnection() {
	    try {
            return datasource.getConnection();
        
	    } catch (SQLException e) {
            log4jLogger.error("error getting connection from datasource", e);
        }
	    
	    return null;
	}
	
}
