package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerVelocityEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.DevotionEventVelocityInfo;

public class fPlayerVelocity extends fPlayer {
	private DevotionEventVelocityInfo velocityInfo;
	
	public fPlayerVelocity(PlayerVelocityEvent event) {
		super(event, FlyweightType.Velocity);
		
		if(event != null) {
			this.velocityInfo = new DevotionEventVelocityInfo();
			this.velocityInfo.eventTime = this.eventInfo.eventTime;
			this.velocityInfo.playerUUID = this.eventInfo.playerUUID;
			this.velocityInfo.trace_id = this.eventInfo.trace_id;
			this.velocityInfo.velocityX = event.getVelocity().getX();
			this.velocityInfo.velocityY = event.getVelocity().getY();
			this.velocityInfo.velocityZ = event.getVelocity().getZ();
			this.velocityInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeDouble(this.velocityInfo.velocityX);
		os.writeDouble(this.velocityInfo.velocityY);
		os.writeDouble(this.velocityInfo.velocityZ);
		os.writeBoolean(this.velocityInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.velocityInfo = new DevotionEventVelocityInfo();
		this.velocityInfo.eventTime = this.eventInfo.eventTime;
		this.velocityInfo.playerUUID = this.eventInfo.playerUUID;
		this.velocityInfo.trace_id = this.eventInfo.trace_id;

		this.velocityInfo.velocityX = is.readDouble();
		this.velocityInfo.velocityY = is.readDouble();
		this.velocityInfo.velocityZ = is.readDouble();
		this.velocityInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getDevotionEventVelocitySource().insert(this.velocityInfo);
	}
}
