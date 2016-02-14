package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerItemHeldInfo;

public class PlayerItemHeldSource extends Source {
	private static final String insertScript = "INSERT dev_player_item_consume (trace_id, previousSlot, newSlot, event_cancelled) VALUES (?, ?, ?, ?)";
	
	public PlayerItemHeldSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerItemHeldInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.previousSlot);
		sql.setInt(3, info.newSlot);
		sql.setBoolean(4, info.eventCancelled);
		
		sql.addBatch();
	}
}
