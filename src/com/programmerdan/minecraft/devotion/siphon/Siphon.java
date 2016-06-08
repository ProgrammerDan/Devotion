package com.programmerdan.minecraft.devotion.siphon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Utility helper class that can be run separately from the Spigot thread, that slowly
 * siphons off records from Devotion and saves them to files for backing up.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 *
 */
public class Siphon {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Unable to initialize Siphon without a configuration file.");
			System.out.println("Please provide a .yml configuration file as a parameter.");
			System.exit(1);
		}
		
		(new Siphon(args[0])).begin();
	}
	
	private File configFile;
	private Map<String, Object> config;
	private Integer delay;
	private Integer slices;
	private String targetFolder;
	
	public Siphon(String filename) {
		configFile = new File(filename);
		
		if (!configFile.exists()) {
			configFile = null;
			System.err.println("The filename provided does not exist.");
			System.exit(2);
		}
	}

	/**
	 * Starts the siphon process, first reading in the config file and aborting if there are issues with it.
	 */
	public void begin() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(configFile));
			
			Yaml yaml = new Yaml(new SafeConstructor());
			
			@SuppressWarnings("unchecked")
			Map<String, Object> config = (Map<String, Object>) yaml.load(reader);
			if (config != null) {
				this.config = config;
			} else {
				System.err.println("Unable to load YAML config.");
				System.exit(4);
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.exit(3);
		}
		
		// Slices # of slices to split the day into while extracting
		this.slices = (Integer) this.config.get("slices");
		// # of minutes to delay between extracts
		this.delay = (Integer) this.config.get("delay");
		// Where to put the file.
		this.targetFolder = (String) this.config.get("targetFolder");
	}
}
