package com.programmerdan.minecraft.devotion;

import com.programmerdan.minecraft.devotion.commands.CommandHandler;
import com.programmerdan.minecraft.devotion.events.*;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>Devotion quietly and unobtrusively tracks everything everyone does. Check the README for details.</p>
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0.0
 */
public class Devotion extends JavaPlugin {
	private static CommandHandler commandHandler;
	private static Logger logger;
	private static JavaPlugin plugin;
	private static boolean active = true;
	private static boolean debug = false;

	public static CommandHandler commandHandler() {
		return Devotion.commandHandler;
	}

	public static Logger logger() {
		return Devotion.logger;
	}

	public static JavaPlugin instance() {
		return Devotion.plugin;
	}

	public static boolean isActive() {
		return Devotion.active;
	}

	public static boolean isDebug() {
		return Devotion.debug;
	}

	public static void setActive(boolean status) {
		Devotion.active = status;
	}

	public static void setDebug(boolean debug) {
		Devotion.debug = debug;
	}

	@Override
	public void onEnable() {
		// setting a couple of static fields so that they are available elsewhere
		logger = getLogger();
		plugin = this;
		commandHandler = new CommandHandler(this);
		getServer().getPluginManager().registerEvents(new MovementEvents(), this);
	}
}
