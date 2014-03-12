package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

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

	/**
	 * Get the line that this line segment lies on.
	 * 
	 * This line segment is the intersection of the line
	 * and the box returned by {@link #getBoundingBox()}.
	 * 
	 * @return Line which contains this line segment.
	 */
	public Line getLine() {
		return new Line(from, to);
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
		return new Box(from, to);
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
		return "LineSegment [from=" + from + ", to=" + to + "]";
	}
}
