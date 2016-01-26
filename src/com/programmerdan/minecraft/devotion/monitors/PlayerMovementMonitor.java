package com.programmerdan.minecraft.devotion.monitors;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.programmerdan.minecraft.devotion.DataHandler;
import com.programmerdan.minecraft.devotion.Devotion;
import com.programmerdan.minecraft.devotion.Monitor;
import com.programmerdan.minecraft.devotion.config.PlayerMovementMonitorConfig;
import com.programmerdan.minecraft.devotion.dao.flyweight.fPlayerMovement;

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
		fPlayerMovement flyweight = new fPlayerMovement(event);
		ArrayList<DataHandler> handlers = Devotion.instance().getHandlers();
		
		for(DataHandler handler : handlers) {
			handler.insert(flyweight);
		}
	}
}
