package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerEggThrowEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerEggThrowInfo;

public class fPlayerEggThrow extends fPlayer {
	private PlayerEggThrowInfo eggThrowInfo;
	
	public fPlayerEggThrow(PlayerEggThrowEvent event) {
		super(event, FlyweightType.EggThrow);
		
		if(event != null) {
			this.eggThrowInfo = new PlayerEggThrowInfo();
			this.eggThrowInfo.trace_id = this.eventInfo.trace_id;
			this.eggThrowInfo.hatching = event.isHatching();
			this.eggThrowInfo.hatchingType = event.getHatchingType().name();
			this.eggThrowInfo.numHatches = event.getNumHatches();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeBoolean(this.eggThrowInfo.hatching);
		os.writeUTF(this.eggThrowInfo.hatchingType);
		os.writeByte(this.eggThrowInfo.numHatches);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.eggThrowInfo = new PlayerEggThrowInfo();
		this.eggThrowInfo.trace_id = this.eventInfo.trace_id;
		this.eggThrowInfo.hatching = is.readBoolean();
		this.eggThrowInfo.hatchingType = is.readUTF();
		this.eggThrowInfo.numHatches = is.readByte();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerEggThrowSource().insert(this.eggThrowInfo);
	}
}
