package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerQuitEvent;

public class fPlayerQuit extends fPlayerMovement {
	public fPlayerQuit(PlayerQuitEvent event) {
		super(event, "Quit");
	}
}