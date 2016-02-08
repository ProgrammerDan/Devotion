package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEventToggleInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerEventToggleSource extends Source {
	private static final String insertScript = "INSERT dev_player_event_toggle (trace_id, toggle_value, event_cancelled) VALUES (?, ?, ?)";
	
	public PlayerEventToggleSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEventToggleInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setBoolean(2, info.toggleValue);
		sql.setBoolean(3, info.eventCancelled);
		
		sql.addBatch();
	}
}
