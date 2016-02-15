package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerShearEntityInfo;

public class PlayerShearEntitySource extends Source {
	private static final String insertScript = "INSERT dev_player_shear_entity (trace_id, entity, entity_uuid, event_cancelled) VALUES (?, ?, ?, ?)";
	
	public PlayerShearEntitySource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerShearEntityInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		if(info.entity != null) {
			sql.setString(2, info.entity);
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
		
		if(info.entityUUID != null) {
			sql.setString(3, info.entityUUID);
		} else {
			sql.setNull(3, Types.VARCHAR);
		}
		
		sql.setBoolean(4, info.eventCancelled);
		
		sql.addBatch();
	}
}
