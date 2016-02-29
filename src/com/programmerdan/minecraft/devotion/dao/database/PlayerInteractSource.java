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
	private static final String insertScript = "INSERT dev_player_interact (trace_id, item_type, item_displayname, item_amount, item_durability, item_enchantments, item_lore, action_name, clicked_block_type, clicked_block_x, clicked_block_y, clicked_block_z, block_face, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerInteractSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerInteractInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		setItemParams(2, info.item);
		
		sql.setString(8, info.actionName);
		
		int nextIndex = setBlockParams(9, info.clickedBlock);
		
		if(info.blockFace != null) {
			sql.setString(nextIndex, info.blockFace);
		} else {
			sql.setNull(nextIndex, Types.VARCHAR);
		}
		
		sql.setBoolean(nextIndex + 1, info.eventCancelled);
		
		sql.addBatch();
	}
}
