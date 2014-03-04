package de.uniwue.smooth.util.point2d;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.comparators.ComparableComparator;
import org.apache.commons.collections15.comparators.ComparatorChain;
import org.apache.commons.collections15.comparators.TransformingComparator;

public class Point2DComparatorFactories {
	
	public static Comparator<Point2D> x() {
		return byTransformer(new Point2DTranformers.X());
	}
	
	public static Comparator<Point2D> y() {
		return byTransformer(new Point2DTranformers.Y());
	}
	
	public static Comparator<Point2D> atan2() {
		return byTransformer(new Point2DTranformers.Atan2());
	}
	
	public static Comparator<Point2D> xy() {
		return new ComparatorChain<>(Arrays.asList(x(), y()));
	}
	
	private static <T extends Comparable<? super T>> Comparator<Point2D> byTransformer(Transformer<Point2D, T> transformer) {
		return new TransformingComparator<Point2D, T>(transformer, new ComparableComparator<T>());
	}
	
}
