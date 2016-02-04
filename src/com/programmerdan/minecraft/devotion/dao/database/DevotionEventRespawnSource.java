package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventRespawnInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class DevotionEventRespawnSource extends Source {
	private static final String insertScript = "INSERT devotion_event_respawn (event_time, player_uuid, is_bed_spawn) VALUES (?, ?, ?)";
	
	public DevotionEventRespawnSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventRespawnInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		sql.setBoolean(3, info.isBedSpawn);
		
		sql.addBatch();
	}
}
