package com.programmerdan.minecraft.devotion;

import com.programmerdan.minecraft.devotion.dao.Flyweight;

/**
 * Abstract interface for data handling
 * 
 * @author ProgrammerDan
 */
public interface DataHandler {
	public long getDelay();
	public boolean useAdaptiveSchedule();
	public void insert(Flyweight data);
}
