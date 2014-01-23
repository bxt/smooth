package de.uniwue.smooth.planar;

/**
 * Iterator for walking around in a planar embedding of a graph
 * pointing at a half edge (edge and one incident vertex).
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public interface EmbeddingIterator<V,E> {
	
	/**
	 * Get the vertex at the current position.
	 * @return Current vertex.
	 */
	public V getVertex();
	
	/**
	 * Get the edge at the current position. 
	 * @return Current edge.
	 */
	public E getEdge();
	
	/**
	 * Advance this iterator to the next (clockwise) edge around the current vertex.
	 */
	public void nextAroundVertex();
	
	/**
	 * Advance this iterator to the next edge and vertex around the current face.
	 */
	public void nextAroundFace();
	
	/**
	 * Set the current vertex to the opposite vertex on the current edge.
	 * 
	 * Also switches to the other face at the current edge.
	 */
	public void oppositeOnEdge();
	
}
