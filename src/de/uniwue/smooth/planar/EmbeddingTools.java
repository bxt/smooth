package de.uniwue.smooth.planar;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Factory;

/**
 * Utilities for using an {@link EmbeddingIterator}.  
 */
public class EmbeddingTools {
	
	/**
	 * Returns the adjacency list of edges at the current vertex of the iterator with the current
	 * edge first.
	 * 
	 * @param embeddingIterator The iterator to start the list at. 
	 * @return A list of edges around the current vertex of the iterator.
	 */
	public static <E> List<E> listAroundVertex(EmbeddingIterator<?, E> embeddingIterator) {
		return listAroundVertex(embeddingIterator, null);
	}
	
	/**
	 * Returns the adjacency list of edges at the current vertex of the iterator with the current
	 * edge first. Allows to use a custom list implementation.
	 * 
	 * @param embeddingIterator The iterator to start the list at. 
	 * @param list_factory The factory used to create the list. Defaults to an {@link ArrayList} when <tt>null</tt>.
	 * @return A list created by the given factory containign the edges around the current vertex of the iterator.
	 */
	public static <E> List<E> listAroundVertex(EmbeddingIterator<?, E> embeddingIterator, Factory<List<E>> list_factory) {
		if (list_factory == null) list_factory = new ListFactory<E>();
		E startEdge = embeddingIterator.getEdge();
		List<E> list = list_factory.create();
		do {
			list.add(embeddingIterator.getEdge());
			embeddingIterator.nextAroundVertex();
		} while (!embeddingIterator.getEdge().equals(startEdge));
		return list;
	}
	
	/**
	 * Creates an iterator to retrieve the adjacency list of edges at the given vertex.
	 * 
	 * @param embedding The embedding which determines the order of the edges in the list.
	 * @param vertex The vertex whose adjacency list is to be returned.
	 * @return The adjacency list of edges at the given vertex.
	 */
	public static <V, E> List<E> listAroundVertex(Embedding<V, E> embedding, V vertex) {
		return listAroundVertex(embedding, vertex, null);
	}
	
	/**
	 * Creates an iterator to retrieve the adjacency list of edges at the given vertex.
	 * Allows to use a custom list implementation.
	 * 
	 * @param embedding The embedding which determines the order of the edges in the list.
	 * @param vertex The vertex whose adjacency list is to be returned.
	 * @param list_factory The factory used to create the list. Defaults to an {@link ArrayList} when <tt>null</tt>.
	 * @return The adjacency list of edges at the given vertex.
	 */
	public static <V, E> List<E> listAroundVertex(Embedding<V, E> embedding, V vertex, Factory<List<E>> list_factory) {
		EmbeddingIterator<V, E> embeddingIterator = embedding.getEmbeddingIteratorAtVertex(vertex);
		return listAroundVertex(embeddingIterator, list_factory);
	}
	
	/**
	 * Returns the list of vertices around the current face of the iterator.
	 * 
	 * @param embeddingIterator Iterator pointing at the half edge of the face.
	 * @return List of vertices around that face.
	 */
	public static <V> List<V> listAroundFace(EmbeddingIterator<V, ?> embeddingIterator) {
		return listAroundFace(embeddingIterator, null);
	}
	
	/**
	 * Returns the list of vertices around the current face of the iterator. Allows to use
	 * a custom list implementation.
	 * 
	 * @param embeddingIterator Iterator pointing at the half edge of the face.
	 * @param list_factory The factory used to create the list. Defaults to an {@link ArrayList} when <tt>null</tt>.
	 * @return Iterator pointing at the half edge of the face.
	 */
	public static <V> List<V> listAroundFace(EmbeddingIterator<V, ?> embeddingIterator, Factory<List<V>> list_factory) {
		if (list_factory == null) list_factory = new ListFactory<V>();
		V startVertex = embeddingIterator.getVertex();
		List<V> list = list_factory.create();
		do {
			list.add(embeddingIterator.getVertex());
			embeddingIterator.nextAroundFace();
		} while (!embeddingIterator.getVertex().equals(startVertex));
		return list;
	}
	
	/**
	 * Default list factory if none is given.
	 *
	 * @param <T> Type of elements contained in the list.
	 */
	private static class ListFactory<T> implements Factory<List<T>> {
		@Override
		public List<T> create() {
			return new ArrayList<T>();
		}
	}
	
}
