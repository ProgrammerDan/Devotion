package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.DropItemInfo;

public class DropItemSource extends Source {
	private static final String insertScript = "INSERT dev_drop_item (trace_id, item_type, item_displayname, item_amount, item_durability, item_enchantments, item_lore) VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	public DropItemSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(DropItemInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		
		setItemParams(2, info.item);
		
		sql.addBatch();
	}
}
