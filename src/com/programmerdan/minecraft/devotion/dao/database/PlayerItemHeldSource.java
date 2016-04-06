package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerItemHeldInfo;

public class PlayerItemHeldSource extends Source {
	private static final String insertScript = "INSERT dev_player_item_held (trace_id, previous_slot, new_slot, main_hand_type, main_hand_displayname, main_hand_amount, main_hand_durability, main_hand_enchantments, main_hand_lore, off_hand_type, off_hand_displayname, off_hand_amount, off_hand_durability, off_hand_enchantments, off_hand_lore, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerItemHeldSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerItemHeldInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.previousSlot);
		sql.setInt(3, info.newSlot);
		
		int nextIndex = setItemParams(4, info.mainHand);
		nextIndex = setItemParams(nextIndex, info.offHand);
		
		sql.setBoolean(nextIndex, info.eventCancelled);
		
		sql.addBatch();
	}
}
