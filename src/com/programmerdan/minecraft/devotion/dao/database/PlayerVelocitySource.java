package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerVelocityInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerVelocitySource extends Source {
	private static final String insertScript = "INSERT dev_player_velocity (trace_id, velocity_x, velocity_y, velocity_z, event_cancelled) VALUES (?, ?, ?, ?, ?)";
	
	public PlayerVelocitySource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerVelocityInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setDouble(2, info.velocityX);
		sql.setDouble(3, info.velocityY);
        sql.setDouble(4, info.velocityZ);
		sql.setBoolean(5, info.eventCancelled);
		
		sql.addBatch();
	}
}
