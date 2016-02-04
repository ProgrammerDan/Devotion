package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;

/**
 * Soft wrapper for the abstract underlying class.
 * @author Aleksey Terzi
 *
 */

public class fPlayerToggleFlight extends fPlayerToggle {
	public fPlayerToggleFlight(PlayerToggleFlightEvent event) {
		super(event, FlyweightType.ToggleFlight, event != null && event.isFlying());
	}
}