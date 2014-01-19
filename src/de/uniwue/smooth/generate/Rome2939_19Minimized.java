package de.uniwue.smooth.generate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Factory;

import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class Rome2939_19Minimized <V, E> implements Factory<UndirectedGraph<V, E>> {
	
	private boolean randomize;
	private Factory<V> vertexFactory;
	private Factory<E> edgeFactory;
	
	public Rome2939_19Minimized(boolean randomize,
			Factory<V> vertexFactory, Factory<E> edgeFactory) {
		super();
		this.randomize = randomize;
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
	}

	@Override
	public UndirectedGraph<V, E> create() {
		List<V> vertices = Util.listFromFactory(vertexFactory, 19);
		if (randomize) Collections.shuffle(vertices);
		
		List<E> edges    = Util.listFromFactory(edgeFactory  , 28);
		if (randomize) Collections.shuffle(edges);
		
		UndirectedGraph<V, E> graph = new UndirectedSparseGraph<>();
		for (V v : vertices) graph.addVertex(v);
		
		graph.addEdge(edges.get( 0), vertices.get( 2), vertices.get( 0));
		graph.addEdge(edges.get( 1), vertices.get( 3), vertices.get( 0));
		graph.addEdge(edges.get( 2), vertices.get( 4), vertices.get( 0));
		graph.addEdge(edges.get( 3), vertices.get( 9), vertices.get( 2));
		graph.addEdge(edges.get( 4), vertices.get( 8), vertices.get( 5));
		graph.addEdge(edges.get( 5), vertices.get( 9), vertices.get( 5));
		graph.addEdge(edges.get( 6), vertices.get( 5), vertices.get(10));
		graph.addEdge(edges.get( 7), vertices.get(10), vertices.get( 6));
		graph.addEdge(edges.get( 8), vertices.get( 6), vertices.get(11));
		graph.addEdge(edges.get( 9), vertices.get( 7), vertices.get(13));
		graph.addEdge(edges.get(10), vertices.get(13), vertices.get( 5));
		graph.addEdge(edges.get(11), vertices.get(11), vertices.get( 2));
		graph.addEdge(edges.get(12), vertices.get( 1), vertices.get(14));
		graph.addEdge(edges.get(13), vertices.get(14), vertices.get( 0));
		graph.addEdge(edges.get(14), vertices.get(15), vertices.get( 5));
		graph.addEdge(edges.get(15), vertices.get( 6), vertices.get( 2));
		graph.addEdge(edges.get(16), vertices.get(16), vertices.get( 2));
		graph.addEdge(edges.get(17), vertices.get(16), vertices.get( 9));
		graph.addEdge(edges.get(18), vertices.get(16), vertices.get(13));
		graph.addEdge(edges.get(19), vertices.get(16), vertices.get( 5));
		graph.addEdge(edges.get(20), vertices.get(16), vertices.get(10));
		graph.addEdge(edges.get(21), vertices.get( 7), vertices.get( 2));
		graph.addEdge(edges.get(22), vertices.get(12), vertices.get(17));
		graph.addEdge(edges.get(23), vertices.get(17), vertices.get( 7));
		graph.addEdge(edges.get(24), vertices.get(18), vertices.get( 7));
		graph.addEdge(edges.get(25), vertices.get(18), vertices.get(15));
		graph.addEdge(edges.get(26), vertices.get(18), vertices.get( 1));
		graph.addEdge(edges.get(27), vertices.get(18), vertices.get(10));
		
		return graph;	
	}
	

}
