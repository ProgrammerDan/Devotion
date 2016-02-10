package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerDropItemEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerDropItemInfo;

public class fPlayerDropItem extends fPlayer {
	private PlayerDropItemInfo dropItemInfo;
	
	public fPlayerDropItem(PlayerDropItemEvent event) {
		super(event, FlyweightType.DropItem);
		
		if(event != null) {
			this.dropItemInfo = new PlayerDropItemInfo();
			this.dropItemInfo.trace_id = this.eventInfo.trace_id;
			this.dropItemInfo.dropItem = new ItemStackInfo(event.getItemDrop().getItemStack());
			this.dropItemInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		marshallItemStackToStream(this.dropItemInfo.dropItem, os);
		os.writeBoolean(this.dropItemInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.dropItemInfo = new PlayerDropItemInfo();
		this.dropItemInfo.trace_id = this.eventInfo.trace_id;
		this.dropItemInfo.dropItem = unmarshallItemStackFromStream(is);
		this.dropItemInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerDropItemSource().insert(this.dropItemInfo);
	}
}