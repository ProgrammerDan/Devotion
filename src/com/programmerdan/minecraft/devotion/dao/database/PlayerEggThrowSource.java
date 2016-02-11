package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEggThrowInfo;

public class PlayerEggThrowSource extends Source {
	private static final String insertScript = "INSERT dev_player_egg_throw (trace_id, hatching, hatching_type, num_hatches) VALUES (?, ?, ?, ?)";
	
	public PlayerEggThrowSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEggThrowInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setBoolean(2, info.hatching);
		sql.setString(3, info.hatchingType);
		sql.setByte(4, info.numHatches);
		
		sql.addBatch();
	}
}
