package de.uniwue.smooth.planar;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.DirectedGraph;

public class DoublyConnectedEdgeListEmbedding<V, E> implements Embedding<V, E> {
	
	private DirectedGraph<V, E> graph;
	private Transformer<E, E> nextEdgeSource;
	private Transformer<E, E> nextEdgeDest;
	private V outerfaceVertex;
	private E outerfaceEdge;
	
	public DoublyConnectedEdgeListEmbedding(DirectedGraph<V, E> graph,
			Transformer<E, E> nextEdgeSource, Transformer<E, E> nextEdgeDest,
			V outerfaceVertex, E outerfaceEdge) {
		super();
		this.graph = graph;
		this.nextEdgeSource = nextEdgeSource;
		this.nextEdgeDest = nextEdgeDest;
		this.outerfaceVertex = outerfaceVertex;
		this.outerfaceEdge = outerfaceEdge;
	}

	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorOnOuterface() {
		return getEmbeddingIterator(outerfaceVertex, outerfaceEdge);
	}
	
	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorAtEdge(E edge) {
		V vertex;
		vertex = graph.getSource(edge);
		return getEmbeddingIterator(vertex, edge);
	}
	
	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorAtVertex(V vertex) {
		E edge = graph.getIncidentEdges(vertex).iterator().next();
		return getEmbeddingIterator(vertex, edge);
	}
	
	@Override
	public EmbeddingIterator<V, E> getEmbeddingIterator(V vertex, E edge) {
		return new DECLEmbeddingIterator(vertex, edge);
	}
	
	private class DECLEmbeddingIterator implements EmbeddingIterator<V, E> {
		private V vertex;
		private E edge;
		
		public DECLEmbeddingIterator(V vertex, E edge) {
			super();
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
			if(graph.isSource(vertex, edge)) {
				edge = nextEdgeSource.transform(edge);
			} else {
				edge = nextEdgeDest.transform(edge);
			}
		}

		@Override
		public void nextAroundFace() {
			if(graph.isSource(vertex, edge)) {
				vertex = graph.getDest(edge);
				edge = nextEdgeDest.transform(edge);
			} else {
				vertex = graph.getSource(edge);
				edge = nextEdgeSource.transform(edge);
			}
		}

		@Override
		public void oppositeOnEdge() {
			vertex = graph.getOpposite(vertex, edge);
		}
	}
	
}
