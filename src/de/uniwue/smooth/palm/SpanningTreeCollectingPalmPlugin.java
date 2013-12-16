package de.uniwue.smooth.palm;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class SpanningTreeCollectingPalmPlugin<V, E> extends NoopPalmPlugin<V, E> implements PalmPlugin<V, E> {
	
	private Tree<V, E> spanningTree;
	
	public Tree<V,E> getSpanningTree() {
		return this.spanningTree;
	}
	
	public void initialize(UndirectedGraph<V, E> graph, V s) {
		spanningTree = new DelegateTree<V, E>();
		spanningTree.addVertex(s);
	}

	@Override
	public void beforeTreeTraverse(V v, V w, E e) {
		spanningTree.addEdge(e, v, w);
	}

}
