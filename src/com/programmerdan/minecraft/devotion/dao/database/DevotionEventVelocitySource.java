package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventVelocityInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class DevotionEventVelocitySource extends Source {
	private static final String insertScript = "INSERT devotion_event_velocity (event_time, player_uuid, velocity_x, velocity_y, velocity_z, event_cancelled) VALUES (?, ?, ?, ?, ?, ?)";
	
	public DevotionEventVelocitySource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventVelocityInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		
		sql.setDouble(3, info.velocityX);
		sql.setDouble(4, info.velocityY);
        sql.setDouble(5, info.velocityZ);
		sql.setBoolean(6, info.eventCancelled);
		
		sql.addBatch();
	}
}
