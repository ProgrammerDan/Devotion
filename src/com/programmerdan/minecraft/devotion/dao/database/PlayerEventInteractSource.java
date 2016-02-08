package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEventInteractInfo;

/**
 * @author Aleksey Terzi
 * @author ProgrammerDan
 *
 */

public class PlayerEventInteractSource extends Source {
	private static final String insertScript = "INSERT dev_player_event_interact (trace_id, item_type, item_amount, item_durability, item_enchantments, item_lore, action_name, clicked_block_type, block_face, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerEventInteractSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEventInteractInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		if(info.itemType != null) {
			sql.setString(2, info.itemType); 
		} else {
			sql.setNull(2, Types.VARCHAR);
		}
		
		if(info.itemAmount != null) {
			sql.setInt(3, info.itemAmount);
		} else {
			sql.setNull(3, Types.INTEGER);
		}
		
		if(info.itemDurability != null) {
			sql.setShort(4, info.itemDurability);
		} else {
			sql.setNull(4, Types.SMALLINT);
		}
		
		if(info.itemEnchantments != null) {
			sql.setString(5, info.itemEnchantments);
		} else {
			sql.setNull(5, Types.VARCHAR);
		}
		
		if(info.itemLore != null) {
			sql.setString(6, info.itemLore);
		} else {
			sql.setNull(6, Types.VARCHAR);
		}
		
		sql.setString(7, info.actionName);
		
		if(info.clickedBlockType != null) {
			sql.setString(8, info.clickedBlockType);
		} else {
			sql.setNull(8, Types.VARCHAR);
		}
		
		if(info.blockFace != null) {
			sql.setString(9, info.blockFace);
		} else {
			sql.setNull(9, Types.VARCHAR);
		}
		
		sql.setBoolean(10, info.eventCancelled);
		
		sql.addBatch();
	}
}
