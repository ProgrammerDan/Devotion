package com.programmerdan.minecraft.devotion.monitors;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.programmerdan.minecraft.devotion.DataHandler;
import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.Monitor;
import com.programmerdan.minecraft.devotion.config.PlayerMovementMonitorConfig;
import com.programmerdan.minecraft.devotion.dao.Flyweight;
import com.programmerdan.minecraft.devotion.dao.flyweight.fPlayerJoin;
import com.programmerdan.minecraft.devotion.dao.flyweight.fPlayerLogin;
import com.programmerdan.minecraft.devotion.dao.flyweight.fPlayerQuit;

public class PlayerMovementMonitor extends Monitor implements Listener {

	private PlayerMovementMonitorConfig config;
	
	public PlayerMovementMonitor(PlayerMovementMonitorConfig config) {
		this.config = config;
	}

	@Override
	public void onEnable() {
		if (super.isEnabled()) {
			return;
		}
		
		Devotion.instance().getServer().getPluginManager().registerEvents(this, Devotion.instance());

		super.setEnabled(true);
	}

	@Override
	public void onDisable() {
		if (!super.isEnabled()) {
			return;
		}
		// TODO Auto-generated method stub

		super.setEnabled(false);
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerLogin(PlayerLoginEvent event) {
		insert(new fPlayerLogin(event));
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerJoin(PlayerJoinEvent event) {
		insert(new fPlayerJoin(event));
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerQuit(PlayerQuitEvent event) {
		insert(new fPlayerQuit(event));
	}
	
	private void insert(Flyweight flyweight) {
		ArrayList<DataHandler> handlers = Devotion.instance().getHandlers();
		
		for(DataHandler handler : handlers) {
			handler.insert(flyweight);
		}
	}
}
