package de.uniwue.smooth.util.point2d;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.comparators.ComparableComparator;
import org.apache.commons.collections15.comparators.ComparatorChain;
import org.apache.commons.collections15.comparators.TransformingComparator;

/**
 * Allows comparison of {@link Point2D} using various criteria. 
 */
public class Point2DComparatorFactories {
	
	/**
	 * Build a comparator for sorting points by their x coordinate.
	 * @return A comparator sorting points by their x coordinate.
	 */
	public static Comparator<Point2D> x() {
		return byTransformer(new Point2DTranformers.X());
	}
	
	/**
	 * Build a comparator for sorting points by their y coordinate.
	 * @return A comparator sorting points by their y coordinate.
	 */
	public static Comparator<Point2D> y() {
		return byTransformer(new Point2DTranformers.Y());
	}
	
	/**
	 * Build a comparator for sorting points by their atan2 value, the angle around the coordinate center.
	 * @see Point2DOperations#atan2(Point2D)
	 * @return A comparator sorting points by their atan2 value.
	 */
	public static Comparator<Point2D> atan2() {
		return byTransformer(new Point2DTranformers.Atan2());
	}
	
	/**
	 * Build a comparator for sorting points by their x coordinate first, and when they equal, by their y coordinate.
	 * @return A comparator sorting points lexicographically.
	 */
	public static Comparator<Point2D> xy() {
		return new ComparatorChain<>(Arrays.asList(x(), y()));
	}
	
	private static <T extends Comparable<? super T>> Comparator<Point2D> byTransformer(Transformer<Point2D, T> transformer) {
		return new TransformingComparator<Point2D, T>(transformer, new ComparableComparator<T>());
	}
	
}
