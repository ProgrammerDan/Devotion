package com.programmerdan.minecraft.devotion.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Movement Event handler placeholder.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public class MovementEvents implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerLogin(PlayerLoginEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerJoin(PlayerJoinEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerQuit(PlayerQuitEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerKick(PlayerKickEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerTeleport(PlayerTeleportEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerChangeWorld(PlayerChangedWorldEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorDeathEvent(PlayerDeathEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerRespawn(PlayerRespawnEvent event) {
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void monitorPlayerMovement(PlayerMoveEvent event) {
	}

}

