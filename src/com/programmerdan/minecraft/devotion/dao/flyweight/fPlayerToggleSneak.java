package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;

public class fPlayerToggleSneak extends fPlayerToggle {
	public fPlayerToggleSneak(PlayerToggleSneakEvent event) {
		super(event, FlyweightType.ToggleSneak, event != null && event.isSneaking());
	}
}
