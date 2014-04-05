package de.uniwue.smooth.collision;

import java.util.Collection;

import de.uniwue.smooth.collision.geom.Bounded;
import de.uniwue.smooth.collision.geom.Box;
import de.uniwue.smooth.collision.segments.Segment;

/**
 * Manages the size of a collection of curves.
 */
public class BoundariesManager {
	
	private Box boundaries = null;
	
	/**
	 * Get the total width of all the curves.
	 * @return Width of the curves.
	 */
	public double getWidth() {
		return boundaries.getXInterval().getExtend();
	}
	
	/**
	 * Get the total height of all the curves.
	 * @return Height of the curves.
	 */
	public double getHeight() {
		return boundaries.getYInterval().getExtend();
	}
	
	/**
	 * Add some segments to the collection.
	 * @param segments The segments to add.
	 */
	public void addAll(Collection<Segment> segments) {
		for(Segment segment : segments) {
			add(segment);
		}
	}
	
	/**
	 * Add one segment to the collection.
	 * @param segment The segement to add.
	 */
	public void add(Segment segment) {
		Object body = segment.getBody();
		if(body instanceof Bounded) {
			Bounded bounded = (Bounded) body;
			Box boundingBox = bounded.getBoundingBox();
			if(boundaries == null) {
				boundaries = boundingBox;
			} else {
				boundaries = boundaries.merge(boundingBox);
			}
		} else {
			throw new IllegalArgumentException("Body not bounded: " + body);
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + boundaries;
	}

}
