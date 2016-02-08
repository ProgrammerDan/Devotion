package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEventQuitInfo;

public class PlayerEventQuitSource extends Source {
	private static final String insertScript = "INSERT dev_player_event_quit (trace_id, quit_message) VALUES (?, ?)";
	
	public PlayerEventQuitSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEventQuitInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);

		if(info.quitMessage != null) {
			sql.setString(2, info.quitMessage); 
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
				
		sql.addBatch();
	}
}
