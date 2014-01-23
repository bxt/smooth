package de.uniwue.smooth.planar;

/**
 * A planar embedding of a graph. The properties of the 
 * embedding are accessed through {@link EmbeddingIterator}s.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public interface Embedding<V, E> {

	public abstract EmbeddingIterator<V, E> getEmbeddingIteratorOnOuterface();

	public abstract EmbeddingIterator<V, E> getEmbeddingIteratorAtEdge(E edge);

	public abstract EmbeddingIterator<V, E> getEmbeddingIteratorAtVertex(
			V vertex);

	public abstract EmbeddingIterator<V, E> getEmbeddingIterator(V vertex,
			E edge);

}