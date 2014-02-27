package de.uniwue.smooth.palm;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * An ordering of vertices in a biconnected graph such that
 * every vertex is assigned a number and is connected to at least one
 * vertex with a lower number and one with a higher number.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public interface StOrdering<V, E> extends Comparator<V> {
	
	/**
	 * List the vertices in the order of their numbers.
	 * @return A list of vertices in the st-ordering's sequence.
	 */
	public abstract List<V> getList();

	/**
	 * Get a map from vertices to their number.
	 * @return A map vertex->number.
	 */
	public abstract Map<V, Integer> asNumbers();

}