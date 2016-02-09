package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerLoginInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerLoginSource extends Source {
	private static final String insertScript = "INSERT dev_player_login (trace_id, address, hostname, real_address, result, kick_message) VALUES (?, ?, ?, ?, ?, ?)";
	
	public PlayerLoginSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerLoginInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setString(2, info.address);
		sql.setString(3, info.hostname);
		sql.setString(4, info.realAddress);
		sql.setString(5, info.result);
		sql.setString(6, info.kickMessage);
		sql.addBatch();
	}
}
