package de.uniwue.smooth.planar;

public interface Embedding<V, E> {

	public abstract EmbeddingIterator<V, E> getEmbeddingIteratorOnOuterface();

	public abstract EmbeddingIterator<V, E> getEmbeddingIteratorAtEdge(E edge);

	public abstract EmbeddingIterator<V, E> getEmbeddingIteratorAtVertex(
			V vertex);

	public abstract EmbeddingIterator<V, E> getEmbeddingIterator(V vertex,
			E edge);

}