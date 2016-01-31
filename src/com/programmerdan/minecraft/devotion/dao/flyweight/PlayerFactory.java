package com.programmerdan.minecraft.devotion.dao.flyweight;

import java.util.ArrayList;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.programmerdan.minecraft.devotion.dao.DAOException;
import com.programmerdan.minecraft.devotion.dao.FlyweightType;

public class PlayerFactory {
	private static final ArrayList<EventDefinition> Definitions = new ArrayList<EventDefinition>();
	
	public static void init() {
		Definitions.add(new EventDefinition(FlyweightType.Login.getId(), PlayerLoginEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Join.getId(), PlayerJoinEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Quit.getId(), PlayerQuitEvent.class));
		Definitions.add(new EventDefinition(FlyweightType.Move.getId(), PlayerMoveEvent.class));
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
