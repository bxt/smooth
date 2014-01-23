package de.uniwue.smooth.orthogonal;

import java.util.Map;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A layout which returns integer position in an orthogonal grid. 
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public interface OrthogonalLayout<V, E> extends Layout<V, E> {
	
	/**
	 * Get the location of a vertex in the orthogonal grid.
	 * 
	 * @param v Vertex to return the location of. 
	 * @return Left/top coordinates of the grid point for this vertex.
	 */
	public Pair<Integer> getVertexLocation(V v);
	
	/**
	 * Get the location of the midpoint of an edge in the orthogonal grid.
	 * @param e Edge to return the location of. 
	 * @return Left/top coordinates of the grid point for this edge.
	 */
	public Pair<Integer> getEdgeLocation(E e);
	
	/**
	 * Get the mapping of ports and edges at a given vertex.
	 * 
	 * In an orthogonal layout an edge may only exit a vertex in four directions, called port.
	 * 
	 * @param v Vertex to get the assignment for.
	 * @return A map from ports to the edges at that port.
	 */
	public Map<Port, E> getPortAssignment(V v);
	
}
