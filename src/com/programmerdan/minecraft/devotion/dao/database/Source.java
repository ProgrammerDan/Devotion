package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
