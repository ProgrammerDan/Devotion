package com.programmerdan.minecraft.devotion;

import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

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
	protected boolean active = false;
	
	public final boolean isActive() {
		return active;
	}

	protected void setup(long delay, boolean adaptive, boolean debug) {
		this.delay = delay;
		this.adaptive = adaptive;
		this.debug = debug;
	}


	protected void debug(Level logLevel, String message) {
		if (debug) {
			Devotion.logger().log(logLevel, message);
		}
	}
	
	protected void debug(Level logLevel, String message, Object... fill) {
		if (debug) {
			Devotion.logger().log(logLevel, message, fill);
		}
	}

	protected void debug(Level logLevel, String message, Throwable thrown) {
		if (debug) {
			Devotion.logger().log(logLevel, message, thrown);
		}
	}

	/**
	 * Called by Devotion to start Data Handling.
	 * @return true if able to begin, false otherwise.
	 */
	public final boolean begin() {
		if (delay < 0) {
			Devotion.logger().warning("Cannot begin before calling setup()");
			return false;
		}
		
		buildUp();

		if (adaptive) {
			this.runTaskLaterAsynchronously(Devotion.instance(), convertToTicks(this.delay) ); // convert milliseconds into ticks
		} else {
			this.runTaskTimerAsynchronously(Devotion.instance(), this.delay, this.delay);
		}
		active = true;

		return true;
	}

	/**
	 * Helper that uses the minecraft internal tps handler to convert an expected delay in milliseconds into 
	 * an actual delay in ticks.
	 */
	protected final long convertToTicks(long milliseconds) {
		double curtick = 20.0;
		try {
			curtick = MinecraftServer.getServer().recentTps[0];
		} catch (NullPointerException npe) {}
		return (long) ( (double) milliseconds / (1000.0 / curtick));
	}

	/**
	 * Called by Devotion plugin to end processing.
	 */
	public final void end() {
		if (active) {
			active = false;
			teardown();
		}
	}
	
	/**
	 * Subclasses must implement this method; It is used to actually accrue data from the
	 * various event tracking methods.
	 * 
	 * @param data
	 */
	public abstract void insert(Flyweight data);
	
	/**
	 * Called by end() of the handler class; used by the Devotion plugin to end processing
	 */
	abstract void teardown();
	
	/**
	 * Called by begin() of the handler class; used by the Devotion plugin to begin processing
	 */
	abstract void buildUp();
	
	/**
	 * Subclasses must implement this method; they should use it to describe the actual
	 * functioning that a handler performs on a per-execution basis.
	 */
	abstract void process();

	/**
	 * Called by the scheduler
	 */
	public final void run() {
		if (!active) {
			this.cancel();
			return;
		}
		
		process();

		if (adaptive && active) {
			this.runTaskLaterAsynchronously(Devotion.instance(), convertToTicks(this.delay) ); // convert milliseconds into ticks
		} else if (!active) {
			this.cancel();
		}
	}

}
