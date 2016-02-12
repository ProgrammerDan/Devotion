package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerGameModeChangeInfo;

public class PlayerGameModeChangeSource extends Source {
	private static final String insertScript = "INSERT dev_player_game_mode_change (trace_id, new_game_mode, event_cancelled) VALUES (?, ?, ?)";
	
	public PlayerGameModeChangeSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerGameModeChangeInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setString(2, info.newGameMode);
		sql.setBoolean(3, info.eventCancelled);
		
		sql.addBatch();
	}
}
