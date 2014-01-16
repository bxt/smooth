package de.uniwue.smooth.generate;

import java.util.List;

import org.apache.commons.collections15.Factory;

import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class OctahedronGraphGenerator<V, E> implements Factory<UndirectedGraph<V, E>> {

	private Factory<V> vertexFactory;
	private Factory<E> edgeFactory;
	
	public OctahedronGraphGenerator(Factory<V> vertexFactory,
			Factory<E> edgeFactory) {
		super();
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
	}

	@Override
	public UndirectedGraph<V, E> create() {
		List<V> vertices = Util.listFromFactory(vertexFactory, 6);
		List<E> edges    = Util.listFromFactory(edgeFactory  ,12);
		UndirectedGraph<V, E> graph = new UndirectedSparseGraph<>();
		for (V v : vertices) graph.addVertex(v);
		
		graph.addEdge(edges.get( 0), vertices.get(0), vertices.get(1));
		graph.addEdge(edges.get( 1), vertices.get(0), vertices.get(2));
		graph.addEdge(edges.get( 2), vertices.get(0), vertices.get(3));
		graph.addEdge(edges.get( 3), vertices.get(0), vertices.get(5));
		graph.addEdge(edges.get( 4), vertices.get(1), vertices.get(2));
		graph.addEdge(edges.get( 5), vertices.get(1), vertices.get(4));
		graph.addEdge(edges.get( 6), vertices.get(1), vertices.get(5));
		graph.addEdge(edges.get( 7), vertices.get(2), vertices.get(3));
		graph.addEdge(edges.get( 8), vertices.get(2), vertices.get(4));
		graph.addEdge(edges.get( 9), vertices.get(3), vertices.get(4));
		graph.addEdge(edges.get(10), vertices.get(3), vertices.get(5));
		graph.addEdge(edges.get(11), vertices.get(4), vertices.get(5));
		
		return graph;
	}

}
