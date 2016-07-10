package com.programmerdan.minecraft.devotion.siphon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

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
	private Integer fuzz;
	private Integer buffer;
	private String targetFolderString;
	//private File targetFolder;
	private String tmpFolderString;
	//private File tmpFolder;
	private String databaseTmpFolderString;
	//private File databaseTmpFolder;
	private String targetOwner;
	private boolean active;
	private boolean attached;
	private SiphonDatabase database;
	private int concurrency;

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
		// Max amount of concurrency
		this.concurrency = (Integer) this.config.get("concurrency");
		// Where to put the file.
		this.targetFolderString = (String) this.config.get("targetFolder");
		/*this.targetFolder = new File(this.targetFolderString);
		if (!this.targetFolder.isDirectory()) {
			throw new SiphonFailure("Target folder provided either isn't a folder or doesn't exist.");
		}*/
		// Where to stage the file.
		this.tmpFolderString = (String) this.config.get("tmpFolder");
		/*this.tmpFolder = new File(this.tmpFolderString);
		if (!this.tmpFolder.isDirectory()) {
			throw new SiphonFailure("Temporary folder provided either isn't a folder or doesn't exist.");
		}*/
		
		this.targetOwner = (String) this.config.get("targetOwner");
		System.out.println("Owner of backups set to " + targetOwner);

		if (this.slices == null || this.slices < 0) {
			throw new SiphonFailure("'slices' must be present and non-negative");
		}

		if (this.delay == null || this.delay < 0) {
			throw new SiphonFailure("'delay' must be present and non-negative");
		}
		
		this.fuzz = (Integer) this.config.get("fuzz");
		
		this.buffer = (Integer) this.config.get("buffer");

		if (this.fuzz == null || this.fuzz < 0) {
			throw new SiphonFailure("'fuzz' must be present and non-negative");
		}

		if (this.buffer == null || this.buffer < 0) {
			throw new SiphonFailure("'buffer' must be present and non-negative");
		}
		
		active = true;

		// are we listening for user input?
		this.attached = (Boolean) this.config.get("attached");

		@SuppressWarnings("unchecked")
		Map<String, Object> database = (Map<String, Object>) this.config.get("database");

		String host = (String) database.get("host");
		int port = (Integer) database.get("port");
		String db = (String) database.get("database");
		String user = (String) database.get("user");
		String password = (String) database.get("password");
		this.databaseTmpFolderString = (String) database.get("tmpFolder");
		/*this.databaseTmpFolder = new File(this.databaseTmpFolderString);
		if (!this.databaseTmpFolder.isDirectory()) {
			throw new SiphonFailure("Temporary folder for databse provided either isn't a folder or doesn't exist.");
		}*/
		
		this.database = new SiphonDatabase(host, port, db, user, password);

		doMainLoop();
	}

	private static final String STOP = "stop";
	private static final String PAUSE = "pause";
	private static final String START = "start";

	private void doMainLoop() {
		Scanner console = null;
		String command = null;
		if (attached) {
			console = new Scanner(System.in);
		}
		
		boolean runWorker = true;
		long currentDelay = -1l;
		SiphonWorker currentWorker = null;
		Future<Boolean> workerFuture = null;
		ExecutorService doSiphon = Executors.newSingleThreadExecutor();

		while (this.active) {
			if (runWorker && currentWorker == null) {
				System.out.println("Kicking off a new Siphon!");
				currentWorker = new SiphonWorker(this, this.database, this.slices, this.fuzz, this.buffer);
				workerFuture = doSiphon.submit(currentWorker);
			}
			if (this.attached) {
				try {
					command = console.nextLine();
				} catch (NoSuchElementException nsee) {
					System.err.println("Console detached while attached, assuming shutdown.");
					this.attached = false;
					this.active = false;
					break;
				}
				switch(command) {
				case STOP:
					runWorker = false;
					this.active = false;
					System.out.println("Current siphon will complete and application will exit.");
					break;
				case PAUSE:
					runWorker = false;
					System.out.println("Current siphon will complete but no new workers will run until you issue " + START);
					break;
				case START:
					runWorker = true;
					System.out.println("New siphons will continue as planned.");
					break;
				}
			}
			try {
				Thread.sleep(50l);
			} catch (InterruptedException ie) {
				System.out.println("Hmm, who woke me?");
			} finally {
				if (currentDelay > -1) {
					currentDelay += 50l;
				}
			}
			if (currentWorker != null && currentDelay < 0l) {
				if (workerFuture.isDone()){
					try {
						System.out.println("Current Siphon process complete with flag " + workerFuture.get() + ". Beginning delay time.");
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					currentDelay = 0l;
				}
			}
			if ((currentDelay / 60000) > this.delay) {
				System.out.println("Delay complete. A new Siphon will begin unless paused.");
				currentDelay = -1l;
				currentWorker = null;
				workerFuture = null;
			}
		}
	}

	public int getConcurrency() {
		return this.concurrency;
	}
	
	public String getTargetFolder() {
		return this.targetFolderString;
	}
	
	public String getTmpFolder() {
		return this.tmpFolderString;
	}

	public String getDatabaseTmpFolder() {
		return this.databaseTmpFolderString;
	}

	
	public String getTargetOwner() {
		return this.targetOwner;
	}
}
