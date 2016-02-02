package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.event.player.PlayerLoginEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.DevotionEventLoginInfo;

public class fPlayerLogin extends fPlayer {
	private DevotionEventLoginInfo loginInfo;
	
	public fPlayerLogin(PlayerLoginEvent event) {
		super(event, FlyweightType.Login);
		
		if(event != null) {
			this.loginInfo = new DevotionEventLoginInfo();
			this.loginInfo.eventTime = this.eventInfo.eventTime;
			this.loginInfo.playerUUID = this.eventInfo.playerUUID;
			this.loginInfo.address = event.getAddress().toString();
			this.loginInfo.hostname = event.getHostname();
			this.loginInfo.realAddress = event.getRealAddress().toString();
		}
	}
	
	@Override
	protected void marshallToStream(DataOutputStream os) throws IOException {
		super.marshallToStream(os);
		
		os.writeUTF(this.loginInfo.address);
		os.writeUTF(this.loginInfo.hostname);
		os.writeUTF(this.loginInfo.realAddress);
	}
	
	@Override
	protected void unmarshallFromStream(DataInputStream is) throws IOException {
		super.unmarshallFromStream(is);
		
		this.loginInfo = new DevotionEventLoginInfo();
		this.loginInfo.eventTime = this.eventInfo.eventTime;
		this.loginInfo.playerUUID = this.eventInfo.playerUUID;
		this.loginInfo.address = is.readUTF();
		this.loginInfo.hostname = is.readUTF();
		this.loginInfo.realAddress = is.readUTF();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) throws SQLException {
		super.marshallToDatabase(db);
		
		db.getDevotionEventLoginSource().insert(this.loginInfo);
	}
}
