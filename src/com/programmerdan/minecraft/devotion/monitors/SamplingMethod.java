package com.programmerdan.minecraft.devotion.monitors;

/**
 * Labels for sampling methods. Monitors may support some of all of these. Or none.
 * 
 * @author ProgrammerDan
 */
public enum SamplingMethod {
	periodic,
	continuous,
	roundrobin,
	onevent
}
