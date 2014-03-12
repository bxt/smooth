package de.uniwue.smooth.collision.geom;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.uniwue.smooth.util.point2d.Point2DComparatorFactories;

public abstract class TestUtils {
	
	public static double DELTA = 0.0001;
	
	public static Point2D p(double x, double y) {
		return new Point2D.Double(x, y);
	}
	
	public static List<Point2D> sortIntersections(Collection<Point2D> intersections) {
		List<Point2D> intersectionList = new ArrayList<>(intersections);
		Collections.sort(intersectionList, Point2DComparatorFactories.xy());
		return intersectionList;
	}
	
}
