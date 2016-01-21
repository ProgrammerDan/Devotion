package com.programmerdan.minecraft.devotion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.programmerdan.minecraft.devotion.config.PlayerMovementMonitorConfig;
import com.programmerdan.minecraft.devotion.monitors.PlayerMovementMonitor;
import com.programmerdan.minecraft.devotion.monitors.SamplingMethod;

public class ConfigurationReader {
	public static boolean readConfig() {
		log("Loading configuration");

		Devotion.instance().saveDefaultConfig();
		Devotion.instance().reloadConfig();
		FileConfiguration conf = Devotion.instance().getConfig();

		Devotion.instance().setDebug(conf.getBoolean("debug", false));

		boolean localDebug = false;
		if (Devotion.instance().isDebug()) {
			log("Debug mode active");
			localDebug = true;
		}
		
		// Discover and configure Monitors
		ConfigurationSection monitors = conf.getConfigurationSection("monitors");

		ConfigurationSection monitor = monitors.getConfigurationSection("movement");
		if (monitor != null) {
			PlayerMovementMonitorConfig pmmc = new PlayerMovementMonitorConfig();
			pmmc.technique = SamplingMethod.valueOf(monitors.getString("sampling", "onevent"));
			pmmc.timeoutBetweenSampling = monitors.getLong("sampling_period", 1000l);
			pmmc.sampleSize = monitors.getInt("sampling_size", 50);
			PlayerMovementMonitor pmm = new PlayerMovementMonitor(pmmc);
			Devotion.instance().registerMonitor(pmm);
		}
		
	
		ConfigurationSection dao = conf.getConfigurationSection("dao");

		// Get Database information, wire up DAO
		ConfigurationSection database = dao.getConfigurationSection("database");
		
		if (database != null) {
			Devotion.instance().registerDataHandler(DatabaseDataHandler.generate(database));
		}

		// Get file information, wire up file
		ConfigurationSection file = dao.getConfigurationSection("file");
		
		if (file != null) {
			Devotion.instance().registerDataHandler(FileDataHandler.generate(file));
		}
	
		return true;
	}

	private static void log(String message) {
		Devotion.logger().info(message);
	}
}
