package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerFishInfo;

public class PlayerFishSource extends Source {
	private static final String insertScript = "INSERT dev_player_fish (trace_id, caught_entity, caught_item_type ,caught_item_displayname ,caught_item_amount ,caught_item_durability ,caught_item_enchantments ,caught_item_lore, exp, state, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerFishSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerFishInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setString(2, info.caughtEntity);
		setItemParams(3, info.caughtItem);
		sql.setInt(9, info.exp);
		sql.setString(10, info.state); 
		sql.setBoolean(11, info.eventCancelled);
		
		sql.addBatch();
	}
}
