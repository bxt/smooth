package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

public class LineSegment extends AbstractCollisionDomain implements CollisionDomain {
	
	private Point2D from;
	private Point2D to;

	public LineSegment(Pair<Integer> from, Pair<Integer> to) {
		this.from = Point2DOperations.fromIntegerPair(from);
		this.to   = Point2DOperations.fromIntegerPair(to  );
	}

	public LineSegment(Pair<Pair<Integer>> endpoints) {
		this.from = Point2DOperations.fromIntegerPair(endpoints.getFirst());
		this.to   = Point2DOperations.fromIntegerPair(endpoints.getSecond());
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

}
