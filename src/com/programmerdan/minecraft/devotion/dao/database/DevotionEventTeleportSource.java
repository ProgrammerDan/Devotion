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
	private static final String insertScript = "INSERT devotion_event_teleport (event_time, player_uuid, cause, from_worlduuid, from_x, from_y, from_z, from_yaw, from_pitch, to_worlduuid, to_x, to_y, to_z, to_yaw, to_pitch, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
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
		sql.setString(4, info.from.worldUUID);
		sql.setDouble(5, info.from.x);
		sql.setDouble(6, info.from.y);
        sql.setDouble(7, info.from.z);
		sql.setFloat(8, info.from.yaw);
		sql.setFloat(9, info.from.pitch);
		sql.setString(10, info.to.worldUUID);
		sql.setDouble(11, info.to.x);
		sql.setDouble(12, info.to.y);
        sql.setDouble(13, info.to.z);
		sql.setFloat(14, info.to.yaw);
		sql.setFloat(15, info.to.pitch);
		sql.setBoolean(16, info.cancelled);
		
		sql.addBatch();
	}
}