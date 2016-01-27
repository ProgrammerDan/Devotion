package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventInfo;

public class DevotionEventSource extends Source {
	private static final String insertScript = "INSERT devotion_event (devotion_event_id, event_time, event_type, player_name, player_uuid, location_worlduuid, location_x, location_y, location_z, location_yaw, location_pitch, location_eye_worlduuid, location_eye_x, location_eye_y, location_eye_z, location_eye_yaw, location_eye_pitch, game_mode, exhaustion, food_level, saturation, total_experience, velocity_x, velocity_y, velocity_z, remaining_air, health, max_health, status_flags) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public DevotionEventSource(SqlDatabase db) {
		super(db);
	}
	
	@Override
	protected String getMaxIdQuery() {
		return "SELECT MAX(devotion_event_id) FROM devotion_event";
	}
		
	public void insert(DevotionEventInfo info) throws SQLException {
		info.devotionEventId = generateNextId();
		
		PreparedStatement sql = getSql(insertScript);

		sql.setInt(1, info.devotionEventId);
		sql.setTimestamp(2, info.eventTime);
		sql.setString(3, info.eventType);
		sql.setString(4, info.playerName);
		sql.setString(5, info.playerUUID);
		sql.setString(6, info.location.worldUUID);
		sql.setDouble(7, info.location.x);
		sql.setDouble(8, info.location.y);
        sql.setDouble(9, info.location.z);
		sql.setFloat(10, info.location.yaw);
		sql.setFloat(11, info.location.pitch);
		sql.setString(12, info.eyeLocation.worldUUID);
		sql.setDouble(13, info.eyeLocation.x);
		sql.setDouble(14, info.eyeLocation.y);
        sql.setDouble(15, info.eyeLocation.z);
		sql.setFloat(16, info.eyeLocation.yaw);
		sql.setFloat(17, info.eyeLocation.pitch);
		sql.setString(18, info.gameMode);
		sql.setFloat(19, info.exhaustion);
		sql.setInt(20, info.foodLevel);
		sql.setFloat(21, info.saturation);
		sql.setInt(22, info.totalExperience);
		sql.setDouble(23, info.velocityX);
		sql.setDouble(24, info.velocityY);
		sql.setDouble(25, info.velocityZ);
		sql.setInt(26, info.remainingAir);
		sql.setDouble(27, info.health);
		sql.setDouble(28, info.maxHealth);
		sql.setString(29, info.getStatusFlags());
		
		sql.addBatch();
	}
}
