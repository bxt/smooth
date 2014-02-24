package de.uniwue.smooth.util;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class Point2DAngleComparator implements Comparator<Point2D> {

	@Override
	public int compare(Point2D p1, Point2D p2) {
		return Double.compare(atan2(p1), atan2(p2));
	}

	public static double atan2(Point2D point2d) {
		return Math.atan2(point2d.getY(), point2d.getX());
	}
}
