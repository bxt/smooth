package de.uniwue.smooth.util.point2d;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Some convenient static methods on {@link Point2D}s.  
 */
public abstract class Point2DOperations {

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

	/**
	 * Returns the point with negated components.
	 * 
	 * This is equivalent to a point reflection around the origin.
	 * 
	 * @param a Input point.
	 * @param b Point added to the input point.
	 * @return The point <tt>a + b</tt>.
	 */
	public static Point2D neg(Point2D a) {
		return new Point2D.Double(-a.getX(), -a.getY());
	}
	
	/**
	 * Computes the dot (scalar) product of the coordinates of two points,
	 * i.e. <tt>a.x * b.y + b.x * a.y</tt>.
	 * 
	 * @param a The first point.
	 * @param b The second point.
	 * @return The scalar product of the points.
	 */
	public static double dot(Point2D a, Point2D b) {
		return a.getX() * b.getY() - b.getX() * a.getY();
	}
	
	/**
	 * Returns the point with scaled components.
	 * 
	 * @param a Input point.
	 * @param b Factor to sale the components with.
	 * @return The scaled point <tt>a * b</tt>.
	 */
	public static Point2D scale(Point2D a, double factor) {
		return new Point2D.Double(factor * a.getX(), factor * a.getY());
	}
	
}
