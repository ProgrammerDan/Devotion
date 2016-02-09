package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerQuitEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.PlayerQuitInfo;

public class fPlayerQuit extends fPlayer {
	private PlayerQuitInfo quitInfo;
	public fPlayerQuit(PlayerQuitEvent event) {
		super(event, FlyweightType.Quit);
		if (event != null) {
			this.quitInfo = new PlayerQuitInfo();
			this.quitInfo.trace_id = this.eventInfo.trace_id;
			this.quitInfo.quitMessage = event.getQuitMessage();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.quitInfo.quitMessage != null ? this.quitInfo.quitMessage: "");
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.quitInfo = new PlayerQuitInfo();
		this.quitInfo.trace_id = this.eventInfo.trace_id;

		this.quitInfo.quitMessage = is.readUTF();
		if(this.quitInfo.quitMessage == "") this.quitInfo.quitMessage = null;
		
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		db.getPlayerQuitSource().insert(this.quitInfo);
	}
}