package com.programmerdan.minecraft.devotion.dao.flyweight;

import org.bukkit.event.player.PlayerMoveEvent;

import com.programmerdan.minecraft.devotion.dao.FlyweightType;

/**
 * Soft wrapper for the abstract underlying class.
 * @author ProgrammerDan <programmerdan@gmail.com>
 *
 */
public class fPlayerMove extends fPlayer {

	public fPlayerMove(PlayerMoveEvent playerEvent) {
		super(playerEvent, FlyweightType.Move);
	}
}
