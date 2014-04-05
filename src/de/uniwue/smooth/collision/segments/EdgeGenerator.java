package de.uniwue.smooth.collision.segments;

public interface EdgeGenerator<V, E, SE extends SegmentedEdge> {

	public abstract SE generateEdge(E e);

}