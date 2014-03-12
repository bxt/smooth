package de.uniwue.smooth.collision.geom;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

public class CircleArc {

	private Circle circle; 
	private Sector sector;
	
	public static CircleArc getCircleArc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		Point2D fromPoint = Point2DOperations.fromIntegerPair(from);
		Point2D toPoint = Point2DOperations.fromIntegerPair(to);
		Point2D midPoint = Point2DOperations.fromIntegerPair(mid);
		return getCircleArc(fromPoint, midPoint, toPoint);
	}

	/**
	 * Create a half circle between two points. The circle wills start at
	 * <tt>from</tt> and end at </tt>to</tt> in counter clockwise direction.
	 * @param from Start point of the circle.
	 * @param to End point of the circle.
	 * @return A half circle between the two input points.
	 */
	public static CircleArc getCircleArc(Pair<Integer> from, Pair<Integer> to) {
		Point2D fromPoint = Point2DOperations.fromIntegerPair(from);
		Point2D toPoint = Point2DOperations.fromIntegerPair(to);
		Point2D midPoint = Point2DOperations.scale(Point2DOperations.add(fromPoint, toPoint), 0.5);
		return getCircleArc(fromPoint, midPoint, toPoint);
	}
	
	public static CircleArc getCircleArc(Point2D from, Point2D mid, Point2D to) {
		double radius = from.distance(mid);
		Circle circle = new Circle(mid, radius);
		Sector sector = Sector.getSectorBetween(Point2DOperations.subtract(from, mid), Point2DOperations.subtract(to, mid));
		return new CircleArc(circle, sector);
	}

	private CircleArc(Circle circle, Sector sector) {
		this.circle = circle;
		this.sector = sector;
	}

	public Circle getCircle() {
		return circle;
	}

	public Sector getSector() {
		return sector;
	}

	/**
	 * Calculates the intersection points between this circle arc and another one.
	 * 
	 * @param circleArc The other circle arc to calculate the intersections with this one for.
	 * @return The 0, 1 or 2 intersection points or null if the arcs overlap.
	 */
	public Collection<Point2D> intersections(final CircleArc circleArc) {
		Collection<Point2D> intersectionsCircles = getCircle().intersections(circleArc.getCircle());
		if(intersectionsCircles == null) { // circles are equal
			return getSector().intersects(circleArc.getSector()) ? null : Collections.<Point2D>emptyList();
		} else {
			return CollectionUtils.select(intersectionsCircles, new Predicate<Point2D>() {
				@Override
				public boolean evaluate(Point2D point) {
					return sectorContains(point) && circleArc.sectorContains(point);
				}
			});
		}
	}
	
	/**
	 * Calculates the intersection points between this circle arc and a {@link LineSegment}.
	 * 
	 * @param lineSegment The line segment to calculate the intersections for.
	 * @return The 0, 1 or 2 intersection points.
	 */
	public Collection<Point2D> intersections(final LineSegment lineSegment) {
		Collection<Point2D> intersectionsLine = getCircle().intersections(lineSegment);
		return CollectionUtils.select(intersectionsLine, new Predicate<Point2D>() {
			@Override
			public boolean evaluate(Point2D point) {
				return sectorContains(point);
			}
		});
	}
	
	/**
	 * Check if a points lies on the circle arc.
	 * @param point The point to check.
	 * @return If or not the point is on the circle arc.
	 */
	public boolean contains(Point2D point) {
		return getCircle().contains(point) && sectorContains(point);
	}

	/**
	 * Check if a points lies in the sector of this circle arc.
	 * @param point The point to check.
	 * @return If or not the point is on the sector of this arc.
	 */
	private boolean sectorContains(Point2D point) {
		return getSector().contains(Point2DOperations.subtract(point, getCircle().getCenter()));
	}

	@Override
	public String toString() {
		return "CircleArc [circle=" + circle + ", sector=" + sector + "]";
	}
}