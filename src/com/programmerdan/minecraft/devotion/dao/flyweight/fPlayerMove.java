package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Soft wrapper for the abstract underlying class.
 * @author ProgrammerDan <programmerdan@gmail.com>
 *
 */
public class fPlayerMove extends fPlayerMovement {

	public fPlayerMove(PlayerEvent playerEvent) {
		super(playerEvent, "Move");
	}
	
	public fPlayerMove(Player player) {
		this(new PlayerEvent(player) {
			@Override
			public HandlerList getHandlers() {
				return null;
			}
		});
	}

}
