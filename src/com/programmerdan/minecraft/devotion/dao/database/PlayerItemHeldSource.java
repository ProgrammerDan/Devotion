package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerItemHeldInfo;

public class PlayerItemHeldSource extends Source {
	private static final String insertScript = "INSERT dev_player_item_held (trace_id, previous_slot, new_slot, new_item_type, new_item_displayname, new_item_amount, new_item_durability, new_item_enchantments, new_item_lore, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerItemHeldSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerItemHeldInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.previousSlot);
		sql.setInt(3, info.newSlot);
		setItemParams(4, info.newItem);
		sql.setBoolean(10, info.eventCancelled);
		
		sql.addBatch();
	}
}
