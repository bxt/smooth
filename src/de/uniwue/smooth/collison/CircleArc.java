package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

public class CircleArc extends AbstractCollisionDomain implements CollisionDomain {

	private Circle circle; 
	private Sector sector;
		
	public static CircleArc getCircleArc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		Point2D fromPoint = Point2DOperations.fromIntegerPair(from);
		Point2D toPoint = Point2DOperations.fromIntegerPair(to);
		Point2D midPoint = Point2DOperations.fromIntegerPair(mid);
		return getCircleArc(fromPoint, midPoint, toPoint);
	}

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
	
	/**
	 * Check if a points lies on the circle arc.
	 * @param point The point to check.
	 * @return If or not the point is on the circle arc.
	 */
	public boolean contains(Point2D point) {
		return circle.contains(point) && sector.contains(Point2DOperations.subtract(point, circle.getCenter()));
	}

	@Override
	public String toString() {
		return "CircleArc [circle=" + circle + ", sector=" + sector + "]";
	}
}
