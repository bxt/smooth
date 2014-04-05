package de.uniwue.smooth.collision.segments;

import java.util.Collection;

public interface SegmentedEdge {

	public abstract Collection<Segment> getSegments();

	public abstract <S extends Segment> S getSegment(Class<S> segmentType);

}