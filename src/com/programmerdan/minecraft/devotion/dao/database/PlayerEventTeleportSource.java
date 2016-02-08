package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEventTeleportInfo;

/**
 * @author Aleksey Terzi
 *
 */

public class PlayerEventTeleportSource extends Source {
	private static final String insertScript = "INSERT dev_player_event_teleport (trace_id, cause, from_worlduuid, from_x, from_y, from_z, from_yaw, from_pitch, to_worlduuid, to_x, to_y, to_z, to_yaw, to_pitch, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerEventTeleportSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEventTeleportInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		if(info.cause != null) {
			sql.setString(2, info.cause); 
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
		sql.setString(3, info.from.worldUUID);
		sql.setDouble(4, info.from.x);
		sql.setDouble(5, info.from.y);
        sql.setDouble(6, info.from.z);
		sql.setFloat(7, info.from.yaw);
		sql.setFloat(8, info.from.pitch);
		sql.setString(9, info.to.worldUUID);
		sql.setDouble(10, info.to.x);
		sql.setDouble(11, info.to.y);
        sql.setDouble(12, info.to.z);
		sql.setFloat(13, info.to.yaw);
		sql.setFloat(14, info.to.pitch);
		sql.setBoolean(15, info.eventCancelled);
		
		sql.addBatch();
	}
}