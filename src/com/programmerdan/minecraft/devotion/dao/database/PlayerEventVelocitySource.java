package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEventVelocityInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerEventVelocitySource extends Source {
	private static final String insertScript = "INSERT player_event_velocity (trace_id, velocity_x, velocity_y, velocity_z, event_cancelled) VALUES (?, ?, ?, ?, ?)";
	
	public PlayerEventVelocitySource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEventVelocityInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setDouble(2, info.velocityX);
		sql.setDouble(3, info.velocityY);
        sql.setDouble(4, info.velocityZ);
		sql.setBoolean(5, info.eventCancelled);
		
		sql.addBatch();
	}
}
