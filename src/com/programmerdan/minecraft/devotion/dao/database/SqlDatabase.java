package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.avaje.ebeaninternal.server.lib.sql.DataSourceException;
import com.programmerdan.minecraft.devotion.util.ResourceHelper;

/**
 * @author Aleksey Terzi
 *
 */

public class SqlDatabase {
	private String host;
    private int port;
    private String db;
    private String user;
    private String password;
    private Logger logger;
    private Connection connection;
    
    private ArrayList<Source> sourceList;
    
    private PlayerEventSource playerEventSource;
    public PlayerEventSource getPlayerEventSource() {
    	return this.playerEventSource;
    }
	
    private PlayerEventLoginSource playerEventLoginSource;
    public PlayerEventLoginSource getPlayerEventLoginSource() {
    	return this.playerEventLoginSource;
    }
    
    private PlayerEventQuitSource playerEventQuitSource;
    public PlayerEventQuitSource getPlayerEventQuitSource() {
    	return this.playerEventQuitSource;
    }

    private PlayerEventInteractSource playerEventInteractSource;
    public PlayerEventInteractSource getPlayerEventInteractSource() {
    	return this.playerEventInteractSource;
    }

    private PlayerEventKickSource playerEventKickSource;
    public PlayerEventKickSource getPlayerEventKickSource() {
    	return this.playerEventKickSource;
    }

    private PlayerEventTeleportSource playerEventTeleportSource;
    public PlayerEventTeleportSource getPlayerEventTeleportSource() {
    	return this.playerEventTeleportSource;
    }

    private PlayerEventRespawnSource playerEventRespawnSource;
    public PlayerEventRespawnSource getPlayerEventRespawnSource() {
    	return this.playerEventRespawnSource;
    }

    private PlayerEventToggleSource playerEventToggleSource;
    public PlayerEventToggleSource getPlayerEventToggleSource() {
    	return this.playerEventToggleSource;
    }

    private PlayerEventVelocitySource playerEventVelocitySource;
    public PlayerEventVelocitySource getPlayerEventVelocitySource() {
    	return this.playerEventVelocitySource;
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
    	this.sourceList = new ArrayList<Source>();
    	
    	this.sourceList.add(this.playerEventSource = new PlayerEventSource(this));
    	this.sourceList.add(this.playerEventLoginSource = new PlayerEventLoginSource(this));
    	this.sourceList.add(this.playerEventInteractSource = new PlayerEventInteractSource(this));
    	this.sourceList.add(this.playerEventKickSource = new PlayerEventKickSource(this));
    	this.sourceList.add(this.playerEventQuitSource = new PlayerEventQuitSource(this));
    	this.sourceList.add(this.playerEventTeleportSource = new PlayerEventTeleportSource(this));
    	this.sourceList.add(this.playerEventRespawnSource = new PlayerEventRespawnSource(this));
    	this.sourceList.add(this.playerEventToggleSource = new PlayerEventToggleSource(this));
    	this.sourceList.add(this.playerEventVelocitySource = new PlayerEventVelocitySource(this));
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
    	
    	for(Source source : this.sourceList) {
    		source.startBatch();
    	}
    }
    
    public void commit() throws SQLException {
    	boolean hasUpdates = false;
    	
    	for(Source source : this.sourceList) {
    		if(source.hasUpdates()) {
    			source.executeBatch();
    			hasUpdates = true;
    		}
    	}
    	
    	if(hasUpdates) {
    		this.connection.commit();
    	}
    	
    	this.connection.setAutoCommit(true);
    }
    
    public boolean initDb() {
    	this.logger.log(Level.INFO, "Database initialization started...");
    	
    	ArrayList<String> list = ResourceHelper.readScript("/create_db.txt");
    	
		for(String script : list) {
			try {
				prepareStatement(script).execute();
	    	} catch (SQLException e) {
	    		this.logger.log(Level.SEVERE, "Database is NOT initialized.");
	    		this.logger.log(Level.SEVERE, "Failed script: \n" + script);
				e.printStackTrace();
				return false;
			}
		}
		
		this.logger.log(Level.INFO, "Database initialized.");
		return true;
    }
}
