package de.uniwue.smooth.planar;

public interface HalfEdge<V, E> {
	
	public V getVertex();
	public E getEdge();
	public HalfEdge<V, E> nextEdge();
	public HalfEdge<V, E> nextVertex();
	public HalfEdge<V, E> flipDirection();
}
