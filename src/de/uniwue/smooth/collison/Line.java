package de.uniwue.smooth.collison;

import edu.uci.ics.jung.graph.util.Pair;

public class Line extends AbstractCollisionDomain implements CollisionDomain {

	public Line(Pair<Integer> from, Pair<Integer> to) {
		// TODO Auto-generated constructor stub
	}

	public Line(Pair<Pair<Integer>> endpoints) {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Integer collisionOffset(CircleArc cirecleArc) {
		// TODO Auto-generated method stub
		return collisionOffsetUnknownCombination(cirecleArc);
	}

	@Override
	protected Integer collisionOffset(Line line) {
		// TODO Auto-generated method stub
		return collisionOffsetUnknownCombination(line);
	}

}
