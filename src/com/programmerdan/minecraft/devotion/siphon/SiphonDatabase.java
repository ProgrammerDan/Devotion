package com.programmerdan.minecraft.devotion.siphon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Aleksey Terzi
 * @author ProgrammerDan
 *
 */

public class SiphonDatabase {
	private String host;
    private int port;
    private String db;
    private String user;
    private String password;
    private Connection connection;
    
    public SiphonDatabase(String host, int port, String db, String user, String password) {
        this.host = host;
        this.port = port;
        this.db = db;
        this.user = user;
        this.password = password;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            throw new SiphonFailure("Failed to initialize JDBC driver.");
        }
    }
    
    public boolean connect() {
        String jdbc = "jdbc:mysql://" + host + ":" + port + "/" + db + "?user=" + user + "&password=" + password;
        
        try {
            this.connection = DriverManager.getConnection(jdbc);
            
            System.out.println("Connected to database!");
            return true;
        } catch (SQLException ex) { //Error handling below:
            throw new SiphonFailure("Could not connnect to the database! Connection string: " + jdbc, ex);
        }
    }
    
    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println("An error occured while closing the connection.");
			ex.printStackTrace();
        }
    }
    
    public boolean isConnected() {
        try {
            return connection.isValid(5);
        } catch (SQLException ex) {
            System.err.println("isConnected error!");
			ex.printStackTrace();
        }
        return false;
    }
    
    public Connection getConnection() {
    	return this.connection;
    }
    
    public PreparedStatement prepareStatement(String sqlStatement) throws SQLException {
        return connection.prepareStatement(sqlStatement);
    }
}

