package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.DevotionEventInteractInfo;

/**
 * @author Aleksey Terzi
 * @author ProgrammerDan
 *
 */

public class DevotionEventInteractSource extends Source {
	private static final String insertScript = "INSERT devotion_event_interact (event_time, player_uuid, item_type, item_amount, item_durability, item_enchantments, item_lore, action_name, clicked_block_type, block_face, event_cancelled, trace_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public DevotionEventInteractSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DevotionEventInteractInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setTimestamp(1, info.eventTime);
		sql.setString(2, info.playerUUID);
		
		if(info.itemType != null) {
			sql.setString(3, info.itemType); 
		} else {
			sql.setNull(3, Types.VARCHAR);
		}
		
		if(info.itemAmount != null) {
			sql.setInt(4, info.itemAmount);
		} else {
			sql.setNull(4, Types.INTEGER);
		}
		
		if(info.itemDurability != null) {
			sql.setShort(5, info.itemDurability);
		} else {
			sql.setNull(5, Types.SMALLINT);
		}
		
		if(info.itemEnchantments != null) {
			sql.setString(6, info.itemEnchantments);
		} else {
			sql.setNull(6, Types.VARCHAR);
		}
		
		if(info.itemLore != null) {
			sql.setString(7, info.itemLore);
		} else {
			sql.setNull(7, Types.VARCHAR);
		}
		
		sql.setString(8, info.actionName);
		
		if(info.clickedBlockType != null) {
			sql.setString(9, info.clickedBlockType);
		} else {
			sql.setNull(9, Types.VARCHAR);
		}
		
		if(info.blockFace != null) {
			sql.setString(10, info.blockFace);
		} else {
			sql.setNull(10, Types.VARCHAR);
		}
		
		sql.setBoolean(11, info.eventCancelled);
		
		sql.setString(12, info.trace_id);
		
		sql.addBatch();
	}
}
