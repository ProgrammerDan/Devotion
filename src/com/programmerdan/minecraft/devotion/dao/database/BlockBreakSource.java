package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.BlockBreakInfo;

public class BlockBreakSource extends Source {
	private static final String insertScript = "INSERT dev_block_place (trace_id, block, exp_to_drop, event_cancelled) VALUES (?, ?, ?, ?)";
	
	public BlockBreakSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(BlockBreakInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		if(info.block != null) {
			sql.setString(2, info.block);
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
		
		sql.setInt(3, info.expToDrop);
		
		sql.setBoolean(4, info.eventCancelled);
		
		sql.addBatch();
	}
}
