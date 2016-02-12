package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerInteractEntityInfo;

public class fPlayerInteractEntity extends fPlayer {
	private PlayerInteractEntityInfo interactEntityInfo;
	
	public fPlayerInteractEntity(PlayerInteractEntityEvent event) {
		super(event, FlyweightType.InteractEntity);
		
		if(event != null) {
			this.interactEntityInfo = new PlayerInteractEntityInfo();
			this.interactEntityInfo.trace_id = this.eventInfo.trace_id;
			this.interactEntityInfo.clickedEntity = event.getRightClicked() != null ? event.getRightClicked().getType().name(): null;
			this.interactEntityInfo.clickedEntityUUID = event.getRightClicked() != null ? event.getRightClicked().getUniqueId().toString(): null;
			this.interactEntityInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.interactEntityInfo.clickedEntity != null ? this.interactEntityInfo.clickedEntity: "");
		os.writeUTF(this.interactEntityInfo.clickedEntityUUID != null ? this.interactEntityInfo.clickedEntityUUID: "");
		os.writeBoolean(this.interactEntityInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.interactEntityInfo = new PlayerInteractEntityInfo();
		this.interactEntityInfo.trace_id = this.eventInfo.trace_id;
		
		this.interactEntityInfo.clickedEntity = is.readUTF();
		if(this.interactEntityInfo.clickedEntity == "") this.interactEntityInfo.clickedEntity = null;
		
		this.interactEntityInfo.clickedEntityUUID = is.readUTF();
		if(this.interactEntityInfo.clickedEntityUUID == "") this.interactEntityInfo.clickedEntityUUID = null;
		
		this.interactEntityInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerInteractEntitySource().insert(this.interactEntityInfo);
	}
}
