package com.programmerdan.minecraft.devotion.siphon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Soft wrapper for Connection, might wind up discarding this.
 */
public class SiphonConnection {
	private Connection connection;

	public static final SiphonConnection FAILURE = new SiphonConnection(null);

	public SiphonConnection(Connection connection) {
		this.connection = connection;
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
		if (connection == null) return false;

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
		if (connection == null) {
			throw new SQLException("Connection not available.");
		}
        return connection.prepareStatement(sqlStatement);
    }
}

