package de.uniwue.smooth.util;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class UndirectedTransformer<V,E> implements Transformer<Graph<V,E>, UndirectedGraph<V,E>> {
	
	private Factory<UndirectedGraph<V, E>> graph_factory;

	public UndirectedTransformer(Factory<UndirectedGraph<V, E>> graph_factory) {
		super();
		this.graph_factory = graph_factory;
	}
	
	@Override
	public UndirectedGraph<V, E> transform(Graph<V, E> graph) {
		UndirectedGraph<V,E> out = graph_factory.create();
	    
	    Util.copyVertices(graph, out);
	    
	    for (E e : graph.getEdges())
	    {
	        Pair<V> endpoints = graph.getEndpoints(e);
            V v1 = endpoints.getFirst();
            V v2 = endpoints.getSecond();
            out.addEdge(e, v1, v2, EdgeType.UNDIRECTED);
	    }
	    return out;
	}
}
