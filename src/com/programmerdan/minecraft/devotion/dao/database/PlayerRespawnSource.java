package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerRespawnInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerRespawnSource extends Source {
	private static final String insertScript = "INSERT dev_player_respawn (trace_id, is_bed_spawn) VALUES (?, ?)";
	
	public PlayerRespawnSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerRespawnInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setBoolean(2, info.isBedSpawn);
		
		sql.addBatch();
	}
}
