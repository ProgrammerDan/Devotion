package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventTeleportInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class DevotionEventTeleportSource extends Source {
	private static final String insertScript = "INSERT devotion_event_teleport (event_time, player_uuid, cause) VALUES (?, ?, ?)";
	
	public DevotionEventTeleportSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventTeleportInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		
		if(info.cause != null) {
			sql.setString(3, info.cause); 
		} else {
			sql.setNull(3, Types.VARCHAR);
		}
		
		sql.addBatch();
	}
}