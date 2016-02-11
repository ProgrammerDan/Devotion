package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerFishInfo;

public class PlayerFishSource extends Source {
	private static final String insertScript = "INSERT dev_player_fish (trace_id, caught_entity, exp, state, event_cancelled) VALUES (?, ?, ?, ?, ?)";
	
	public PlayerFishSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerFishInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setString(2, info.caughtEntity);
		sql.setInt(3, info.exp);
		sql.setString(4, info.state); 
		sql.setBoolean(5, info.eventCancelled);
		
		sql.addBatch();
	}
}
