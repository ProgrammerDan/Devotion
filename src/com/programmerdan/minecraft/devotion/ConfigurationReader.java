package com.programmerdan.minecraft.devotion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.programmerdan.minecraft.devotion.datahandlers.DataHandler;
import com.programmerdan.minecraft.devotion.datahandlers.DatabaseDataHandler;
import com.programmerdan.minecraft.devotion.datahandlers.FileDataHandler;
import com.programmerdan.minecraft.devotion.monitors.Monitor;

/**
 * Handles loading of configurations, including establishment of the various monitors,
 * handlers, and whatever else comprises Devotion.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public class ConfigurationReader {
	public static boolean readConfig() {
		log("Loading configuration");

		Devotion.instance().saveDefaultConfig();
		Devotion.instance().reloadConfig();
		FileConfiguration conf = Devotion.instance().getConfig();

		Devotion.instance().setDebug(conf.getBoolean("debug", false));

		if (Devotion.instance().isDebug()) {
			log("Debug mode active");
		}
		
		// Discover and configure Monitors
		ConfigurationSection monitors = conf.getConfigurationSection("monitors");

		for (String key : monitors.getKeys(false)) {
			ConfigurationSection monitor = monitors.getConfigurationSection(key);
			Monitor mon = null;
			String clz = monitor.getString("class");
			if (clz == null) { // "internal" monitors get added to the list here. 
				if (monitor.getName().equalsIgnoreCase("movement")) {
					clz = "com.programmerdan.minecraft.devotion.monitors.PlayerMovementMonitor";
				} else if (monitor.getName().equalsIgnoreCase("interaction")) {
					clz = "com.programmerdan.minecraft.devotion.monitors.PlayerInteractionMonitor";
				}
				// TODO: next monitor is inventory
			}
			if (clz != null) {
				log("Trying to add Monitor " + key + " using class " + clz);
				try {
					Class<?> mont = Class.forName(clz);
					if (Monitor.class.isAssignableFrom(mont)) {
						Method montMethod = mont.getMethod("generate", ConfigurationSection.class);
						mon = (Monitor) montMethod.invoke(null, monitor);
					} else {
						log("Found class " + clz + " but is not a valid monitor. Skipping.");
					}
					
				} catch (ClassNotFoundException cnfe) {
					log("Could not find monitor " + clz + ", skipping.");
				} catch (NoSuchMethodException msme) {
					log("Monitor defined as " + clz + " is not well formed, lacks generate function.");
				} catch (SecurityException se) {
					log("Monitor defined as "+ clz + " has reflection-inhibiting security.");
				} catch (IllegalAccessException iae) {
					log("Monitor defined as " + clz + " has access-inhibiting security.");
				} catch (IllegalArgumentException iae2) {
					log("Monitor defined as " + clz + " does not accept the correct parameters.");
				} catch (InvocationTargetException ite) {
					Devotion.logger().log(Level.WARNING,"While provisioning Monitor from " + clz + ", it threw an exception.", ite);
				} catch (NullPointerException npe) {
					Devotion.logger().log(Level.WARNING,"While provisioning Monitor from " + clz + ", it threw an NPE.", npe);
				}
			}
			if (mon == null) { 
				log("Monitor " + key + " defined but could not be matched to a known monitor.");
			} else {
				Devotion.instance().registerMonitor(mon);
			}
		}
		
		// Discover and configure DAO
		
		ConfigurationSection dao = conf.getConfigurationSection("dao");

		for (String key : dao.getKeys(false)) {
			ConfigurationSection handler = dao.getConfigurationSection(key);
			DataHandler han = null;
			String clz = handler.getString("class");
			if (clz == null) { // "internal" datahandlers can get added to the list here. 
				if (handler.getName().equalsIgnoreCase("database")) {
					clz = "com.programmerdan.minecraft.devotion.datahandlers.DatabaseDataHandler";
				} else if (handler.getName().equalsIgnoreCase("file")) {
					clz = "com.programmerdan.minecraft.devotion.datahandlers.FileDataHandler";
				}
				// TODO: next monitor is inventory
			}
			if (clz != null) {
				log("Trying to add DataHandler " + key + " using class " + clz);
				try {
					Class<?> hand = Class.forName(clz);
					if (DataHandler.class.isAssignableFrom(hand)) {
						Method handMethod = hand.getMethod("generate", ConfigurationSection.class);
						han = (DataHandler) handMethod.invoke(null, handler);
					} else {
						log("Found class " + clz + " but is not a valid DataHandler. Skipping.");
					}
					
				} catch (ClassNotFoundException cnfe) {
					log("Could not find data handler " + clz + ", skipping.");
				} catch (NoSuchMethodException msme) {
					log("DataHandler defined as " + clz + " is not well formed, lacks generate function.");
				} catch (SecurityException se) {
					log("DataHandler defined as "+ clz + " has reflection-inhibiting security.");
				} catch (IllegalAccessException iae) {
					log("DataHandler defined as " + clz + " has access-inhibiting security.");
				} catch (IllegalArgumentException iae2) {
					log("DataHandler defined as " + clz + " does not accept the correct parameters.");
				} catch (InvocationTargetException ite) {
					Devotion.logger().log(Level.WARNING,"While provisioning DataHandler from " + clz + ", it threw an exception.", ite);
				} catch (NullPointerException npe) {
					Devotion.logger().log(Level.WARNING,"While provisioning DataHandler from " + clz + ", it threw an NPE.", npe);
				}
			}
			if (han == null) { 
				log("DataHandler " + key + " defined but could not be matched to a known DataHandler.");
			} else {
				Devotion.instance().registerDataHandler(han);
			}
		}
		
		/*
		// Get Database information, wire up DAO
		ConfigurationSection database = dao.getConfigurationSection("database");
		
		if (database != null) {
			DataHandler dataHandler = DatabaseDataHandler.generate(database);
			
			if(dataHandler != null) {
				Devotion.instance().registerDataHandler(dataHandler);
				log("DatabaseDataHandler is registered.");
			}
		}

		// Get file information, wire up file
		ConfigurationSection file = dao.getConfigurationSection("file");
		
		if (file != null) {
			DataHandler dataHandler = FileDataHandler.generate(file);
			
			if(dataHandler != null) {
				Devotion.instance().registerDataHandler(dataHandler);
				log("FileDataHandler is registered.");
			}
		}*/
	
		return true;
	}

	private static void log(String message) {
		Devotion.logger().info(message);
	}
}
