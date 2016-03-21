package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemStackInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerFishInfo;

public class fPlayerFish extends fPlayer {
	private PlayerFishInfo fishInfo;
	
	public fPlayerFish(PlayerFishEvent event) {
		super(event, FlyweightType.Fish);
		
		if(event != null) {
			this.fishInfo = new PlayerFishInfo();
			this.fishInfo.trace_id = this.eventInfo.trace_id;
			this.fishInfo.caughtEntity = event.getCaught() != null ? event.getCaught().getType().name(): null;
			this.fishInfo.caughtEntityId = event.getCaught() != null ? event.getCaught().getUniqueId().toString(): null;
			this.fishInfo.caughtItem = new ItemStackInfo(event.getCaught() != null && event.getCaught() instanceof Item ? ((Item)event.getCaught()).getItemStack(): null);
			this.fishInfo.exp = event.getExpToDrop();
			this.fishInfo.state = event.getState().name();
			this.fishInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.fishInfo.caughtEntity != null ? this.fishInfo.caughtEntity: "");
		os.writeUTF(this.fishInfo.caughtEntityId != null ? this.fishInfo.caughtEntityId: "");
		marshallItemStackToStream(this.fishInfo.caughtItem, os);
		os.writeInt(this.fishInfo.exp);
		os.writeUTF(this.fishInfo.state != null ? this.fishInfo.state: "");
		os.writeBoolean(this.fishInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.fishInfo = new PlayerFishInfo();
		this.fishInfo.trace_id = this.eventInfo.trace_id;
		
		this.fishInfo.caughtEntity = is.readUTF();
		if(this.fishInfo.caughtEntity == "") this.fishInfo.caughtEntity = null;
		
		this.fishInfo.caughtEntityId = is.readUTF();
		if(this.fishInfo.caughtEntityId == "") this.fishInfo.caughtEntityId = null;

		this.fishInfo.caughtItem = unmarshallItemStackFromStream(is);
		this.fishInfo.exp = is.readInt();
		
		this.fishInfo.state = is.readUTF();
		if(this.fishInfo.state == "") this.fishInfo.state = null;
		
		this.fishInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerFishSource().insert(this.fishInfo);
	}
}
