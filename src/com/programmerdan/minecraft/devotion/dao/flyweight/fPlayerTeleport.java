package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerTeleportEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.DevotionEventTeleportInfo;
import com.programmerdan.minecraft.devotion.dao.info.LocationInfo;

/**
 * Soft wrapper for the abstract underlying class.
 * @author Aleksey Terzi
 *
 */

public class fPlayerTeleport extends fPlayer {
	private DevotionEventTeleportInfo teleportInfo;
	
	public fPlayerTeleport(PlayerTeleportEvent event) {
		super(event, FlyweightType.Teleport);
		
		if(event != null) {
			this.teleportInfo = new DevotionEventTeleportInfo();
			this.teleportInfo.eventTime = this.eventInfo.eventTime;
			this.teleportInfo.playerUUID = this.eventInfo.playerUUID;
			this.teleportInfo.cause = event.getCause().name();
			this.teleportInfo.from = new LocationInfo(event.getFrom());
			this.teleportInfo.to = new LocationInfo(event.getTo());
			this.teleportInfo.cancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.teleportInfo.cause != null ? this.teleportInfo.cause: "");
		marshallLocationToStream(this.teleportInfo.from, os);
		marshallLocationToStream(this.teleportInfo.to, os);
		os.writeBoolean(this.teleportInfo.cancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.teleportInfo = new DevotionEventTeleportInfo();
		this.teleportInfo.eventTime = this.eventInfo.eventTime;
		this.teleportInfo.playerUUID = this.eventInfo.playerUUID;

		this.teleportInfo.cause = is.readUTF();
		if(this.teleportInfo.cause == "") this.teleportInfo.cause = null;
		this.teleportInfo.from = unmarshallLocationFromStream(is);
		this.teleportInfo.to = unmarshallLocationFromStream(is);
		this.teleportInfo.cancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getDevotionEventTeleportSource().insert(this.teleportInfo);
	}
}
