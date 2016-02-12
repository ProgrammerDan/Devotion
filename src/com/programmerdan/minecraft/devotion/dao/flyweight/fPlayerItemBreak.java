package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerItemBreakEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerItemBreakInfo;

public class fPlayerItemBreak extends fPlayer {
	private PlayerItemBreakInfo itemBreakInfo;
	
	public fPlayerItemBreak(PlayerItemBreakEvent event) {
		super(event, FlyweightType.ItemBreak);
		
		if(event != null) {
			this.itemBreakInfo = new PlayerItemBreakInfo();
			this.itemBreakInfo.trace_id = this.eventInfo.trace_id;
			this.itemBreakInfo.brokenItem = new ItemStackInfo(event.getBrokenItem());
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		// in context of file IO it isn't necessary to write the unique UUID twice b/c the parent
		// and child records are written together.
		marshallItemStackToStream(this.itemBreakInfo.brokenItem, os);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.itemBreakInfo = new PlayerItemBreakInfo();
		this.itemBreakInfo.trace_id = this.eventInfo.trace_id;
		this.itemBreakInfo.brokenItem = unmarshallItemStackFromStream(is);
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerItemBreakSource().insert(this.itemBreakInfo);
	}
}