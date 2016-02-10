package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEditBookInfo;

public class PlayerEditBookSource extends Source {
	private static final String insertScript = "INSERT dev_player_edit_book (trace_id, slot, signing, title_changed, author_changed, content_changed, page_count_changed, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerEditBookSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEditBookInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.slot);
		sql.setBoolean(3, info.signing);
		sql.setBoolean(4, info.titleChanged);
		sql.setBoolean(5, info.authorChanged);
		sql.setBoolean(6, info.contentChanged);
		sql.setBoolean(7, info.pageCountChanged);
		sql.setBoolean(8, info.eventCancelled);
		
		sql.addBatch();
	}
}
