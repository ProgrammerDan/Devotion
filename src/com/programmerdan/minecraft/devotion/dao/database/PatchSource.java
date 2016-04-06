package com.programmerdan.minecraft.devotion.dao.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.programmerdan.minecraft.devotion.dao.info.PatchInfo;

public class PatchSource extends Source {
	private static final String insertScript = "INSERT dev_patch (patch_name, applied_date) VALUES (?, ?)";
	private static final String selectScript = "SELECT * FROM dev_patch WHERE patch_name = ?";
	
	public PatchSource(SqlDatabase db) {
		super(db);
	}
		
	public void insert(PatchInfo info) throws SQLException {
		PreparedStatement sql = getSql(insertScript);

		sql.setString(1, info.patchName);
		sql.setTimestamp(2, info.appliedDate);
		
		sql.addBatch();
	}
	
	public Boolean isExist(String patchName) throws SQLException {
		PreparedStatement select = getDb().prepareStatement(selectScript);
		
		select.setString(1, patchName);
		
		Boolean isExist;
		ResultSet resultSet = select.executeQuery();
		
		try
		{
			isExist = resultSet.next();
		}
		finally
		{
			resultSet.close();
		}
		
		return isExist;
	}
}
