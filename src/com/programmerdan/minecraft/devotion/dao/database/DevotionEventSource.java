package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventInfo;

public class DevotionEventSource {
	private static final String insertScript = "INSERT devotion_event (event_utctime, event_type, player_name, player_uuid, location_worlduuid, location_x, location_y, location_z, location_yaw, location_pitch, location_eye_worlduuid, location_eye_x, location_eye_y, location_eye_z, location_eye_yaw, location_eye_pitch, game_mode, exhaustion, food_level, saturation, total_experience, velocity_x, velocity_y, velocity_z, remaining_air, health, max_health, status_flags) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private SqlDatabase db;
	private PreparedStatement sql;
	
	public boolean hasUpdates() {
		return this.sql != null;
	}
	
	public DevotionEventSource(SqlDatabase db) {
		this.db = db;
	}
		
	public void insert(DevotionEventInfo info) throws SQLException {
		if(this.sql == null) {
			this. sql = this.db.prepareStatement(insertScript);
		}

		sql.setDate(1, info.eventUtcTime);
		sql.setString(2, info.eventType);
		sql.setString(3, info.playerName);
		sql.setString(4, info.playerUUID);
		sql.setString(5, info.location.worldUUID);
		sql.setDouble(5, info.location.x);
		sql.setDouble(6, info.location.y);
        sql.setDouble(7, info.location.z);
		sql.setFloat(8, info.location.yaw);
		sql.setFloat(9, info.location.pitch);
		sql.setString(10, info.eyeLocation.worldUUID);
		sql.setDouble(11, info.eyeLocation.x);
		sql.setDouble(12, info.eyeLocation.y);
        sql.setDouble(13, info.eyeLocation.z);
		sql.setFloat(14, info.eyeLocation.yaw);
		sql.setFloat(15, info.eyeLocation.pitch);
		sql.setString(16, info.gameMode);
		sql.setFloat(17, info.exhaustion);
		sql.setInt(18, info.foodLevel);
		sql.setFloat(19, info.saturation);
		sql.setInt(20, info.totalExperience);
		sql.setDouble(21, info.velocityX);
		sql.setDouble(22, info.velocityY);
		sql.setDouble(23, info.velocityZ);
		sql.setInt(24, info.remainingAir);
		sql.setDouble(25, info.health);
		sql.setDouble(26, info.maxHealth);
		sql.setString(27, info.getStatusFlags());
		
		sql.addBatch();
	}
	
	public void startBatch() {
		this.sql = null;
	}
	
	public void executeBatch() throws SQLException {
		this.sql.executeBatch();
		this.sql.close();
		this.sql = null;
	}
}
