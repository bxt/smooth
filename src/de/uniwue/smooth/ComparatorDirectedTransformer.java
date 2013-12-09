package de.uniwue.smooth;

import java.util.Comparator;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class ComparatorDirectedTransformer<V, E> implements Transformer<Graph<? extends V, E>, DirectedGraph<? super V,? super E>> {

	private Factory<DirectedGraph<? super V, ? super E>> graph_factory;
	private Comparator<? super V> vertexComparator;

	public ComparatorDirectedTransformer(Factory<DirectedGraph<? super V, ? super E>> graph_factory,
			Comparator<? super V> vertexComparator) {
		super();
		this.graph_factory = graph_factory;
		this.vertexComparator = vertexComparator;
	}
	
	@Override
	public DirectedGraph<? super V,? super E> transform(Graph<? extends V, E> graph) {
		DirectedGraph<? super V, ? super E> out = graph_factory.create();
	    
	    Util.copyVertices(graph, out);
	    
	    for (E e : graph.getEdges())
	    {
	        Pair<V> endpoints = sort(graph.getEndpoints(e));
            V v1 = endpoints.getFirst();
            V v2 = endpoints.getSecond();
            out.addEdge(e, v1, v2, EdgeType.DIRECTED);
	    }
	    return out;
	}
	
	private Pair<V> sort(Pair<? extends V> pair) {
        V v1 = pair.getFirst();
        V v2 = pair.getSecond();
		Pair<V> sortedPair;
		if(vertexComparator.compare(v1, v2) < 0) {
			sortedPair = new Pair<V>(v2, v1);
		} else {
			sortedPair = new Pair<V>(v1, v2);
		}
		return sortedPair;
	}
}
