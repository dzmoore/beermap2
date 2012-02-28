package com.spacepocalypse.beermap2.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.spacepocalypse.util.Conca;

public class DbExecutor {
	private Logger log4jLogger;
	protected Connection dbConnection;
	protected String username;
	protected String password;
	protected String dbName;
	
	public DbExecutor(String dbName, String username, String password) {
		log4jLogger = Logger.getLogger(DbExecutor.class);
		this.username = username;
		this.password = password;
		this.dbName = dbName;
	}

	public void setDbConnection(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public Connection getDbConnection() {
		if (dbConnection == null) {
			// load the MySQL driver
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				log4jLogger.error(e.getMessage());
				e.printStackTrace();
			}
			
			// Setup the connection with the DB
			try {
				dbConnection = DriverManager.getConnection(
					Conca.t("jdbc:mysql://localhost/", dbName, "?user=", username, "&password=", password, "&autoReconnect=true")
				);
				
			} catch (SQLException e) {
				log4jLogger.error("Error establishing database connection", e);
			}
		}
		return dbConnection;
	}
	
	public void closeAndReconnect() {
		synchronized (dbConnection) {

			Level origLvl = log4jLogger.getLevel();
			log4jLogger.setLevel(Level.INFO);
			if (dbConnection != null) {
				log4jLogger.info("Attempting to close existing DB connection");
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log4jLogger.error(e.getMessage());
					e.printStackTrace();
				}
			}
			log4jLogger.info("Nulling existing DB connection reference.");
			dbConnection = null;

			log4jLogger.info("Reconnecting.");
			getDbConnection();

			log4jLogger.setLevel(origLvl);
		}
	}
	
}
