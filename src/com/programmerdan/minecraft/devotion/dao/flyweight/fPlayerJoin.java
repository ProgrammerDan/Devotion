package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerJoinEvent;

public class fPlayerJoin extends fPlayer {
	public fPlayerJoin(PlayerJoinEvent event) {
		super(event, "Join");
	}
}
