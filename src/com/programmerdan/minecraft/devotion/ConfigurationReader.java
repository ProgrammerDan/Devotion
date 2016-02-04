package com.programmerdan.minecraft.devotion;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.programmerdan.minecraft.devotion.datahandlers.DataHandler;
import com.programmerdan.minecraft.devotion.datahandlers.DatabaseDataHandler;
import com.programmerdan.minecraft.devotion.datahandlers.FileDataHandler;
import com.programmerdan.minecraft.devotion.monitors.Monitor;
import com.programmerdan.minecraft.devotion.monitors.PlayerInteractionMonitor;
import com.programmerdan.minecraft.devotion.monitors.PlayerMovementMonitor;

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

		ConfigurationSection movement = monitors.getConfigurationSection("movement");
		if (movement != null) {
			Monitor pmm = PlayerMovementMonitor.generate(movement);
			if (pmm != null){
				Devotion.instance().registerMonitor(pmm);
				log("Player Movement Monitor is registered");
			}
		}
		
		ConfigurationSection interaction = monitors.getConfigurationSection("interaction");
		if (interaction != null) {
			Monitor pim = PlayerInteractionMonitor.generate(interaction);
			if (pim != null){
				Devotion.instance().registerMonitor(pim);
				log("Player Interaction Monitor is registered");
			}
		}
	
		// TODO: next monitor is inventory
		
		
		ConfigurationSection dao = conf.getConfigurationSection("dao");

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
		}
	
		return true;
	}

	private static void log(String message) {
		Devotion.logger().info(message);
	}
}
