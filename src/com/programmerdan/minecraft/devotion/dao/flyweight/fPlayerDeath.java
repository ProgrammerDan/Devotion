package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.entity.PlayerDeathEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerDeathInfo;

public class fPlayerDeath extends fPlayer {
	private PlayerDeathInfo deathInfo;
	
	public fPlayerDeath(PlayerDeathEvent event) {
		super(event.getEntity(), FlyweightType.PlayerDeath);
		
		if(event != null) {
			this.deathInfo = new PlayerDeathInfo();
			this.deathInfo.trace_id = this.eventInfo.trace_id;
			this.deathInfo.newExp = event.getNewExp();
			this.deathInfo.newLevel = event.getNewLevel();
			this.deathInfo.newTotalExp = event.getNewTotalExp();
			this.deathInfo.keepLevel = event.getKeepLevel();
			this.deathInfo.keepInventory = event.getKeepInventory();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeInt(this.deathInfo.newExp);
		os.writeInt(this.deathInfo.newLevel);
		os.writeInt(this.deathInfo.newTotalExp);
		os.writeBoolean(this.deathInfo.keepLevel);
		os.writeBoolean(this.deathInfo.keepInventory);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.deathInfo = new PlayerDeathInfo();
		this.deathInfo.trace_id = this.eventInfo.trace_id;
		this.deathInfo.newExp = is.readInt();
		this.deathInfo.newLevel = is.readInt();
		this.deathInfo.newTotalExp = is.readInt();
		this.deathInfo.keepLevel = is.readBoolean();
		this.deathInfo.keepInventory = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerDeathSource().insert(this.deathInfo);
	}
}
