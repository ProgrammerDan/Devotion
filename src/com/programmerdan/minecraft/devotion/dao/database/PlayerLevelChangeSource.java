package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerLevelChangeInfo;

public class PlayerLevelChangeSource extends Source {
	private static final String insertScript = "INSERT dev_player_level_change (trace_id, oldLevel, newLevel) VALUES (?, ?, ?)";
	
	public PlayerLevelChangeSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerLevelChangeInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.oldLevel);
		sql.setInt(3, info.newLevel);
		
		sql.addBatch();
	}
}
