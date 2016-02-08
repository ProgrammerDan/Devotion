package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.DevotionEventToggleInfo;

/**
 * Soft wrapper for the abstract underlying class.
 * @author Aleksey Terzi
 *
 */

public abstract class fPlayerToggle extends fPlayer {
	private DevotionEventToggleInfo toggleFlightInfo;
	
	protected fPlayerToggle(PlayerEvent event, FlyweightType flyweightType, Boolean toggleValue) {
		super(event, flyweightType);
		
		if(event != null) {
			this.toggleFlightInfo = new DevotionEventToggleInfo();
			this.toggleFlightInfo.eventTime = this.eventInfo.eventTime;
			this.toggleFlightInfo.playerUUID = this.eventInfo.playerUUID;
			this.toggleFlightInfo.trace_id = this.eventInfo.trace_id;
			this.toggleFlightInfo.toggleValue = toggleValue;
			this.toggleFlightInfo.eventCancelled = ((Cancellable)event).isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeBoolean(this.toggleFlightInfo.toggleValue);
		os.writeBoolean(this.toggleFlightInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.toggleFlightInfo = new DevotionEventToggleInfo();
		this.toggleFlightInfo.eventTime = this.eventInfo.eventTime;
		this.toggleFlightInfo.playerUUID = this.eventInfo.playerUUID;
		this.toggleFlightInfo.trace_id = this.eventInfo.trace_id;

		this.toggleFlightInfo.toggleValue = is.readBoolean();
		this.toggleFlightInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getDevotionEventToggleSource().insert(this.toggleFlightInfo);
	}
}
