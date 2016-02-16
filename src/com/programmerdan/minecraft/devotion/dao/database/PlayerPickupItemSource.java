package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerPickupItemInfo;

public class PlayerPickupItemSource extends Source {
	private static final String insertScript = "INSERT dev_player_pickup_item (trace_id, item_type, item_displayname, item_amount, item_durability, item_enchantments, item_lore, remaining, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerPickupItemSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerPickupItemInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		setItemParams(2, info.item);
		sql.setInt(8, info.remaining);
		sql.setBoolean(9, info.eventCancelled);
		
		sql.addBatch();
	}
}
