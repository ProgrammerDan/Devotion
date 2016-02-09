package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerRespawnInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerRespawnSource extends Source {
	private static final String insertScript = "INSERT dev_player_respawn (is_bed_spawn, trace_id) VALUES (?, ?)";
	
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
