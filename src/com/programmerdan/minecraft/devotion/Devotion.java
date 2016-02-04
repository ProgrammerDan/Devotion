package com.programmerdan.minecraft.devotion;

import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.programmerdan.minecraft.devotion.commands.CommandHandler;
import com.programmerdan.minecraft.devotion.dao.Flyweight;
import com.programmerdan.minecraft.devotion.dao.flyweight.PlayerFactory;
import com.programmerdan.minecraft.devotion.datahandlers.DataHandler;
import com.programmerdan.minecraft.devotion.monitors.Monitor;

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

	/**
	 * @return Gets the singleton instance of this plugin.
	 */
	public static Devotion instance() {
		return Devotion.instance;
	}

	/**
	 * @return Returns if this plugin is in debug mode. 
	 */
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * Sets the debug mode
	 * @param debug new mode.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Registers a new Monitor (which tracks player / game actions).
	 * This does NOT activate a monitor. 
	 * @see #onEnable() for that functionality.
	 *  
	 * @param monitor the Monitor to register
	 */
	public void registerMonitor(Monitor monitor) {
		activeMonitors.add(monitor);
	}

	/**
	 * Gets all the monitors currently registered.
	 * 
	 * @return Vector of all monitors
	 */
	public Vector<Monitor> getMonitors() {
		return activeMonitors;
	}
	
	/**
	 * Registers a new Data Handler (which stores player / game actions).
	 * Be careful how many you register as each new handler will introduce its own
	 * drain on system resources/time.
	 * 
	 * @param handler the DataHandler to register
	 */
	public void registerDataHandler(DataHandler handler) {
		dataHandlers.add(handler);
	}
	
	/**
	 * Gets all the currently registered Handlers.
	 * 
	 * @return Vector of DataHandler objects.
	 */
	public Vector<DataHandler> getHandlers() {
		return dataHandlers;
	}
	
	public void insert(Flyweight data){
		for (DataHandler dh : dataHandlers) {
			dh.insert(data);
		}
	}

	/**
	 * Called by Bukkit to set up this plugin.
	 * In term this plugin sets up its command handler,
	 *  activates its Data Handlers by calling the {@link DataHandler#begin()} function,
	 *  and activates its monitors by callingthe {@link Monitor#onEnable()} function.
	 */
	@Override
	public void onEnable() {
		// setting a couple of static fields so that they are available elsewhere
		instance = this;
		commandHandler = new CommandHandler(this);
		activeMonitors = new Vector<Monitor>();
		dataHandlers = new Vector<DataHandler>();
		
		PlayerFactory.init();

		if (ConfigurationReader.readConfig()) {
			for (DataHandler dh : dataHandlers) {
				dh.begin();
			}
			for (Monitor m : activeMonitors) {
				m.onEnable();
			}
		} else {
			getLogger().severe("Unable to configure Devotion, no monitors active. Fix configuration and reload.");
			this.setEnabled(false);
			return;
		}
	}

	/**
	 * Trying to be a good Bukkit resident. Fully supported teardown; first
	 * turns off registered Monitors by calling {@link Monitor#onDisable()} then
	 * turns off registered DataHandlers by calling {@link DataHandler#teardown()}.
	 * Each registered list is cleared, and the instance is nulled.
	 * Bukkit handles the rest.  
	 */
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
		dataHandlers.clear();
		instance = null;
	}
}
