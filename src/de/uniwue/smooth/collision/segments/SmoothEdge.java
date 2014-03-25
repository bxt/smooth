package de.uniwue.smooth.collision.segments;

import java.util.Collection;

import edu.uci.ics.jung.graph.util.Pair;

public class SmoothEdge {
	
	private Collection<Segment> segments;
	private Pair<Integer> kink;
	private SmoothEdgeType edgeType;
	
	public SmoothEdge(Collection<Segment> segments, Pair<Integer> kink,
			SmoothEdgeType edgeType) {
		super();
		this.segments = segments;
		this.kink = kink;
		this.edgeType = edgeType;
	}

	public Collection<Segment> getSegments() {
		return segments;
	}
	public Pair<Integer> getKink() {
		return kink;
	}
	public SmoothEdgeType getEdgeType() {
		return edgeType;
	}

	@SuppressWarnings("unchecked")
	public <S extends Segment> S getSegment(Class<S> segmentType) {
		for(Segment segment : getSegments())
			if (segment.getClass().equals(segmentType))
				return (S) segment;
		return null;
	}
	
}
