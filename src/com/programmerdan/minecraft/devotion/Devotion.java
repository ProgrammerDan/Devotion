package com.programmerdan.minecraft.devotion;

import com.programmerdan.minecraft.devotion.commands.CommandHandler;
import com.programmerdan.minecraft.devotion.events.*;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>Devotion quietly and unobtrusively tracks everything everyone does. Check the README for details.</p>
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0.0
 */
public class Devotion extends JavaPlugin {
	private CommandHandler commandHandler;
	private static Devotion instance;
	private boolean debug = false;

	private ArrayList<Monitor> activeMonitors;

	public CommandHandler commandHandler() {
		return this.commandHandler;
	}

	public static Logger logger() {
		return Devotion.instance.getLogger();
	}

	public static Devotion instance() {
		return Devotion.instance;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void registerMonitor(Monitor monitor) {
		activeMonitors.add(monitor);
	}

	public ArrayList<Monitor> getMonitors() {
		return activeMonitors;
	}

	@Override
	public void onEnable() {
		// setting a couple of static fields so that they are available elsewhere
		plugin = this;
		commandHandler = new CommandHandler(this);
		activeMonitors = new ArrayList<Monitor>();

		if (ConfigurationReader.readConfig()) {
			for (Monitor m : activeMonitors) {
				m.onEnable();
			}
		} else {
			getLogger().error("Unable to configure Devotion, no monitors active. Fix configuration and reload.");
		}
		
		//getServer().getPluginManager().registerEvents(new MovementEvents(), this);
	}

	@Override
	public void onDisable() {
		// end monitors
		for (Monitor m : activeMonitors) {
			m.onDisable();
		}
		// close database
		// close files
		plugin = null;
	}
}
