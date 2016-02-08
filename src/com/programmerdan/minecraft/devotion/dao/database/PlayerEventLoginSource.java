package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEventLoginInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerEventLoginSource extends Source {
	private static final String insertScript = "INSERT dev_player_event_login (trace_id, address, hostname, real_address, result, kick_message) VALUES (?, ?, ?, ?, ?, ?)";
	
	public PlayerEventLoginSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEventLoginInfo info) throws SQLException {
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
