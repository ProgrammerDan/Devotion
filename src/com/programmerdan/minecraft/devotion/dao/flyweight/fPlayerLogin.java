package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.event.player.PlayerLoginEvent;

import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.dao.info.DevotionEventLoginInfo;

public class fPlayerLogin extends fPlayer {
	private DevotionEventLoginInfo loginInfo;
	
	public fPlayerLogin(PlayerLoginEvent event) {
		super(event, "Login");
		
		this.loginInfo = new DevotionEventLoginInfo();
		this.loginInfo.eventTime = this.eventInfo.eventTime;
		this.loginInfo.playerUUID = this.eventInfo.playerUUID;
		this.loginInfo.address = event.getAddress().toString();
		this.loginInfo.hostname = event.getHostname();
		this.loginInfo.realAddress = event.getRealAddress().toString();
	}
	
	@Override
	protected void marshallToDatabase(SqlDatabase db) {
		try {
			db.getDevotionEventSource().insert(this.eventInfo);
			db.getDevotionEventLoginSource().insert(this.loginInfo);
		} catch (SQLException e) {
			Devotion.logger().log(Level.SEVERE, "Failed to Serialize an event", e);
		}
	}
}
