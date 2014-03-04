package de.uniwue.smooth.collison;

import edu.uci.ics.jung.graph.util.Pair;

public class CircleArc extends AbstractCollisionDomain implements CollisionDomain {

	public CircleArc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		// TODO Auto-generated constructor stub
	}

	public CircleArc(Pair<Integer> from, Pair<Integer> to) {
		// TODO Auto-generated constructor stub
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
