package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerItemConsumeInfo;

public class fPlayerItemConsume extends fPlayer {
	private PlayerItemConsumeInfo itemConsumeInfo;
	
	public fPlayerItemConsume(PlayerItemConsumeEvent event) {
		super(event, FlyweightType.ItemConsume);
		
		if(event != null) {
			this.itemConsumeInfo = new PlayerItemConsumeInfo();
			this.itemConsumeInfo.trace_id = this.eventInfo.trace_id;
			this.itemConsumeInfo.item = new ItemStackInfo(event.getItem());
			this.itemConsumeInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		marshallItemStackToStream(this.itemConsumeInfo.item, os);
		os.writeBoolean(this.itemConsumeInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.itemConsumeInfo = new PlayerItemConsumeInfo();
		this.itemConsumeInfo.trace_id = this.eventInfo.trace_id;
		this.itemConsumeInfo.item = unmarshallItemStackFromStream(is);
		this.itemConsumeInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerItemConsumeSource().insert(this.itemConsumeInfo);
	}
}
