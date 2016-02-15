package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerResourcePackStatusInfo;

public class fPlayerResourcePackStatus extends fPlayer {
	private PlayerResourcePackStatusInfo resourcePackStatusInfo;
	
	public fPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
		super(event, FlyweightType.ResourcePackStatus);
		
		if (event != null) {
			this.resourcePackStatusInfo = new PlayerResourcePackStatusInfo();
			this.resourcePackStatusInfo.trace_id = this.eventInfo.trace_id;
			this.resourcePackStatusInfo.status = event.getStatus() != null ? event.getStatus().name(): null;
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.resourcePackStatusInfo.status != null ? this.resourcePackStatusInfo.status: "");
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.resourcePackStatusInfo = new PlayerResourcePackStatusInfo();
		this.resourcePackStatusInfo.trace_id = this.eventInfo.trace_id;

		this.resourcePackStatusInfo.status = is.readUTF();
		if(this.resourcePackStatusInfo.status == "") this.resourcePackStatusInfo.status = null;
		
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		db.getPlayerResourcePackStatusSource().insert(this.resourcePackStatusInfo);
	}
}
