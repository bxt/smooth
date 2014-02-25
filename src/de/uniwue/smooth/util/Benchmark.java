package de.uniwue.smooth.util;

import java.text.DecimalFormat;

/**
 * Simple class to measure elapsed time. 
 */
public class Benchmark {
	
	long start;
	
	/**
	 * Construct a new timer. Calls {@link #start()} for convenience.
	 */
	public Benchmark() {
		start();
	}
	
	/**
	 * Start from current time. Reset elapsed time.
	 */
	public void start() {
		start = System.currentTimeMillis();
	}
	
	/**
	 * Currently elapsed time.
	 * @return Milliseconds elapsed since last call to {@link #start()}
	 */
	public long time() {
		return System.currentTimeMillis() - start;
	}
	
	/**
	 * Returns a text containing the currently elapsed time.
	 */
	@Override
	public String toString() {
		return "Running " + new DecimalFormat("#,###").format(time()) + "s. ";
	}
	
	/**
	 * Prints the currently elapsed time (result of {@link #toString()}) to {@link System#out}.
	 */
	public void print() {
		System.out.println(toString());
	}
	
}
