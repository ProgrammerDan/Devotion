package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerJoinEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;

public class fPlayerJoin extends fPlayer {
	public fPlayerJoin(PlayerJoinEvent event) {
		super(event, FlyweightType.Join);
	}
}
