package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerPickupItemEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerPickupItemInfo;

public class fPlayerPickupItem extends fPlayer {
	private PlayerPickupItemInfo pickupItemInfo;
	
	public fPlayerPickupItem(PlayerPickupItemEvent event) {
		super(event, FlyweightType.PickupItem);
		
		if(event != null) {
			this.pickupItemInfo = new PlayerPickupItemInfo();
			this.pickupItemInfo.trace_id = this.eventInfo.trace_id;
			this.pickupItemInfo.item = event.getItem() != null ? new ItemStackInfo(event.getItem().getItemStack()): new ItemStackInfo(null);
			this.pickupItemInfo.remaining = event.getRemaining();
			this.pickupItemInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		marshallItemStackToStream(this.pickupItemInfo.item, os);
		os.writeInt(this.pickupItemInfo.remaining);
		os.writeBoolean(this.pickupItemInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.pickupItemInfo = new PlayerPickupItemInfo();
		this.pickupItemInfo.trace_id = this.eventInfo.trace_id;
		this.pickupItemInfo.item = unmarshallItemStackFromStream(is);
		this.pickupItemInfo.remaining = is.readInt();
		this.pickupItemInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerPickupItemSource().insert(this.pickupItemInfo);
	}
}
