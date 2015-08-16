package com.programmerdan.minecraft.devotion;

import com.programmerdan.minecraft.devotion.commands.CommandHandler;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>Devotion quietly and unobtrusively tracks everything everyone does. Check the README for details.</p>
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0.0
 */
public class Devotion extends JavaPlugin {
	private static final CommandHandler commandHandler;
	private static final Logger logger;
	private static final JavaPlugin plugin;
	private static boolean enabled = true;
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

	public static boolean isEnabled() {
		return Devotion.enabled;
	}

	public static boolean isDebug() {
		return Devotion.debug;
	}

	public static void setEnabled(boolean status) {
		Devotion.enabled = status;
	}

	public static void setDebug(boolean debug) {
		Devotion.debug = debug;
	}

	@Override
	public void onEnable() {
		// setting a couple of static fields so that they are available
		// elsewhere
		logger = getLogger();
		plugin = this;
		commandHandler = new CommandHandler(this);
		getServer().getPluginManager().registerEvents(new ChunkEvents(), this);
	}
}
