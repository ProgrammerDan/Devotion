package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerItemConsumeInfo;

public class PlayerItemConsumeSource extends Source {
	private static final String insertScript = "INSERT dev_player_item_consume (trace_id, item_type, item_amount, item_durability, item_enchantments, item_lore, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerItemConsumeSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerItemConsumeInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		setItemParams(2, info.item);
		sql.setBoolean(7, info.eventCancelled);
		
		sql.addBatch();
	}
}
