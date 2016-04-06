package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.BlockBreakInfo;

public class BlockBreakSource extends Source {
	private static final String insertScript = "INSERT dev_block_break (trace_id, block_type, block_x, block_y, block_z, exp_to_drop, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	public BlockBreakSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(BlockBreakInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		int nextIndex = this.setBlockParams(2, info.block);
		
		sql.setInt(nextIndex, info.expToDrop);
		sql.setBoolean(nextIndex + 1, info.eventCancelled);
		
		sql.addBatch();
	}
}
