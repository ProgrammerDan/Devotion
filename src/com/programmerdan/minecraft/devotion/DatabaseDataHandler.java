package com.programmerdan.minecraft.devotion;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import com.programmerdan.minecraft.devotion.dao.Flyweight;
import com.programmerdan.minecraft.devotion.dao.database.SqlDatabase;
import com.programmerdan.minecraft.devotion.util.FlowHelper;

/**
 * Database handler for all Flyweight types that properly extend
 * {@link Flyweight}.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @author Aleksey-Terzi
 */
public class DatabaseDataHandler extends DataHandler {
	private FlowHelper statistics = new FlowHelper(); 
	private long lastSampleTime;

	private SqlDatabase db;

	private DatabaseDataHandler() {
	}

	/**
	 * Generates a new DatabaseDataHandler from a config section.
	 * 
	 * @param config
	 *            the config to use
	 * @return a new DatabaseDataHandler, or null if setup failed.
	 */
	public static DatabaseDataHandler generate(ConfigurationSection config) {
		if (config == null) {
			Devotion.logger().log(Level.SEVERE,
							"Null configuration passed; DatabaseDataHandler not created");
			return null;
		}

		DatabaseDataHandler ddh = new DatabaseDataHandler();

		String host = config.getString("host");
		int port = config.getInt("port", 2306);
		String username = config.getString("username");
		String password = config.getString("password");
		String database = config.getString("database");
		// String schema = config.getString("schema");

		ddh.db = new SqlDatabase(host, port, database, username, password,Devotion.logger());

		if (!ddh.db.connect())
			return null;

		ddh.setup(config.getLong("delay", 100l), config.getLong("max_run", 50l), false,
				config.getBoolean("debug"));

		return ddh;
	}

	@Override
	public void teardown() {
		this.db.close();
	}

	@Override
	void buildUp() {
		if (!this.db.initDb()) {
			this.db.close();
			setActive(false);
		}
		this.lastSampleTime = System.currentTimeMillis();
	}

	/**
	 * Simple process, generalized for all serializable Flyweights.
	 * 
	 * - Gets the datastream {@link getStream()} - Gets a Flyweight from the
	 * asynch FIFO queue {@link insertQueue} - Writes the flyweight {@link
	 * Flyweight.serialize(DataOutputStream)} - Continues until: - Nothing left
	 * in the queue - Time exceeded for write operations - Bytes exceed limit
	 * per cycle
	 * 
	 * Using atomic statistics handler. TODO: self-configure based on results.
	 */
	@Override
	void process() {
		debug(Level.INFO, "Starting commit...");
		long in = System.currentTimeMillis();

		int records = 0;

		if (!isQueueEmpty()) {
			try {
				this.db.begin();
	
				while (System.currentTimeMillis() < in + this.getMaxRun() && 
						records < getMaxRun() && !isQueueEmpty()) {
					Flyweight obj = pollFromQueue();
	
					obj.serialize(this.db);
	
					records++;
				}
	
				this.db.commit();
	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			debug(Level.INFO, "Event queue is empty, nothing to commit.");
		}

		long sTime = System.currentTimeMillis();
		statistics.sample(sTime - this.lastSampleTime, super.getAndClearInsertCount(), records);
			
		in = sTime - in;
		this.lastSampleTime = sTime;
		debug(Level.INFO, "Done commit {0} records in {1} milliseconds",
				new Object[]{records, in});
	}
}