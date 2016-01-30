package com.programmerdan.minecraft.devotion;

import com.programmerdan.minecraft.devotion.commands.CommandHandler;

import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>Devotion quietly and un-obtrusively tracks everything everyone does. Check the README for details.</p>
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0.0
 */
public class Devotion extends JavaPlugin {
	private CommandHandler commandHandler;
	private static Devotion instance;
	private boolean debug = false;

	private Vector<Monitor> activeMonitors;
	
	private Vector<DataHandler> dataHandlers;
	
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

	public Vector<Monitor> getMonitors() {
		return activeMonitors;
	}
	
	public void registerDataHandler(DataHandler handler) {
		dataHandlers.add(handler);
	}
	
	public Vector<DataHandler> getHandlers() {
		return dataHandlers;
	}

	@Override
	public void onEnable() {
		// setting a couple of static fields so that they are available elsewhere
		instance = this;
		commandHandler = new CommandHandler(this);
		activeMonitors = new Vector<Monitor>();
		dataHandlers = new Vector<DataHandler>();

		if (ConfigurationReader.readConfig()) {
			for (Monitor m : activeMonitors) {
				m.onEnable();
			}
			
			for (DataHandler dh : dataHandlers) {
				dh.begin();
			}
		} else {
			getLogger().severe("Unable to configure Devotion, no monitors active. Fix configuration and reload.");
			this.setEnabled(false);
			return;
		}
		
		
		
		//getServer().getPluginManager().registerEvents(new MovementEvents(), this);
	}

	@Override
	public void onDisable() {
		// end monitors
		for (Monitor m : activeMonitors) {
			m.onDisable();
		}
		for (DataHandler dh : dataHandlers) {
			dh.teardown();
		}
		activeMonitors.clear();
		// close database
		// close files
		instance = null;
	}
}
