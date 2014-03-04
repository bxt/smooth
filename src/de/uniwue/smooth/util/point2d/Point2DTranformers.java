package de.uniwue.smooth.util.point2d;

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
	 * {@link Point2DOperations#atan2(Point2D)} for the input point.
	 */
	public static class Atan2 implements Transformer<Point2D, Double> {
		@Override
		public Double transform(Point2D input) {
			return Point2DOperations.atan2(input);
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
	 * {@link Point2DOperations#toIntegerPair(Point2D)} for the input point.
	 */
	public static class ToIntegerPair implements Transformer<Point2D, Pair<Integer>> {
		@Override
		public Pair<Integer> transform(Point2D input) {
			return Point2DOperations.toIntegerPair(input);
		}
	}
	
	/**
	 * Transforms the input pair of integers to a point like the result of a call to 
	 * {@link Point2DOperations#fromIntegerPair(Pair)} for the input pair.
	 */
	public static class FromIntegerPair implements Transformer<Pair<Integer>, Point2D> {
		@Override
		public Point2D transform(Pair<Integer> input) {
			return Point2DOperations.fromIntegerPair(input);
		}
	}
}
