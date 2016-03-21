package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerItemHeldEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerItemHeldInfo;

public class fPlayerItemHeld extends fPlayer {
	private PlayerItemHeldInfo itemHeldInfo;
	
	public fPlayerItemHeld(PlayerItemHeldEvent event) {
		super(event, FlyweightType.ItemHeld);
		
		if(event != null) {
			this.itemHeldInfo = new PlayerItemHeldInfo();
			this.itemHeldInfo.trace_id = this.eventInfo.trace_id;
			this.itemHeldInfo.previousSlot = event.getPreviousSlot();
			this.itemHeldInfo.newSlot = event.getNewSlot();
			this.itemHeldInfo.newItem = new ItemStackInfo(event.getPlayer().getInventory().getItemInMainHand());
			this.itemHeldInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeInt(this.itemHeldInfo.previousSlot);
		os.writeInt(this.itemHeldInfo.newSlot);
		marshallItemStackToStream(this.itemHeldInfo.newItem, os);
		os.writeBoolean(this.itemHeldInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.itemHeldInfo = new PlayerItemHeldInfo();
		this.itemHeldInfo.trace_id = this.eventInfo.trace_id;
		this.itemHeldInfo.previousSlot = is.readInt();
		this.itemHeldInfo.newSlot = is.readInt();
		this.itemHeldInfo.newItem = unmarshallItemStackFromStream(is);
		this.itemHeldInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerItemHeldSource().insert(this.itemHeldInfo);
	}
}