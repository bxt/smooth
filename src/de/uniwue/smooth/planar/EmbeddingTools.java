package de.uniwue.smooth.planar;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Factory;

public class EmbeddingTools {
	
	public static <E> List<E> listAroundVertex(EmbeddingIterator<?, E> embeddingIterator) {
		return listAroundVertex(embeddingIterator, null);
	}
	
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
	
	public static <V, E> List<E> listAroundVertex(Embedding<V, E> embedding, V vertex) {
		return listAroundVertex(embedding, vertex, null);
	}
	
	public static <V, E> List<E> listAroundVertex(Embedding<V, E> embedding, V vertex, Factory<List<E>> list_factory) {
		EmbeddingIterator<V, E> embeddingIterator = embedding.getEmbeddingIteratorAtVertex(vertex);
		return listAroundVertex(embeddingIterator, list_factory);
	}
	
	public static <V> List<V> listAroundFace(EmbeddingIterator<V, ?> embeddingIterator) {
		return listAroundFace(embeddingIterator, null);
	}
	
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
	
	private static class ListFactory<T> implements Factory<List<T>> {
		@Override
		public List<T> create() {
			return new ArrayList<T>();
		}
	}
	
}
