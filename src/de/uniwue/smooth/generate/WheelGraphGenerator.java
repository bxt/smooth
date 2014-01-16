package de.uniwue.smooth.generate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Factory;

import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class WheelGraphGenerator<V, E> implements Factory<UndirectedGraph<V, E>> {
	
	private boolean randomize;
	private int size;
	private Factory<V> vertexFactory;
	private Factory<E> edgeFactory;
	
	public WheelGraphGenerator(boolean randomize, int size,
			Factory<V> vertexFactory, Factory<E> edgeFactory) {
		super();
		this.randomize = randomize;
		this.size = size;
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
	}

	@Override
	public UndirectedGraph<V, E> create() {
		List<V> vertices = Util.listFromFactory(vertexFactory, size + 1);
		if (randomize) Collections.shuffle(vertices);
		
		List<E> edges    = Util.listFromFactory(edgeFactory  , size * 2);
		if (randomize) Collections.shuffle(edges);
		
		UndirectedGraph<V, E> graph = new UndirectedSparseGraph<>();
		for (V v : vertices) graph.addVertex(v);
		
		for (int i = 0; i < size; i++) {
			graph.addEdge(edges.get(i*2    ), vertices.get(i + 1), vertices.get(((i+1)%size) + 1));
			graph.addEdge(edges.get(i*2 + 1), vertices.get(i + 1), vertices.get(               0));
		}
		
		return graph;
		
	}
	
}
