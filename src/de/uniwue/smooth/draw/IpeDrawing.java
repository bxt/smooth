package de.uniwue.smooth.draw;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.collections15.Factory;


/**
 * Wraps {@link IpeDraw} for stateful incremental creation of drawing. 
 */
public class IpeDrawing implements Factory<Appendable> {
	
	private Appendable appendable;
	
	public IpeDrawing() {
		appendable = new StringBuilder();
		initialize();
	}
	
	public IpeDrawing(Appendable appendable) {
		this.appendable = appendable;
		initialize();
	}
	
	public IpeDrawing(String fileName) {
		try {
			this.appendable = new BufferedWriter(new FileWriter(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		initialize();
	}
	
	/**
	 * Initialize the drawing.
	 */
	private void initialize() {
		try {
			appendable.append(IpeDraw.getIpePreamble());
			appendable.append(IpeDraw.getIpeConf());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Draw some ipe code into the drawing.
	 * @param ipeContent
	 */
	public void draw(String ipeContent) {
		try {
			appendable.append(ipeContent);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Appendable create() {
		try {
			appendable.append(IpeDraw.getIpeEnd());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(appendable instanceof Closeable) {
				try {
					((Closeable) appendable).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return appendable;
	}
	
	@Override
	public String toString() {
		return "Drawing: " + appendable.toString() + " ...";
	}
	
}
