package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerBucketInfo;

public class PlayerBucketSource extends Source {
	private static final String insertScript = "INSERT dev_player_bucket (trace_id, item_type, item_displayname, item_amount, item_durability, item_enchantments, item_lore, clicked_block_type, clicked_block_x, clicked_block_y, clicked_block_z, block_face, bucket, is_fill, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerBucketSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerBucketInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		setItemParams(2, info.item);
		
		int nextIndex = setBlockParams(8, info.clickedBlock);
		
		if(info.blockFace != null) {
			sql.setString(nextIndex, info.blockFace);
		} else {
			sql.setNull(nextIndex, Types.VARCHAR);
		}
		
		if(info.bucket != null) {
			sql.setString(nextIndex + 1, info.bucket);
		} else {
			sql.setNull(nextIndex + 1, Types.VARCHAR);
		}
		
		sql.setBoolean(nextIndex + 2, info.isFill);
		sql.setBoolean(nextIndex + 3, info.eventCancelled);
		
		sql.addBatch();
	}
}
