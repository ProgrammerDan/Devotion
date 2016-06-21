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

	// So some testing reveals that constructing the slice table using precise times will be very expensive on the whole.
	// The goal of Siphon is to get the data out; not to be precise in its splits.
	// With that in mind we'll go a new route; leveraging dev_player_id field within dev_player to find in O(ln n) where 
	// to "stop" our retrieval (within some target "nearness" of real time) and use those dev_player_id values to 
	// build the slice_table. If follows my testing, this will be _very_ fast.
	public static final String BOUNDS = "SELECT min(dev_player_id), max(dev_player_id) FROM dev_player";
	public static final String SAMPLE_DATE = "SELECT event_time FROM dev_player WHERE dev_player_id = ?";
	// using the above three tests, and assuming that _roughly_ event_time is monotonically increasing w.r.t dev_player_id, 
	// we should be able to get very close to precise with only a few samples.
	public static final String REMOVE_SLICE_INDEX = "DROP INDEX IF EXISTS slice_table_idx ON slice_table";
	public static final String GET_SLICE_TABLE = "CREATE TABLE IF NOT EXISTS slice_table (trace_id VARCHAR(36) NOT NULL) SELECT trace_id FROM dev_player WHERE dev_player_id <= ?";
	public static final String REMOVE_SLICE_TABLE = "DROP TABLE slice_table";
	public static final String ADD_SLICE_INDEX = "CREATE INDEX IF NOT EXISTS slice_table_idx ON slice_table (trace_id)";
	public static final String GENERAL_SELECT = "SELECT * FROM {0} WHERE trace_id IN (SELECT * FROM slice_table)";
	public static final String FILE_SELECT = "SELECT * FROM {0} WHERE trace_id IN (SELECT * FROM slice_table) INTO OUTFILE '/tmp/{0}_{1}.dat' FIELDS TERMINATED BY \";\" OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'";
	public static final String GENERAL_DELETE = "DELETE FROM {0} WHERE trace_id IN (SELECT * FROM slice_table)";


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

