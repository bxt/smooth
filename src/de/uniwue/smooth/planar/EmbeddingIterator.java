package de.uniwue.smooth.planar;




public interface EmbeddingIterator<V,E> {
	
	public V getVertex();
	public E getEdge();
	public void nextAroundVertex();
	public void nextAroundFace();
	public void oppositeOnEdge();
	
}
