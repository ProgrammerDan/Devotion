package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerToggleSprintEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;

public class fPlayerToggleSprint extends fPlayerToggle {
	public fPlayerToggleSprint(PlayerToggleSprintEvent event) {
		super(event, FlyweightType.ToggleSprint, event != null && event.isSprinting());
	}
}
