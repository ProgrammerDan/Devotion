package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerGameModeChangeEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerGameModeChangeInfo;

public class fPlayerGameModeChange extends fPlayer {
	private PlayerGameModeChangeInfo gameModeChangeInfo;
	
	public fPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		super(event, FlyweightType.GameModeChange);
		
		if(event != null) {
			this.gameModeChangeInfo = new PlayerGameModeChangeInfo();
			this.gameModeChangeInfo.trace_id = this.eventInfo.trace_id;
			this.gameModeChangeInfo.newGameMode = event.getNewGameMode().name();
			this.gameModeChangeInfo.eventCancelled = event.isCancelled();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.gameModeChangeInfo.newGameMode);
		os.writeBoolean(this.gameModeChangeInfo.eventCancelled);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.gameModeChangeInfo = new PlayerGameModeChangeInfo();
		this.gameModeChangeInfo.trace_id = this.eventInfo.trace_id;
		this.gameModeChangeInfo.newGameMode = is.readUTF();
		this.gameModeChangeInfo.eventCancelled = is.readBoolean();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getPlayerGameModeChangeSource().insert(this.gameModeChangeInfo);
	}
}