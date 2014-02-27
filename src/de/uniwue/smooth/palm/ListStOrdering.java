package de.uniwue.smooth.palm;

import java.util.List;

/**
 * Creates an st-ordering based on the order of the vertices in a given list.
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class ListStOrdering<V, E> extends AbstractStOrdering<V, E> implements StOrdering<V, E> {
	
	private List<V> vertexList;
	
	/**
	 * Build an st-ordering representing the order of the vertices in a list.
	 * @param vertexList The list whose vertex sequence determines the ordering.
	 */
	public ListStOrdering(List<V> vertexList) {
		this.vertexList = vertexList;
	}
	
	@Override
	public List<V> getList() {
		return vertexList;
	}
	
}
