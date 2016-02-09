package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerInteractEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.ItemInfo;
import com.programmerdan.minecraft.devotion.dao.info.PlayerInteractInfo;

public class fPlayerInteract extends fPlayer {
	private PlayerInteractInfo interactInfo;
	
	public fPlayerInteract(PlayerInteractEvent event) {
		super(event, FlyweightType.Interact);
		
		if(event != null) {
			this.interactInfo = new PlayerInteractInfo();
			this.interactInfo.trace_id = this.eventInfo.trace_id;
			this.interactInfo.item = new ItemInfo(event.getItem());
			this.interactInfo.actionName = event.getAction().name();
			this.interactInfo.clickedBlock = event.getClickedBlock() != null ? event.getClickedBlock().getType().name(): null;
			this.interactInfo.blockFace = event.getBlockFace() != null ? event.getBlockFace().name(): null;
			this.interactInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		// in context of file IO it isn't necessary to write the unique UUID twice b/c the parent
		// and child records are written together.
		marshallItemToStream(this.interactInfo.item, os);
		os.writeUTF(this.interactInfo.actionName);
		os.writeUTF(this.interactInfo.clickedBlock != null ? this.interactInfo.clickedBlock: "");
		os.writeUTF(this.interactInfo.blockFace != null ? this.interactInfo.blockFace: "");
		os.writeBoolean(this.interactInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.interactInfo = new PlayerInteractInfo();
		this.interactInfo.trace_id = this.eventInfo.trace_id;
		this.interactInfo.item = unmarshallItemFromStream(is);
		this.interactInfo.actionName = is.readUTF();
		
		this.interactInfo.clickedBlock = is.readUTF();
		if(this.interactInfo.clickedBlock == "") this.interactInfo.clickedBlock = null;
		
		this.interactInfo.blockFace = is.readUTF();
		if(this.interactInfo.blockFace == "") this.interactInfo.blockFace = null;
		
		this.interactInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerInteractSource().insert(this.interactInfo);
	}
}
