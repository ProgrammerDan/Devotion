package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerQuitEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;

public class fPlayerQuit extends fPlayer {
	public fPlayerQuit(PlayerQuitEvent event) {
		super(event, FlyweightType.Quit);
	}
}