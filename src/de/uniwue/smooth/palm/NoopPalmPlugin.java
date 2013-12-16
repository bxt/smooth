package de.uniwue.smooth.palm;

import edu.uci.ics.jung.graph.UndirectedGraph;


public class NoopPalmPlugin<V,E> implements PalmPlugin<V, E> {
	
	@Override
	public void setLowVertexViaTree(V v, V w, E e) {}
	
	@Override
	public void setLowVertexViaCycle(V v, V w, E e) {}
	
	@Override
	public void findBridgeVertex(V v, V w, E e) {}

	@Override
	public void initialize(UndirectedGraph<V, E> graph, V s) {}

	@Override
	public void teardown() {}

	@Override
	public void beforeTreeTraverse(V v, V w, E e) {}

	@Override
	public void notTraversedBackwards(V v, V w, E e) {}

}
