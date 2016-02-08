package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventToggleInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class DevotionEventToggleSource extends Source {
	private static final String insertScript = "INSERT devotion_event_toggle (event_time, player_uuid, toggle_value, event_cancelled, trace_id) VALUES (?, ?, ?, ?, ?)";
	
	public DevotionEventToggleSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventToggleInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		sql.setBoolean(3, info.toggleValue);
		sql.setBoolean(4, info.eventCancelled);
		sql.setString(5, info.trace_id);
		
		sql.addBatch();
	}
}
