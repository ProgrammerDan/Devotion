package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.ItemInfo;

/**
 * @author Aleksey Terzi
 *
 */

public abstract class Source {
	private SqlDatabase db;
	private PreparedStatement sql;
	
	protected Source(SqlDatabase db) {
		this.db = db;
	}
	
	public boolean hasUpdates() {
		return this.sql != null;
	}
	
	public void startBatch() {
		this.sql = null;
	}
	
	public void executeBatch() throws SQLException {
		this.sql.executeBatch();
		this.sql.close();
		this.sql = null;
	}
		
	protected PreparedStatement getSql(String script) throws SQLException {
		if(this.sql == null) {
			this.sql = this.db.prepareStatement(script);
		}
		
		return this.sql;
	}
	
	protected void setItemParams(int startIndex, ItemInfo info) throws SQLException {
		if(info.itemType != null) {
			this.sql.setString(startIndex, info.itemType); 
		} else {
			this.sql.setNull(startIndex, Types.VARCHAR);
		}
		
		if(info.itemAmount != null) {
			this.sql.setInt(startIndex + 1, info.itemAmount);
		} else {
			this.sql.setNull(startIndex + 1, Types.INTEGER);
		}
		
		if(info.itemDurability != null) {
			this.sql.setShort(startIndex + 2, info.itemDurability);
		} else {
			this.sql.setNull(startIndex + 2, Types.SMALLINT);
		}
		
		if(info.itemEnchantments != null) {
			this.sql.setString(startIndex + 3, info.itemEnchantments);
		} else {
			this.sql.setNull(startIndex + 3, Types.VARCHAR);
		}
		
		if(info.itemLore != null) {
			this.sql.setString(startIndex + 4, info.itemLore);
		} else {
			this.sql.setNull(startIndex + 4, Types.VARCHAR);
		}
	}
}
