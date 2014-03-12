package de.uniwue.smooth.collision.geom;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents a finite part of a 2D line.
 */
public class LineSegment {
	
	private Point2D from;
	private Point2D to;
	
	/**
	 * Create a line segment between two points.
	 * @param from Start point of the line.
	 * @param to End point of the line.
	 */
	public LineSegment(Pair<Integer> from, Pair<Integer> to) {
		this(Point2DOperations.fromIntegerPair(from), Point2DOperations.fromIntegerPair(to));
	}

	/**
	 * Create a line segment between two points.
	 * @param endpoints The two points.
	 */
	public LineSegment(Pair<Pair<Integer>> endpoints) {
		this(endpoints.getFirst(), endpoints.getSecond());
	}
	
	/**
	 * Create a line segment between two points.
	 * @param from Start point of the line.
	 * @param to End point of the line.
	 */
	public LineSegment(Point2D from, Point2D to) {
		this.from = from;
		this.to = to;
	}

	public Point2D getFrom() {
		return from;
	}

	public Point2D getTo() {
		return to;
	}

	/**
	 * Get the line that this line segment lies on.
	 * 
	 * This line segment is the intersection of the line
	 * and the box returned by {@link #getBoundingBox()}.
	 * 
	 * @return Line which contains this line segment.
	 */
	public Line getLine() {
		return new Line(getFrom(), getTo());
	}
	
	/**
	 * Get the bounding box of this line.
	 * 
	 * This line segment is the intersection of the box
	 * and the line returned by {@link #getLine()}.
	 * 
	 * @return Boundaries of this line segment.
	 */
	public Box getBoundingBox() {
		return new Box(getFrom(), getTo());
	}
	
	/**
	 * Calculates the intersection points between this line segment and another one.
	 * 
	 * @param lineSegment The other line segment to calculate the intersections for.
	 * @return The 0 or 1 intersection points, null if the line segments overlap in more then one point.
	 */
	public Collection<Point2D> intersections(final LineSegment lineSegment) {
		Collection<Point2D> intersectionsLines = getLine().intersections(lineSegment.getLine());
		if(intersectionsLines == null) { // their lines are equal
			Box boundingBoxIntersection = getBoundingBox().intersect(lineSegment.getBoundingBox());
			if(boundingBoxIntersection == null) { // discrete
				return Collections.<Point2D>emptyList();
			} else if (boundingBoxIntersection.isEmpty()) { // endpoints overlap
				return Collections.singletonList(boundingBoxIntersection.getFrom());
			} else { // multiple points overlap
				return null;
			}
		} else { // their lines have 0 or 1 intersections
			return CollectionUtils.select(intersectionsLines, new Predicate<Point2D>() {
				@Override
				public boolean evaluate(Point2D point) {
					return getBoundingBox().contains(point) && lineSegment.getBoundingBox().contains(point);
				}
			});
		}
	}
	
	/**
	 * Check if a points lies on the line segment, or on one of its endpoints.
	 * @param point The point to check.
	 * @return If or not the point is on the line segment.
	 */
	public boolean contains(Point2D point) {
		return getLine().contains(point) && getBoundingBox().contains(point);
	}

	@Override
	public String toString() {
		return "LineSegment [from=" + getFrom() + ", to=" + getTo() + "]";
	}
}
