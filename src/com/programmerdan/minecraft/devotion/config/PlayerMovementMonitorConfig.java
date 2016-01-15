package com.programmerdan.minecraft.devotion.config;

import com.programmerdan.minecraft.devotion.monitors.SamplingMethod;

/**
 * Wrapper holding configuration for player movement monitor
 * @author Daniel
 *
 */
public class PlayerMovementMonitorConfig {
	public SamplingMethod technique;
	
	// for  CONTINUOUS_ROUNDROBIN,
	public int sampleSize;
	
	// for  ON_EVENT, CONTINUOUS_ROUNDROBIN
	public long timeoutBetweenSampling;
}
