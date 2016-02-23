package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.BlockPlaceInfo;

public class BlockPlaceSource extends Source {
	private static final String insertScript = "INSERT dev_block_place (trace_id, canBuild, item_in_hand_type, item_in_hand_displayname, item_in_hand_amount, item_in_hand_durability, item_in_hand_enchantments, item_in_hand_lore, block_against, block_placed, block_replaced, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public BlockPlaceSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(BlockPlaceInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setBoolean(2, info.canBuild);
		
		setItemParams(3, info.itemInHand);
		
		if(info.blockAgainst != null) {
			sql.setString(9, info.blockAgainst);
		} else {
			sql.setNull(9, Types.VARCHAR);
		}
		
		if(info.blockPlaced != null) {
			sql.setString(10, info.blockPlaced);
		} else {
			sql.setNull(10, Types.VARCHAR);
		}

		if(info.blockReplaced != null) {
			sql.setString(11, info.blockReplaced);
		} else {
			sql.setNull(11, Types.VARCHAR);
		}
		
		sql.setBoolean(11, info.eventCancelled);
		
		sql.addBatch();
	}
}
