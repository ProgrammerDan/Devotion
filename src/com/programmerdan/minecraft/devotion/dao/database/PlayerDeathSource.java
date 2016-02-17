package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerDeathInfo;

public class PlayerDeathSource extends Source {
	private static final String insertScript = "INSERT dev_player_death (trace_id, new_exp, new_level, new_total_exp, keep_level, keep_inventory) VALUES (?, ?, ?, ?, ?, ?)";
	
	public PlayerDeathSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerDeathInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.newExp);
		sql.setInt(3, info.newLevel);
		sql.setInt(4, info.newTotalExp);
		sql.setBoolean(5, info.keepLevel);
		sql.setBoolean(6, info.keepInventory);
		
		sql.addBatch();
	}
}
