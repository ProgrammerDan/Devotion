package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerResourcePackStatusInfo;

public class PlayerResourcePackStatusSource extends Source {
	private static final String insertScript = "INSERT dev_player_resource_pack_status (trace_id, status) VALUES (?, ?)";
	
	public PlayerResourcePackStatusSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerResourcePackStatusInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);

		if(info.status != null) {
			sql.setString(2, info.status); 
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
				
		sql.addBatch();
	}
}
