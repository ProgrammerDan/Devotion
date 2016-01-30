package com.programmerdan.minecraft.devotion.util;

/**
 * Helper method to deal with flow-monitoring constructs.
 * This implements a moving average by leveraging memory and circular lists.
 * Should be very fast, but like all statistics modules will incur some overhead.
 * The public-facing update is synchronized to prevent concurrency issues. It may be necessary
 * to remove this if it proves to be a bottleneck.
 * TODO: Speed test this code.
 * 
 * @author ProgrammerDan <programmerdan@gmail.com>
 */
public class FlowHelper {
	
	private int samples;
	private long[] inflowCount;
	private long[] outflowCount;
	private long[] flowTime;
	private boolean[] inflowOverOutflow;
	
	private int nextSamplePtr;
	
	private long inflowNumerator;
	private long outflowNumerator;
	private long timeDenominator;
	
	private double inflow;
	private double outflow;
	
	private long inflowExceedsOutflowTime;
	private double exceedPercentage;
	private boolean averageExceeds;

	public FlowHelper() {
		this(120);
	}
	
	public FlowHelper(int sampleSize) {
		samples = sampleSize;
		inflowCount = new long[samples];
		outflowCount = new long[samples];
		flowTime = new long[samples];
		inflowOverOutflow = new boolean[samples];
		inflow = 0.0d;
		outflow = 0.0d;
		inflowExceedsOutflowTime = 0l;
		inflowNumerator = 0l;
		outflowNumerator = 0l;
		timeDenominator = 0l;
		nextSamplePtr = 0;
		exceedPercentage= 0.0d;
		averageExceeds = false;
	}
	
	/**
	 * Adds a new sample datapoint to this FlowHelper.
	 * 
	 * @param sampleTime The time in milliseconds over which sampling occurred
	 * @param inflowCount The number of inflows during the sample period
	 * @param outflowCount The number of outflows during the sample period
	 */
	public synchronized void sample(long sampleTime, long inflowCount, long outflowCount) {
		this.inflowCount[nextSamplePtr] = inflowCount;
		this.outflowCount[nextSamplePtr] = outflowCount;
		this.flowTime[nextSamplePtr] = sampleTime;
		this.inflowOverOutflow[nextSamplePtr] = inflowCount > outflowCount;
		update();
	}
	
	/**
	 * Internal update method. Updates the running numerator and denominator and 
	 * recomputes averages, including exceed threshold checks.
	 */
	private void update() {
		inflowNumerator += inflowCount[nextSamplePtr];
		outflowNumerator += outflowCount[nextSamplePtr];
		timeDenominator += flowTime[nextSamplePtr];
		inflowExceedsOutflowTime += inflowOverOutflow[nextSamplePtr] ? flowTime[nextSamplePtr] : 0; 
		
		nextSamplePtr = (++nextSamplePtr < samples ? nextSamplePtr : 0); // faster then ++n % limit by 4x
		
		inflowNumerator -= inflowCount[nextSamplePtr];
		outflowNumerator -= outflowCount[nextSamplePtr];
		timeDenominator -= flowTime[nextSamplePtr];
		inflowExceedsOutflowTime -= inflowOverOutflow[nextSamplePtr] ? flowTime[nextSamplePtr] : 0;
		
		if (timeDenominator == 0) {
			inflow = 0.0d;
			outflow = 0.0d;
			averageExceeds = false;
			exceedPercentage = 0.0d;
		} else {
			inflow = (double) inflowNumerator / (double) timeDenominator;
			outflow = (double) outflowNumerator / (double) timeDenominator;
			averageExceeds = inflow > outflow;
			exceedPercentage = (double) inflowExceedsOutflowTime / (double) timeDenominator;
		}
	}
	
	/**
	 * Returns true iff during the current total sample period, average inflow exceeds
	 * average outflow.
	 * 
	 * @return true if average inflow > outflow
	 */
	public boolean doesAverageInflowExceedOutflow() {
		return averageExceeds;
	}
	
	/**
	 * Returns the total time in milliseconds that inflow exceeded outflow during the total
	 * sample period.
	 * 
	 * @return number of milliseconds <= total sample time that raw inflow > raw outflow
	 */
	public long timeInflowExceedsOutflow() {
		return inflowExceedsOutflowTime;
	}
	
	/**
	 * Returns the fraction of total sample period where raw inflow exceeded raw outflow.
	 * 
	 * @return Number between 0.0 and 1.0d where 1.0 means inflow always exceeded outflow, and 0.0 means
	 *    inflow never exceeded outflow.
	 */
	public double fractionTimeInflowExceedsOutflow() {
		return exceedPercentage;
	}

	/**
	 * Average number of inflows per millisecond over total sample period.
	 * 
	 * @return average inflow rate per millisecond
	 */
	public double inflowRatePerMillis() {
		return inflow;
	}
	
	/**
	 * Average number of outflows per millisecond over total sample period.
	 * 
	 * @return average outflow rate per millisecond
	 */
	public double outflowRatePerMillis() {
		return outflow;
	}
	
	/**
	 * Total sample period as of last update.
	 * 
	 * @return total sample period in milliseconds
	 */
	public long totalSampleTime() {
		return timeDenominator;
	}
	
	/**
	 * Raw count of inflow events in total sample period
	 * 
	 * @return unadjusted count of inflow events over total sample time
	 */
	public long totalInflow() {
		return inflowNumerator;
	}
	
	/**
	 * Raw count of outflow events in total sample period
	 * 
	 * @return unadjusted count of outflow events over total sample time
	 */
	public long totalOutflow(){
		return outflowNumerator;
	}
}
