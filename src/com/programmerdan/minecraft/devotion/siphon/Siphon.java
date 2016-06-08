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
	private String targetFolderString;
	private File targetFolder;
	private boolean active;
	private boolean attached;
	private SiphonDatabase database;

	public void deactivate() {
		this.active = false;
	}
	
	public Siphon(String filename) {
		configFile = new File(filename);
		
		if (!configFile.exists()) {
			configFile = null;
			throw new SiphonFailure("The filename provided does not exist.");
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
				throw new SiphonFailure("Unable to load YAML config.");
			}
		} catch (FileNotFoundException fnfe) {
			throw new SiphonFailure(fnfe);
		}
		
		// Slices # of slices to split the day into while extracting
		this.slices = (Integer) this.config.get("slices");
		// # of minutes to delay between extracts
		this.delay = (Integer) this.config.get("delay");
		// Where to put the file.
		this.targetFolderString = (String) this.config.get("targetFolder");
		this.targetFolder = new File(this.targetFolderString);
		if (!this.targetFolder.isDirectory()) {
			throw new SiphonFailure("Target folder provided either isn't a folder or doesn't exist.");
		}

		if (this.slices == null || this.slices < 0) {
			throw new SiphonFailure("'slices' must be present and non-negative");
		}

		if (this.delay == null || this.delay < 0) {
			throw new SiphonFailure("'delay' must be present and non-negative");
		}

		active = true;

		// are we listening for user input?
		this.attached = (Boolean) this.config.get("attached");

		Map<String, Object> database = this.config.get("database");

		String host = (String) database.get("host");
		int port = (Integer) database.get("port");
		String db = (String) database.get("database");
		String user = (String) database.get("user");
		String password = (String) database.get("password");

		this.database = new SiphonDatabase(host, post, db, user, password);

		doMainLoop();
	}



	private void doMainLoop() {
		Scanner console = null;
		String command = null;
		if (attached) {
			console = new Scanner(System.in);
		}

		while (active) {
			if (attached) {
				try {
					command = console.nextLine();
				} catch (NoSuchElementException nsee) {
					System.err.println("Console detached while attached, assuming shutdown.");
					attached = false;
					active = false;
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				System.out.println("Hmm, who woke me?");
			}
		}

		if (this.database != null) {
			this.database.close();
		}
	}
}
