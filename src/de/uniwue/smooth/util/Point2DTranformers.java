package de.uniwue.smooth.util;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

/**
 * Some convenient {@link Transformer}s that will call
 * methods on {@link Point2D}s.  
 */
public class Point2DTranformers {
	
	/**
	 * Transforms the input point to the result of a call to 
	 * {@link Point2DTranformers#atan2(Point2D)} for the input point.
	 */
	public static class Atan2 implements Transformer<Point2D, Double> {
		@Override
		public Double transform(Point2D input) {
			return atan2(input);
		}
	}

	/**
	 * Transforms the input point to the result of a call to 
	 * {@link Point2D#getX()} for the input point.
	 */
	public static class X implements Transformer<Point2D, Double> {
		@Override
		public Double transform(Point2D input) {
			return input.getX();
		}
	}

	/**
	 * Transforms the input point to the result of a call to 
	 * {@link Point2D#getY()} for the input point.
	 */
	public static class Y implements Transformer<Point2D, Double> {
		@Override
		public Double transform(Point2D input) {
			return input.getY();
		}
	}
	
	/**
	 * Calculates the result of the {@link Math#atan2(double, double)}
	 * function (angle around x coordinate axis) for the input point.
	 * @param point2d Input point.
	 * @return Angle of the polar coordinate of the input point.
	 */
	public static double atan2(Point2D point2d) {
		return Math.atan2(point2d.getY(), point2d.getX());
	}
}
