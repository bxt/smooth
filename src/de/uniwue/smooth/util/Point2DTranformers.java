package de.uniwue.smooth.util;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

public class Point2DTranformers {

	public static class Atan2 implements Transformer<Point2D, Double> {
		@Override
		public Double transform(Point2D input) {
			return atan2(input);
		}
	}

	public static class X implements Transformer<Point2D, Double> {
		@Override
		public Double transform(Point2D input) {
			return input.getX();
		}
	}

	public static class Y implements Transformer<Point2D, Double> {
		@Override
		public Double transform(Point2D input) {
			return input.getY();
		}
	}

	public static double atan2(Point2D point2d) {
		return Math.atan2(point2d.getY(), point2d.getX());
	}
}
