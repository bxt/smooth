package de.uniwue.smooth.planar;

import java.util.List;

/**
 * Implements an iterator and some of the embedding methods for embeddings based
 * on adjacency lists.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public abstract class AdjacencyListEmbedding<V, E> implements Embedding<V, E> {
	
	/**
	 * Get the list of edges around a vertex in the order of the embedding.
	 * @param vertex The vertex whose connected edges are returned.
	 * @return A list of edges around the given vertex.
	 */
	protected abstract List<E> getAdjacent(V vertex);
	
	/**
	 * Get the opposite vertex on an edge.
	 * @param vertex The other vertex on the edge.
	 * @param edge The edge to get the opposite vertex at.
	 * @return The vertex connected to the given edge which is not the given vertex.
	 */
	protected abstract V getOpposite(V vertex, E edge);
	
	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorAtVertex(V vertex) {
		return getEmbeddingIterator(vertex, getAdjacent(vertex).get(0));
	}

	@Override
	public EmbeddingIterator<V, E> getEmbeddingIterator(V vertex, E edge) {
		return new AdjacencyListEmbeddingIterator(vertex, edge);
	}

	/**
	 * Implements the {@link EmbeddingIterator} for an embedding based on adjacency lists.
	 */
	private class AdjacencyListEmbeddingIterator implements EmbeddingIterator<V, E> {
		private V vertex;
		private E edge;
		
		public AdjacencyListEmbeddingIterator(V vertex, E edge) {
			this.vertex = vertex;
			this.edge = edge;
		}

		@Override
		public V getVertex() {
			return vertex;
		}
		
		@Override
		public E getEdge() {
			return edge;
		}
		
		@Override
		public void nextAroundVertex() {
			List<E> adjacent = getAdjacent(vertex);
			edge = adjacent.get((adjacent.indexOf(edge) + 1) % adjacent.size());
		}
		
		@Override
		public void nextAroundFace() {
			oppositeOnEdge();
			nextAroundVertex();
		}
		
		@Override
		public void oppositeOnEdge() {
			vertex = getOpposite(vertex, edge);
		}
	}
	
}
