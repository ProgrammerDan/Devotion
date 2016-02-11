package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerExpChangeInfo;

public class PlayerExpChangeSource extends Source {
	private static final String insertScript = "INSERT dev_player_exp_change (trace_id, amount) VALUES (?, ?)";
	
	public PlayerExpChangeSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerExpChangeInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.amount);
		
		sql.addBatch();
	}
}
