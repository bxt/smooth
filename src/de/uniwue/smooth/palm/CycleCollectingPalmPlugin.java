package de.uniwue.smooth.palm;

import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class CycleCollectingPalmPlugin<V, E> extends NoopPalmPlugin<V, E> implements PalmPlugin<V, E> {
	
	private DirectedGraph<V, E> cycle;
	
	public DirectedGraph<V, E> getCycle() {
		return this.cycle;
	}
	
	@Override
	public void initialize(UndirectedGraph<V, E> graph, V s) {
		cycle = new DirectedSparseGraph<V, E>();
		Util.copyVertices(graph, cycle);
	}

	@Override
	public void notTraversedBackwards(V v, V w, E e) {
		cycle.addEdge(e, v, w);
	}

}
