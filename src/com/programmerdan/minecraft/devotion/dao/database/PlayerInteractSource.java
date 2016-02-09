package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.PlayerInteractInfo;

/**
 * @author Aleksey Terzi
 * @author ProgrammerDan
 *
 */

public class PlayerInteractSource extends Source {
	private static final String insertScript = "INSERT dev_player_interact (trace_id, item_type, item_amount, item_durability, item_enchantments, item_lore, action_name, clicked_block, block_face, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerInteractSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerInteractInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		setItemParams(2, info.item);
		
		sql.setString(7, info.actionName);
		
		if(info.clickedBlock != null) {
			sql.setString(8, info.clickedBlock);
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
