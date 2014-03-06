package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

public class CircleArc extends AbstractCollisionDomain implements CollisionDomain {

	public CircleArc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		// TODO Auto-generated constructor stub
	}

	public CircleArc(Pair<Integer> from, Pair<Integer> to) {
		// TODO Auto-generated constructor stub
	}

	private Circle circle; 
	private Sector sector;
		
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

}
