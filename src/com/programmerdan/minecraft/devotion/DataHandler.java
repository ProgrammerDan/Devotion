package com.programmerdan.minecraft.devotion;

import com.programmerdan.minecraft.devotion.dao.Flyweight;

import net.minecraft.server.MinecraftServer;


/**
 * Abstract interface for data handling
 * 
 * @author ProgrammerDan
 */
public abstract class DataHandler extends BukkitRunnable {
	private long delay = -1;
	private boolean adaptive;
	protected boolean debug = false;

	protected void setup(long delay, boolean adaptive) {
		this.delay = delay;
		this.adaptive = adaptive;
	}


	protected void debug(Level logLevel, String message) {
		if (debug) {
			Devoted.logger().log(logLevel, message);
		}
	}
	
	protected void debug(Level logLevel, String message, Object... fill) {
		if (debug) {
			Devoted.logger().log(logLevel, message, fill);
		}
	}

	protected void debug(Level logLevel, String message, Throwable thrown) {
		if (debug) {
			Devoted.logger().log(logLevel, message, thrown);
		}
	}

	public boolean begin() {
		if (delay < 0) {
			Devotion.logger().warning("Cannot begin before calling setup()");
			return false;
		}

		if (adaptive) {
			this.runTaskLaterAsynchronously(Devotion, convertToTicks(this.delay) ); // convert milliseconds into ticks
		} else {
			this.runTaskTimerAsynchronously(Devotion, this.delay, this.delay);
		}
		active = true;

		return true;
	}

	/**
	 * Helper that uses the minecraft internal tps handler to convert an expected delay in milliseconds into 
	 * an actual delay in ticks.
	 */
	private long convertToTicks(long milliseconds) {
		double curtick = 20.0;
		try {
			curtick = MinecraftServer.getServer().recentTps[0];
		} catch (NullPointerException npe) {}
		return milliseconds / (1000.0 / curtick);
	}

	public abstract void insert(Flyweight data);
	public abstract void teardown();
	abstract void process();

	public void run() {
		if (!active) {
			this.cancel();
			return;
		}
		
		process();

		if (adaptive && active) {
			this.runTaskLaterAsynchronously(Devotion, convertToTicks(this.delay) ); // convert milliseconds into ticks
		} else if (!active) {
			this.cancel();
		}
	}

}
