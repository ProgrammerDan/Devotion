package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PlayerEditBookInfo;

public class PlayerEditBookSource extends Source {
	private static final String insertScript = "INSERT dev_player_edit_book (trace_id, slot, signing, prev_title, new_title, title_changed, author_changed, content_changed, page_count_changed, event_cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public PlayerEditBookSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PlayerEditBookInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.trace_id);
		sql.setInt(2, info.slot);
		sql.setBoolean(3, info.signing);
		sql.setString(4, info.prevTitle);
		sql.setString(5, info.newTitle);
		sql.setBoolean(6, info.titleChanged);
		sql.setBoolean(7, info.authorChanged);
		sql.setBoolean(8, info.contentChanged);
		sql.setBoolean(9, info.pageCountChanged);
		sql.setBoolean(10, info.eventCancelled);
		
		sql.addBatch();
	}
}
