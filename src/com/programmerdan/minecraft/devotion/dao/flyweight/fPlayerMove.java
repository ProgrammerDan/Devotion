package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Soft wrapper for the abstract underlying class.
 * @author ProgrammerDan <programmerdan@gmail.com>
 *
 */
public class fPlayerMove extends fPlayer {

	public fPlayerMove(PlayerMoveEvent playerEvent) {
		super(playerEvent, "Move");
	}
	
	public fPlayerMove(Player player) {
		this(new PlayerMoveEvent(player, new Location(null, 0, 0, 0), new Location(null, 0, 0, 0)) {
			@Override
			public HandlerList getHandlers() {
				return null;
			}
		});
	}

}
