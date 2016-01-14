package com.programmerdan.minecraft.devotion;

import java.util.logging.Level;

/**
 * Base class for Monitor instances, each which will hold self-contained monitors of specific types of things
 * but leverage the larger DAO / persistence fire-and-forget backplane.
 * 
 * @author ProgrammerDan
 */
public abstract class Monitor {

	/**
	 * Convenience method for instances, easy access to plugin
	 */
	protected Devotion devotion() {
		return Devotion.instance();
	}

	/**
	 * Convenience method for instances, easy informational logging
	 */
	protected void log(String msg) {
		Devotion.logger().info(msg);
	}

	/**
	 * Convenience method for instances, easy warning logging
	 */
	protected void warn(String msg) {
		Devotion.logger().warning(msg);
	}

	/**
	 * Convenience method for instances, easy warning logging w/ error report
	 */
	protected void warn(String msg, Throwable error) {
		Devotion.logger().log(Level.WARNING, msg, error);
	}

	/**
	 * Convenience method for instances, easy error logging
	 */
	protected void error(String msg) {
		Devotion.logger().severe(msg);
	}

	/**
	 * Convenience method for instances, easy error logging w/ error report
	 */
	protected void error(String msg, Throwable error) {
		Devotion.logger().log(Level.SEVERE, msg, error);
	}

	/**
	 * Convenience method for instances, easy debug/trace logging w/ respect
	 * for debug mode on the plugin.
	 */
	protected void debug(String msg) {
		// TODO: Test if logger can leverage FINE or not...
		if (devotion().isDebug()) {
			Devotion.logger().info(" -DEBUG- " + msg);
		}
	}

	private boolean enabled = false;

	/**
	 * Check if this monitor is active or not
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Method exposed to implementations to allow control of enabled status.
	 */
	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Implementations must implement this with appropriate steps including configuration,
	 *   listener registration, task execution, DAO registration, and anything else necessary.
	 */
	public abstract onEnable();
	/**
	 * Implementations must implement this with appropriate steps including unregistering
	 *   any listeners, halting any tasks, DAO unregistration, etc.
	 */
	public abstract onDisable();
}
