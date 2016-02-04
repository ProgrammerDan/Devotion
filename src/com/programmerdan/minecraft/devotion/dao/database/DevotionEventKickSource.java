package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventKickInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class DevotionEventKickSource extends Source {
	private static final String insertScript = "INSERT devotion_event_interact (event_time, player_uuid, leave_message, kick_reason) VALUES (?, ?, ?, ?)";
	
	public DevotionEventKickSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventKickInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		
		if(info.leaveMessage != null) {
			sql.setString(3, info.leaveMessage); 
		} else {
			sql.setNull(3, Types.VARCHAR);
		}
		
		if(info.kickReason != null) {
			sql.setString(4, info.kickReason);
		} else {
			sql.setNull(4, Types.VARCHAR);
		}
		
		sql.addBatch();
	}
}