package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerDropItemInfo;

public class PlayerDropItemSource extends Source {
	private static final String insertScript = "INSERT dev_player_drop_item (trace_id, drop_item_type, drop_item_displayname, drop_item_amount, drop_item_durability, drop_item_enchantments, drop_item_lore, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerDropItemSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerDropItemInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		setItemParams(2, info.dropItem);
		sql.setBoolean(8, info.eventCancelled);
		
		sql.addBatch();
	}
}
