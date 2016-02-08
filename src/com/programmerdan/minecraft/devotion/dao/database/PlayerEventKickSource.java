package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEventKickInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerEventKickSource extends Source {
	private static final String insertScript = "INSERT player_event_kick (trace_id, leave_message, kick_reason) VALUES (?, ?, ?)";
	
	public PlayerEventKickSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEventKickInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		if(info.leaveMessage != null) {
			sql.setString(2, info.leaveMessage); 
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
		
		if(info.kickReason != null) {
			sql.setString(3, info.kickReason);
		} else {
			sql.setNull(3, Types.VARCHAR);
		}
		
		sql.addBatch();
	}
}