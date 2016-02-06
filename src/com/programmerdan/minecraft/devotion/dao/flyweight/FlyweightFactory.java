package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.util.ArrayList;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.programmerdan.minecraft.devotion.dao.DAOException;
import com.programmerdan.minecraft.devotion.dao.FlyweightType;

/**
 * 
 * @author Aleksey Terzi
 */
public class FlyweightFactory {
	private static final ArrayList<EventDefinition> Definitions = new ArrayList<EventDefinition>();
	
	public static void init() {
		Definitions.add(new EventDefinition(FlyweightType.Login.getId(), PlayerLoginEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Join.getId(), PlayerJoinEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Quit.getId(), PlayerQuitEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Move.getId(), PlayerMoveEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Interact.getId(), PlayerInteractEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Kick.getId(), PlayerKickEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Teleport.getId(), PlayerTeleportEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.ChangedWorld.getId(), PlayerChangedWorldEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Respawn.getId(), PlayerRespawnEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.ToggleFlight.getId(), PlayerToggleFlightEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.ToggleSneak.getId(), PlayerToggleSneakEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.ToggleSprint.getId(), PlayerToggleSprintEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Velocity.getId(), PlayerVelocityEvent.class));
	}
	
	public static fPlayer create(PlayerEvent event) {
		byte id = getId(event);
		return create(id, event);
	}
	
	public static fPlayer create(byte id) {
		return create(id, null);
	}

	private static fPlayer create(byte id, PlayerEvent event) {
		if(id == FlyweightType.Login.getId()) return new fPlayerLogin((PlayerLoginEvent)event);
		if(id == FlyweightType.Join.getId()) return new fPlayerJoin((PlayerJoinEvent)event);
		if(id == FlyweightType.Quit.getId()) return new fPlayerQuit((PlayerQuitEvent)event);
		if(id == FlyweightType.Move.getId()) return new fPlayerMove((PlayerMoveEvent)event);
		if(id == FlyweightType.Interact.getId()) return new fPlayerInteract((PlayerInteractEvent)event);
		if(id == FlyweightType.Kick.getId()) return new fPlayerKick((PlayerKickEvent)event);
		if(id == FlyweightType.Teleport.getId()) return new fPlayerTeleport((PlayerTeleportEvent)event);
		if(id == FlyweightType.ChangedWorld.getId()) return new fPlayerChangedWorld((PlayerChangedWorldEvent)event);
		if(id == FlyweightType.Respawn.getId()) return new fPlayerRespawn((PlayerRespawnEvent)event);
		if(id == FlyweightType.ToggleFlight.getId()) return new fPlayerToggleFlight((PlayerToggleFlightEvent)event);
		if(id == FlyweightType.ToggleSneak.getId()) return new fPlayerToggleSneak((PlayerToggleSneakEvent)event);
		if(id == FlyweightType.ToggleSprint.getId()) return new fPlayerToggleSprint((PlayerToggleSprintEvent)event);
		if(id == FlyweightType.Velocity.getId()) return new fPlayerVelocity((PlayerVelocityEvent)event);
		
		throw new DAOException("Event with ID = " + id + " is not registered.");
	}
	
	private static byte getId(PlayerEvent event) {
		Class<?> cls = event.getClass();
		
		for(EventDefinition def : Definitions) {
			if(def.cls == cls) return def.id;
		}
		
		throw new DAOException("Event " + cls.toString() + " is not registered.");
	}
}
