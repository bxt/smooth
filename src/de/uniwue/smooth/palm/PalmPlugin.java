package de.uniwue.smooth.palm;

import edu.uci.ics.jung.graph.UndirectedGraph;


public interface PalmPlugin<V,E> {
	
	public void initialize(UndirectedGraph<V, E> graph, V s);
	
	public void teardown();
	
	public void beforeTreeTraverse(V v, V w, E e);
	
	public void notTraversedBackwards(V v, V w, E e);
	
	public void setLowVertexViaTree(V v, V w, E e);
	
	public void setLowVertexViaCycle(V v, V w, E e);
	
	public void findBridgeVertex(V v, V w, E e);

}
