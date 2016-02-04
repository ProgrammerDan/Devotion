package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventLoginInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class DevotionEventLoginSource extends Source {
	private static final String insertScript = "INSERT devotion_event_login (event_time, player_uuid, address, hostname, real_address) VALUES (?, ?, ?, ?, ?)";
	
	public DevotionEventLoginSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventLoginInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		sql.setString(3, info.address);
		sql.setString(4, info.hostname);
		sql.setString(5, info.realAddress);
		
		sql.addBatch();
	}
}
