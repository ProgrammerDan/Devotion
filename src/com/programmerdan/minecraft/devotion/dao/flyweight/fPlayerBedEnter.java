package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerBedEnterEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerBedInfo;

public class fPlayerBedEnter extends fPlayer {
	private PlayerBedInfo bedInfo;
	
	public fPlayerBedEnter(PlayerBedEnterEvent event) {
		super(event, FlyweightType.BedEnter);
		
		if(event != null) {
			this.bedInfo = new PlayerBedInfo();
			this.bedInfo.trace_id = this.eventInfo.trace_id;
			
			this.bedInfo.bed = event.getBed() != null ? event.getBed().getType().name(): null;
			this.bedInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.bedInfo.bed != null ? this.bedInfo.bed: "");
		os.writeBoolean(this.bedInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.bedInfo = new PlayerBedInfo();
		this.bedInfo.trace_id = this.eventInfo.trace_id;

		this.bedInfo.bed = is.readUTF();
		if(this.bedInfo.bed == "") this.bedInfo.bed = null;
		
		this.bedInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerBedSource().insert(this.bedInfo);
	}
}
