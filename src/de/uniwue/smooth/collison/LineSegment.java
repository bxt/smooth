package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

public class LineSegment extends AbstractCollisionDomain implements CollisionDomain {
	
	private Point2D from;
	private Point2D to;

	public LineSegment(Pair<Integer> from, Pair<Integer> to) {
		this(Point2DOperations.fromIntegerPair(from), Point2DOperations.fromIntegerPair(to));
	}

	public LineSegment(Pair<Pair<Integer>> endpoints) {
		this(endpoints.getFirst(), endpoints.getSecond());
	}
	
	public LineSegment(Point2D from, Point2D to) {
		this.from = from;
		this.to = to;
	}

	@Override
	protected Integer collisionOffset(CircleArc cirecleArc) {
		// TODO Auto-generated method stub
		return collisionOffsetUnknownCombination(cirecleArc);
	}

	@Override
	protected Integer collisionOffset(LineSegment line) {
		// TODO Auto-generated method stub
		return collisionOffsetUnknownCombination(line);
	}
	
	public Line getLine() {
		return new Line(from, to);
	}
	
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
