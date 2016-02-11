package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerExpChangeEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerExpChangeInfo;

public class fPlayerExpChange extends fPlayer {
	private PlayerExpChangeInfo expChangeInfo;
	
	public fPlayerExpChange(PlayerExpChangeEvent event) {
		super(event, FlyweightType.ExpChange);
		
		if(event != null) {
			this.expChangeInfo = new PlayerExpChangeInfo();
			this.expChangeInfo.trace_id = this.eventInfo.trace_id;
			this.expChangeInfo.amount = event.getAmount();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeInt(this.expChangeInfo.amount);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.expChangeInfo = new PlayerExpChangeInfo();
		this.expChangeInfo.trace_id = this.eventInfo.trace_id;
		this.expChangeInfo.amount = is.readInt();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerExpChangeSource().insert(this.expChangeInfo);
	}
}