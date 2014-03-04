package de.uniwue.smooth.util.point2d;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Some convenient static methods on {@link Point2D}s.  
 */
public class Point2DOperations {

	/**
	 * Calculates the result of the {@link Math#atan2(double, double)}
	 * function (angle around x coordinate axis) for the input point.
	 * @param point2d Input point.
	 * @return Angle of the polar coordinate of the input point.
	 */
	public static double atan2(Point2D point2d) {
		return Math.atan2(point2d.getY(), point2d.getX());
	}

	/**
	 * Converts a point to a pair of integers, rounding to the nearest integer values.
	 * @param point2d Input point.
	 * @return Same point converted to a pair of integer coordinates.
	 */
	public static Pair<Integer> toIntegerPair(Point2D point2d) {
		return new Pair<Integer>((int)(point2d.getX()+0.5), (int)(point2d.getY()+0.5));
	}

	/**
	 * Converts a pair of integers to a double point.
	 * @param pair Input pair.
	 * @return Same coordinates converted to a point.
	 */
	public static Point2D fromIntegerPair(Pair<Integer> pair) {
		Point2D point = new Point2D.Double(pair.getFirst().doubleValue(), pair.getSecond().doubleValue());
		return point;
	}
	
	/**
	 * Subtract two points, component wise.
	 * @param a Input point.
	 * @param b Point subtracted from the input point.
	 * @return The point <tt>a - b</tt>.
	 */
	public static Point2D subtract(Point2D a, Point2D b) {
		return new Point2D.Double(a.getX()-b.getX(), a.getY()-b.getY());
	}

	/**
	 * Add two points, component wise.
	 * @param a Input point.
	 * @param b Point added to the input point.
	 * @return The point <tt>a + b</tt>.
	 */
	public static Point2D add(Point2D a, Point2D b) {
		return new Point2D.Double(a.getX()+b.getX(), a.getY()+b.getY());
	}
	
}
