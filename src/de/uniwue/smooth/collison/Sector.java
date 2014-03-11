package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

import de.uniwue.smooth.util.point2d.Point2DOperations;


/**
 * Represents the space between two angles around the coordinate origin.
 */
public class Sector {
	
	private static final double TWO_PI = 2*Math.PI;
	
	/**
	 * Create a sector between two points, exclusive.
	 * 
	 * It is assumed that the x axis points upwards and the sector is between from
	 * and to in counterclockwise direction.
	 * 
	 * @param from Starting point.
	 * @param to End point.
	 * @return Sector between these points.
	 */
	public static Sector getSectorBetween(Point2D from, Point2D to) {
		return getSectorBetween(Point2DOperations.atan2(from), Point2DOperations.atan2(to));
	}

	/**
	 * Create a sector between two angles.
	 * 
	 * @param from Starting angle, exclusive.
	 * @param to End angle, exclusive.
	 * @return Sector between the given angles.
	 */
	public static Sector getSectorBetween(double from, double to) {
		return getSectorFrom(from, to - from);
	}
	
	/**
	 * Create a sector from an angle with a certain extend.
	 * @param from Starting angle, exclusive.
	 * @param extend Maximum offset from starting angle, exclusive.
	 * @return Sector at the given start with the given extend.
	 */
	public static Sector getSectorFrom(double from, double extend) {
		return new Sector(normalizeAngle(from), normalizeAngle(extend));
	}

	
	private double from;
	private double extend;
	
	private Sector(double from, double extend) {
		this.from = from;
		this.extend = extend;
	}
	
	/**
	 * Check if a points is in this sector.
	 * @param point The point to check.
	 * @return If or not the point is in this sector.
	 */
	public boolean contains(Point2D point) {
		double offset = normalizeAngle(Point2DOperations.atan2(point) - from);
		return offset > 0 && offset < extend;
	}
	
	@Override
	public String toString() {
		return "Sector[from=" + radiansToDegrees(from) + ", extend=" + radiansToDegrees(extend) + "]";
	}
	
	private static double normalizeAngle(double a) {
		a = a % TWO_PI;
		return a < 0 ? a + TWO_PI : a;
	}
	
	private static double radiansToDegrees(double a) {
		return (a/TWO_PI)*365;
	}
	

}
