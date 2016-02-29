package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.BlockPlaceInfo;

public class BlockPlaceSource extends Source {
	private static final String insertScript = "INSERT dev_block_place (trace_id, can_build, item_in_hand_type, item_in_hand_displayname, item_in_hand_amount, item_in_hand_durability, item_in_hand_enchantments, item_in_hand_lore, block_against_type, block_against_x, block_against_y, block_against_z, block_placed_type, block_placed_x, block_placed_y, block_placed_z, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public BlockPlaceSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(BlockPlaceInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setBoolean(2, info.canBuild);
		
		setItemParams(3, info.itemInHand);
		
		int nextIndex = setBlockParams(9, info.blockAgainst);
		
		nextIndex = setBlockParams(nextIndex, info.blockPlaced);
		
		sql.setBoolean(nextIndex, info.eventCancelled);
		
		sql.addBatch();
	}
}
