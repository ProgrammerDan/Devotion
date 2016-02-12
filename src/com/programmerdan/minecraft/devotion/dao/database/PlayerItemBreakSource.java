package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerItemBreakInfo;

public class PlayerItemBreakSource extends Source {
	private static final String insertScript = "INSERT dev_player_item_break (trace_id, broken_item_type, broken_item_amount, broken_item_durability, broken_item_enchantments, broken_item_lore) VALUES (?, ?, ?, ?, ?, ?)";
	
	public PlayerItemBreakSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerItemBreakInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		setItemParams(2, info.brokenItem);
		
		sql.addBatch();
	}
}
