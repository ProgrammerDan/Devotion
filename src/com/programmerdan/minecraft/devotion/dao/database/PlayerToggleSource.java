package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerToggleInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerToggleSource extends Source {
	private static final String insertScript = "INSERT dev_player_toggle (trace_id, toggle_value, event_cancelled) VALUES (?, ?, ?)";
	
	public PlayerToggleSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerToggleInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setBoolean(2, info.toggleValue);
		sql.setBoolean(3, info.eventCancelled);
		
		sql.addBatch();
	}
}
