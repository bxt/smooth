package de.uniwue.smooth.collision.segments;

import java.util.Collection;

import edu.uci.ics.jung.graph.util.Pair;

public class StraightlineEdge extends AbstractSegmentedEdge {

	private Pair<Integer> midpoint;
	
	public StraightlineEdge(Collection<Segment> segments, Pair<Integer> midpoint) {
		super(segments);
		this.midpoint = midpoint;
	}

	public Pair<Integer> getMidpoint() {
		return midpoint;
	}

	@Override
	public String toString() {
		return "StraightlineEdge [getMidpoint()=" + getMidpoint()
				+ ", getSegments()=" + getSegments() + "]";
	}
	
}
