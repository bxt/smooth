package de.uniwue.smooth.generate;

import java.util.List;

import org.apache.commons.collections15.Factory;

import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * Generate a simple planar graph.
 * 
 * The graph looks like this with vertex and edge numbers:
 * <pre>
 * (0)--0--(1)
 *  | \     |
 *  |  \    |
 *  3   4   1
 *  |    \  |
 *  |     \ |
 * (3)--2--(2) 
 * </pre>
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class SimplePlanarGraphGenerator<V, E> implements Factory<UndirectedGraph<V, E>> {

	private Factory<V> vertexFactory;
	private Factory<E> edgeFactory;
	
	public SimplePlanarGraphGenerator(Factory<V> vertexFactory,
			Factory<E> edgeFactory) {
		super();
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
	}

	@Override
	public UndirectedGraph<V, E> create() {
		List<V> vertices = Util.listFromFactory(vertexFactory, 4);
		List<E> edges    = Util.listFromFactory(edgeFactory  , 5);
		UndirectedGraph<V, E> graph = new UndirectedSparseGraph<>();
		for (V v : vertices) graph.addVertex(v);
		
		graph.addEdge(edges.get(0), vertices.get(0), vertices.get(1));
		graph.addEdge(edges.get(1), vertices.get(1), vertices.get(2));
		graph.addEdge(edges.get(2), vertices.get(2), vertices.get(3));
		graph.addEdge(edges.get(3), vertices.get(3), vertices.get(0));
		graph.addEdge(edges.get(4), vertices.get(0), vertices.get(2));
		
		return graph;
	}

}
