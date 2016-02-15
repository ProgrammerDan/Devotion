package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerShearEntityEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerShearEntityInfo;

public class fPlayerShearEntity extends fPlayer {
	private PlayerShearEntityInfo shearEntityInfo;
	
	public fPlayerShearEntity(PlayerShearEntityEvent event) {
		super(event, FlyweightType.ShearEntity);
		
		if(event != null) {
			this.shearEntityInfo = new PlayerShearEntityInfo();
			this.shearEntityInfo.trace_id = this.eventInfo.trace_id;
			this.shearEntityInfo.entity = event.getEntity() != null ? event.getEntity().getType().name(): null;
			this.shearEntityInfo.entityUUID = event.getEntity() != null ? event.getEntity().getUniqueId().toString(): null;
			this.shearEntityInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.shearEntityInfo.entity != null ? this.shearEntityInfo.entity: "");
		os.writeUTF(this.shearEntityInfo.entityUUID != null ? this.shearEntityInfo.entityUUID: "");
		os.writeBoolean(this.shearEntityInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.shearEntityInfo = new PlayerShearEntityInfo();
		this.shearEntityInfo.trace_id = this.eventInfo.trace_id;
		
		this.shearEntityInfo.entity = is.readUTF();
		if(this.shearEntityInfo.entity == "") this.shearEntityInfo.entity = null;
		
		this.shearEntityInfo.entityUUID = is.readUTF();
		if(this.shearEntityInfo.entityUUID == "") this.shearEntityInfo.entityUUID = null;
		
		this.shearEntityInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerShearEntitySource().insert(this.shearEntityInfo);
	}
}
