package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerBedInfo;

/**
 * @author Aleksey Terzi
 * @author ProgrammerDan
 *
 */

public class PlayerBedSource extends Source {
	private static final String insertScript = "INSERT dev_player_bed (trace_id, bed, event_cancelled) VALUES (?, ?, ?)";
	
	public PlayerBedSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerBedInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		if(info.bed != null) {
			sql.setString(2, info.bed); 
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
		
		sql.setBoolean(3, info.eventCancelled);
		
		sql.addBatch();
	}
}