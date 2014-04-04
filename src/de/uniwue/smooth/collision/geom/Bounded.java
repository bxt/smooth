package de.uniwue.smooth.collision.geom;

/**
 * A body/curve that fits into a bounding box.
 */
public interface Bounded {
	
	/**
	 * Get the bounding box of this body/curve.
	 * @return The bounding box containing this body/curve.
	 */
	public Box getBoundingBox();
	
}
