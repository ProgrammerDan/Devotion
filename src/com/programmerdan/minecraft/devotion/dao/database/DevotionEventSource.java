package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class DevotionEventSource extends Source {
	private static final String insertScript = "INSERT devotion_event (event_time, event_type, player_name, player_uuid, location_worlduuid, location_x, location_y, location_z, location_yaw, location_pitch, location_eye_worlduuid, location_eye_x, location_eye_y, location_eye_z, location_eye_yaw, location_eye_pitch, game_mode, exhaustion, food_level, saturation, total_experience, velocity_x, velocity_y, velocity_z, remaining_air, health, max_health, status_flags) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public DevotionEventSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.eventType);
		sql.setString(3, info.playerName);
		sql.setString(4, info.playerUUID);
		sql.setString(5, info.location.worldUUID);
		sql.setDouble(6, info.location.x);
		sql.setDouble(7, info.location.y);
        sql.setDouble(8, info.location.z);
		sql.setFloat(9, info.location.yaw);
		sql.setFloat(10, info.location.pitch);
		sql.setString(11, info.eyeLocation.worldUUID);
		sql.setDouble(12, info.eyeLocation.x);
		sql.setDouble(13, info.eyeLocation.y);
        sql.setDouble(14, info.eyeLocation.z);
		sql.setFloat(15, info.eyeLocation.yaw);
		sql.setFloat(16, info.eyeLocation.pitch);
		sql.setString(17, info.gameMode);
		sql.setFloat(18, info.exhaustion);
		sql.setInt(19, info.foodLevel);
		sql.setFloat(20, info.saturation);
		sql.setInt(21, info.totalExperience);
		sql.setDouble(22, info.velocityX);
		sql.setDouble(23, info.velocityY);
		sql.setDouble(24, info.velocityZ);
		sql.setInt(25, info.remainingAir);
		sql.setDouble(26, info.health);
		sql.setDouble(27, info.maxHealth);
		sql.setString(28, info.getStatusFlags());
		
		sql.addBatch();
	}
}
