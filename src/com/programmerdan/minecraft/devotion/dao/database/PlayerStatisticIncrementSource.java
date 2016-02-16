package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerStatisticIncrementInfo;

public class PlayerStatisticIncrementSource extends Source {
	private static final String insertScript = "INSERT dev_player_statistic_increment (trace_id, statistic, prev_value, new_value, entity_type, material, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerStatisticIncrementSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerStatisticIncrementInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		if(info.statistic != null) {
			sql.setString(2, info.statistic); 
		} else {
			sql.setNull(2, Types.VARCHAR);
		}

		sql.setInt(3, info.prevValue);
		sql.setInt(4, info.newValue);
		
		if(info.entityType != null) {
			sql.setString(5, info.entityType); 
		} else {
			sql.setNull(5, Types.VARCHAR);
		}

		if(info.material != null) {
			sql.setString(6, info.material); 
		} else {
			sql.setNull(6, Types.VARCHAR);
		}

		sql.setBoolean(7, info.eventCancelled);
		
		sql.addBatch();
	}
}
