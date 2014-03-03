package de.uniwue.smooth.util;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.util.Pair;

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
	 * Transforms the input point to a pair of integers like result of a call to 
	 * {@link Point2DTranformers#toIntegerPair(Point2D)} for the input point.
	 */
	public static class ToIntegerPair implements Transformer<Point2D, Pair<Integer>> {
		@Override
		public Pair<Integer> transform(Point2D input) {
			return toIntegerPair(input);
		}
	}
	
	/**
	 * Transforms the input pair of integers to a point like the result of a call to 
	 * {@link Point2DTranformers#fromIntegerPair(Pair)} for the input pair.
	 */
	public static class FromIntegerPair implements Transformer<Pair<Integer>, Point2D> {
		@Override
		public Point2D transform(Pair<Integer> input) {
			return fromIntegerPair(input);
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
}
