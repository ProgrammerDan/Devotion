package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerLevelChangeEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerLevelChangeInfo;

public class fPlayerLevelChange extends fPlayer {
	private PlayerLevelChangeInfo levelChangeInfo;
	
	public fPlayerLevelChange(PlayerLevelChangeEvent event) {
		super(event, FlyweightType.LevelChange);
		
		if(event != null) {
			this.levelChangeInfo = new PlayerLevelChangeInfo();
			this.levelChangeInfo.trace_id = this.eventInfo.trace_id;
			this.levelChangeInfo.oldLevel = event.getOldLevel();
			this.levelChangeInfo.newLevel = event.getNewLevel();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeInt(this.levelChangeInfo.oldLevel);
		os.writeInt(this.levelChangeInfo.newLevel);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.levelChangeInfo = new PlayerLevelChangeInfo();
		this.levelChangeInfo.trace_id = this.eventInfo.trace_id;
		this.levelChangeInfo.oldLevel = is.readInt();
		this.levelChangeInfo.newLevel = is.readInt();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerLevelChangeSource().insert(this.levelChangeInfo);
	}
}