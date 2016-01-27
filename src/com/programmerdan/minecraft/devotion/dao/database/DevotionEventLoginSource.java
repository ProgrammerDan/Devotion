package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventLoginInfo;

public class DevotionEventLoginSource extends Source {
	private static final String insertScript = "INSERT devotion_event_login (devotion_event_login_id, devotion_event_id, address, hostname, real_address) VALUES (?, ?, ?, ?, ?)";
	
	public DevotionEventLoginSource(SqlDatabase db) {
		super(db);
	}
	
	@Override
	protected String getMaxIdQuery() {
		return "SELECT MAX(devotion_event_login_id) FROM devotion_event_login";
	}
		
	public void insert(DevotionEventLoginInfo info) throws SQLException {
		info.devotionEventLoginId = generateNextId();
		
		PreparedStatement sql = getSql(insertScript);

		sql.setInt(1, info.devotionEventLoginId);
		sql.setInt(2, info.devotionEventId);
		sql.setString(3, info.address);
		sql.setString(4, info.hostname);
		sql.setString(5, info.realAddress);
		
		sql.addBatch();
	}
}
