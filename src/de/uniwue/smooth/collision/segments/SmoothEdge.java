package de.uniwue.smooth.collision.segments;

import java.util.Collection;

import edu.uci.ics.jung.graph.util.Pair;

public class SmoothEdge extends AbstractSegmentedEdge implements SegmentedEdge {
	
	private Pair<Integer> kink;
	private SmoothEdgeType edgeType;
	
	public SmoothEdge(Collection<Segment> segments, Pair<Integer> kink,
			SmoothEdgeType edgeType) {
		super(segments);
		this.kink = kink;
		this.edgeType = edgeType;
	}

	public Pair<Integer> getKink() {
		return kink;
	}
	public SmoothEdgeType getEdgeType() {
		return edgeType;
	}

	@Override
	public String toString() {
		return "SmoothEdge [segments=" + getSegments() + ", kink=" + kink
				+ ", edgeType=" + edgeType + "]";
	}
	
}
