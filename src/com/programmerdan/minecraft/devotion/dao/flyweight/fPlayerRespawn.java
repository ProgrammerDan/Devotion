package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerRespawnEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerRespawnInfo;

/**
 * Soft wrapper for the abstract underlying class.
 * @author Aleksey Terzi
 *
 */

public class fPlayerRespawn extends fPlayer {
	private PlayerRespawnInfo respawnInfo;
	
	public fPlayerRespawn(PlayerRespawnEvent event) {
		super(event, FlyweightType.Respawn);
		
		if(event != null) {
			this.respawnInfo = new PlayerRespawnInfo();
			this.respawnInfo.trace_id = this.eventInfo.trace_id;
			this.respawnInfo.isBedSpawn = event.isBedSpawn();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeBoolean(this.respawnInfo.isBedSpawn);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.respawnInfo = new PlayerRespawnInfo();
		this.respawnInfo.trace_id = this.eventInfo.trace_id;

		this.respawnInfo.isBedSpawn = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerRespawnSource().insert(this.respawnInfo);
	}
}