package com.programmerdan.minecraft.devotion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import com.programmerdan.minecraft.devotion.dao.Flyweight;

/**
 * Database handler for all Flyweight types that properly extend {@link Flyweight}.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public class DatabaseDataHandler extends DataHandler {

	private DriverType driver;
	private String host;
	private int port;
	private String username;
	private String password;
	private String database;
	private String schema;

	private long delay;

	private long maxRun;

	private ConcurrentHashMap<Class<?>, ConcurrentLinkedQueue<Flyweight>> insertQueues;

	private DatabaseDataHandler() {
	}

	/**
	 * Not sure I like this approach but for a given class get its statement.
	 * 
	 * @return a valid PreparedStatement suitable for insertion or null.
	 */
	private PreparedStatement getStatement(Class clazz) {
		return null; // TODO
	}
	
	/**
	 * All done inserting to the batch? commit.
	 */
	private void commitStatement(PrepareStatement) {
		// TODO
	}
	
	/**
	 * Synchronous method called by listeners, puts data on the queue and returns quickly.
	 */
	@Override
	public void insert(Flyweight data) {
		ConcurrentLinkedQueue<Flyweight> insertQueue = insertQueues.get(data.class);
		if (insertQueue == null) {
			insertQueue = new ConcurrentLinkedQueue<Flyweight>();
			insertQueues.put(data.class, insertQueue);
		}
		
		insertQueue.add(data);
	}

	/**
	 * Generates a new DatabaseDataHandler from a config section.
	 * 
	 * @param config the config to use
	 * @return a new DatabaseDataHandler, or null if setup failed.
	 */
	public static DatabaseDataHandler generate(ConfigurationSection config) {
		if (config == null) {
			Devotion.logger().log(Level.SEVERE,
					"Null configuration passed; DatabaseDataHandler not created");
			return null;
		}

		DatabaseDataHandler fdh = new DatabaseDataHandler();

		fdh.maxRun = config.getLong("max_run", 50l);

		if (fdh.maxFileSize <= 0 || fdh.maxIORate <= 0 || fdh.ioChunkSize <= 0) {
			Devotion.logger().log(Level.SEVERE,
					"Improper settings for FileDataHandler");
			return null;
		}

		try {
			if (!fdh.baseFolder.isDirectory() && !fdh.baseFolder.mkdirs()) {
				Devotion.logger().log(
						Level.SEVERE,
						"FileDataHandler base folder can't be created: "
								+ fdh.baseFolder.getPath());
				return null;
			}
		} catch (SecurityException se) {
			Devotion.logger().log(Level.SEVERE,
					"Failed to set up FileDataHandler", se);
			return null;
		}

		fdh.setup(config.getLong("delay", 100l), false, config.getBoolean("debug"));
		
		if (fdh.isActive()) {
			return fdh;
		} else {
			Devotion.logger().log(Level.SEVERE,
					"Failed to satisfy DataHandler interface");
			return null;
		}
	}

	@Override
	public void teardown() {
		this.releaseStream();
	}

	@Override
	void buildUp() {
		this.getStream();		
	}

	/**
	 * Simple process, generalized for all serializable Flyweights.
	 * 
	 *  - Gets the datastream {@link getStream()}
	 *  - Gets a Flyweight from the asynch FIFO queue {@link insertQueue}
	 *  - Writes the flyweight {@link Flyweight.serialize(DataOutputStream)}
	 *  - Continues until:
	 *    - Nothing left in the queue
	 *    - Time exceeded for write operations
	 *    - Bytes exceed limit per cycle
	 *  
	 *  TODO: Use a semaphore to track incoming vs. written and ensure that
	 *    output rate >> input rate. Else warn/fail.
	 */
	@Override
	void process() {
		debug(Level.INFO, "Starting commit...");
		long in = System.currentTimeMillis();
		long records = 0l;
		
		int writeSoFar = 0;

		DataOutputStream bos = getStream();
		
		if (bos != null) {

			while (System.currentTimeMillis() < in + this.maxRun
					&& !this.insertQueue.isEmpty() && writeSoFar <= this.maxIORate) {
				Flyweight toWrite = this.insertQueue.poll();
	
				toWrite.serialize(bos);
				
				writeSoFar += toWrite.getLastWriteSize();
				
				records++;
			}

			debug(Level.INFO, "Done commit {0} records ({1} bytes) in {2} milliseconds",
					new Object[]{records, writeSoFar, (in - System.currentTimeMillis())});
		} else {
			debug(Level.SEVERE, "Data stream is null, cannot commit. Skipping for now.");
		}
	}
}
