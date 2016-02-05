package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventQuitInfo;

public class DevotionEventQuitSource extends Source {
	private static final String insertScript = "INSERT devotion_event_quit (event_time, player_uuid, quit_message) VALUES (?, ?, ?)";
	
	public DevotionEventQuitSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventQuitInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		
		if(info.quitMessage != null) {
			sql.setString(3, info.quitMessage); 
		} else {
			sql.setNull(3, Types.VARCHAR);
		}
				
		sql.addBatch();
	}
}
