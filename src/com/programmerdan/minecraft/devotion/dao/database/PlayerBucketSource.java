package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerBucketInfo;

public class PlayerBucketSource extends Source {
	private static final String insertScript = "INSERT dev_player_bucket (trace_id, item_type, item_amount, item_durability, item_enchantments, item_lore, clicked_block, block_face, bucket, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerBucketSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerBucketInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		setItemParams(2, info.item);
				
		if(info.clickedBlock != null) {
			sql.setString(7, info.clickedBlock);
		} else {
			sql.setNull(7, Types.VARCHAR);
		}
		
		if(info.blockFace != null) {
			sql.setString(8, info.blockFace);
		} else {
			sql.setNull(8, Types.VARCHAR);
		}
		
		if(info.bucket != null) {
			sql.setString(9, info.bucket);
		} else {
			sql.setNull(9, Types.VARCHAR);
		}
		
		sql.setBoolean(10, info.eventCancelled);
		
		sql.addBatch();
	}
}
