package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;

public class fPlayerChangedWorld extends fPlayer {
	public fPlayerChangedWorld(PlayerChangedWorldEvent event) {
		super(event, FlyweightType.ChangedWorld);
	}
}
