package de.uniwue.smooth.draw;

import org.apache.commons.collections15.Factory;

/**
 * Wraps {@link IpeDraw} for stateful incremental creation of drawing. 
 */
public class AbstractIpeDrawing implements Factory<String> {
	
	private StringBuilder sb = new StringBuilder();
	
	public AbstractIpeDrawing() {
		sb.append(IpeDraw.getIpePreamble());
		sb.append(IpeDraw.getIpeConf());
	}
	
	public void draw(String ipeContent) {
		sb.append(ipeContent);
	}
	
	@Override
	public String create() {
		return toString();
	}
	
	@Override
	public String toString() {
		return sb.toString() + IpeDraw.getIpeEnd();
	}

}
