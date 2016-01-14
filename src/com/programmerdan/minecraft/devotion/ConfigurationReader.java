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

		// Get Database information, wire up DAO

		// Get file information, wire up file

		// Discover and configure Monitors
	}

	private static log(String message) {
		Devotion.logger().info(message);
	}
}
