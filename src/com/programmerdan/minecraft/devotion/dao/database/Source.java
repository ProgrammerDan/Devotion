package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.programmerdan.minecraft.devotion.dao.info.BlockInfo;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;

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
	
	protected SqlDatabase getDb() {
		return this.db;
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
	
	protected int setItemParams(int startIndex, ItemStackInfo info) throws SQLException {
		if(info.itemType != null) {
			this.sql.setString(startIndex, info.itemType); 
		} else {
			this.sql.setNull(startIndex, Types.VARCHAR);
		}
		
		if (info.itemDisplayName != null) {
			this.sql.setString(startIndex + 1, info.itemDisplayName);
		} else {
			this.sql.setNull(startIndex + 1, Types.VARCHAR);
		}
		
		if(info.itemAmount != null) {
			this.sql.setInt(startIndex + 2, info.itemAmount);
		} else {
			this.sql.setNull(startIndex + 2, Types.INTEGER);
		}
		
		if(info.itemDurability != null) {
			this.sql.setShort(startIndex + 3, info.itemDurability);
		} else {
			this.sql.setNull(startIndex + 3, Types.SMALLINT);
		}
		
		if(info.itemEnchantments != null) {
			this.sql.setString(startIndex + 4, info.itemEnchantments);
		} else {
			this.sql.setNull(startIndex + 4, Types.VARCHAR);
		}
		
		if(info.itemLore != null) {
			this.sql.setString(startIndex + 5, info.itemLore);
		} else {
			this.sql.setNull(startIndex + 5, Types.VARCHAR);
		}
		
		return startIndex + 6;
	}
	
	protected int setBlockParams(int startIndex, BlockInfo info) throws SQLException {
		if(info.blockType != null) {
			this.sql.setString(startIndex, info.blockType); 
		} else {
			this.sql.setNull(startIndex, Types.VARCHAR);
		}
		
		if(info.x != null) {
			this.sql.setInt(startIndex + 1, info.x);
		} else {
			this.sql.setNull(startIndex + 1, Types.INTEGER);
		}

		if(info.y != null) {
			this.sql.setInt(startIndex + 2, info.y);
		} else {
			this.sql.setNull(startIndex + 2, Types.INTEGER);
		}
		
		if(info.z != null) {
			this.sql.setInt(startIndex + 3, info.z);
		} else {
			this.sql.setNull(startIndex + 3, Types.INTEGER);
		}
		
		return startIndex + 4;
	}
}
