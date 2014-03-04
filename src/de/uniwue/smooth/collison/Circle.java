package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

/**
 * Represents a 2D circle.
 */
public class Circle {
	
	private Point2D center;
	private double radius;
	
	/**
	 * Construct a circle with its central point and its radius.
	 * @param center Center of the circle.
	 * @param radius Radius of the circle.
	 */
	public Circle(Point2D center, double radius) {
		super();
		this.center = center;
		this.radius = radius;
	}

	public Point2D getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}
	
}
