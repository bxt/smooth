package de.uniwue.smooth.collision.segments;

import java.util.Collection;

public abstract class AbstractSegmentedEdge implements SegmentedEdge {
	
	private Collection<Segment> segments;
	
	public AbstractSegmentedEdge(Collection<Segment> segments) {
		super();
		this.segments = segments;
	}

	@Override
	public Collection<Segment> getSegments() {
		return segments;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends Segment> S getSegment(Class<S> segmentType) {
		for(Segment segment : getSegments())
			if (segment.getClass().equals(segmentType))
				return (S) segment;
		return null;
	}

	@Override
	public String toString() {
		return "Edge [segments=" + segments + "]";
	}
	
}
