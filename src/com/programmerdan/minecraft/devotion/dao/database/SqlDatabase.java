package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avaje.ebeaninternal.server.lib.sql.DataSourceException;
import com.programmerdan.minecraft.devotion.helpers.ResourceHelper;

public class SqlDatabase {
	private String host;
    private int port;
    private String db;
    private String user;
    private String password;
    private Logger logger;
    private Connection connection;
    
    private DevotionEventSource devotionEventSource;
    public DevotionEventSource getDevotionEventSource() {
    	return this.devotionEventSource;
    }
	
    public SqlDatabase(String host, int port, String db, String user, String password, Logger logger) {
        this.host = host;
        this.port = port;
        this.db = db;
        this.user = user;
        this.password = password;
        this.logger = logger;
    }
    
    public boolean connect() {
        String jdbc = "jdbc:mysql://" + host + ":" + port + "/" + db + "?user=" + user + "&password=" + password;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            throw new DataSourceException("Failed to initialize JDBC driver.");
        }
        try {
            this.connection = DriverManager.getConnection(jdbc);
            
            initDataSources();
            
            this.logger.log(Level.INFO, "Connected to database!");
            return true;
        } catch (SQLException ex) { //Error handling below:
            this.logger.log(Level.SEVERE, "Could not connnect to the database! Connection string: " + jdbc, ex);
            return false;
        }
    }
    
    private void initDataSources() {
    	this.devotionEventSource = new DevotionEventSource(this);
    }
    
    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "An error occured while closing the connection.", ex);
        }
    }
    
    public boolean isConnected() {
        try {
            return connection.isValid(5);
        } catch (SQLException ex) {
            this.logger.log(Level.SEVERE, "isConnected error!", ex);
        }
        return false;
    }
    
    public PreparedStatement prepareStatement(String sqlStatement) throws SQLException {
        return connection.prepareStatement(sqlStatement);
    }
    
    public void begin() throws SQLException {
    	this.connection.setAutoCommit(false);
    	this.devotionEventSource.startBatch();
    }
    
    public void commit() throws SQLException {
    	boolean hasUpdates = false;
    	
    	if(this.devotionEventSource.hasUpdates()) {
    		this.devotionEventSource.executeBatch();
    		hasUpdates = true;
    	}
    	
    	if(hasUpdates) {
    		this.connection.commit();
    	}
    	
    	this.connection.setAutoCommit(true);
    }
    
    public void initDb() {
    	this.logger.log(Level.INFO, "Database initialization started...");
    	
    	String script = ResourceHelper.readTextFile("resources/create_db.txt");
    	
		try {
			prepareStatement(script).execute();
    	} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.logger.log(Level.INFO, "Database initialized.");
    }
}
