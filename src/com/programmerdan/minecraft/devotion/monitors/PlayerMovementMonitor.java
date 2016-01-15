package com.programmerdan.minecraft.devotion.monitors;

import com.programmerdan.minecraft.devotion.Monitor;
import com.programmerdan.minecraft.devotion.config.PlayerMovementMonitorConfig;

public class PlayerMovementMonitor extends Monitor{

	private PlayerMovementMonitorConfig config;
	
	public PlayerMovementMonitor(PlayerMovementMonitorConfig config) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEnable() {
		if (super.isEnabled()) {
			return;
		}
		
		if (config.useListeners)
		// register listeners
		
		// register 
		// TODO Auto-generated method stub

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

}
