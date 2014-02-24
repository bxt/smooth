package de.uniwue.smooth.draw;

import org.apache.commons.collections15.Factory;

/**
 * Wraps {@link IpeDraw} for stateful incremental creation of drawing. 
 */
public class ListAbstractIpeDrawing implements Factory<String> {
	
	private StringBuilder sb = new StringBuilder();
	
	/**
	 * Initialize the drawing.
	 */
	public ListAbstractIpeDrawing() {
	}
	
	/**
	 * Draw a string of ipe code into the buffer.
	 * @param ipeContent The string to save.
	 */
	public void draw(String ipeContent) {
		sb.append(ipeContent);
	}
	
	/**
	 * Return the accumulated string with a suitable header and footer to create an ipe document.
	 */
	@Override
	public String create() {
		return toString();
	}
	
	/**
	 * Returns what {@link #create()} would return. 
	 */
	@Override
	public String toString() {
		return sb.toString();
	}

}
