package de.uniwue.smooth.palm;

import edu.uci.ics.jung.graph.UndirectedGraph;

public class SequencingPalmPlugin<V, E> implements PalmPlugin<V, E> {
	
	PalmPlugin<V, E>[] plugins;

	@Override
	public void setLowVertexViaTree(V v, V w, E e) {
		for (PalmPlugin<V, E> plugin : plugins) {
			plugin.setLowVertexViaTree(v, w, e);
		}
	}

	@Override
	public void setLowVertexViaCycle(V v, V w, E e) {
		for (PalmPlugin<V, E> plugin : plugins) {
			plugin.setLowVertexViaCycle(v, w, e);
		}
	}

	@Override
	public void findBridgeVertex(V v, V w, E e) {
		for (PalmPlugin<V, E> plugin : plugins) {
			plugin.findBridgeVertex(v, w, e);
		}
	}

	@Override
	public void initialize(UndirectedGraph<V, E> graph, V s) {
		for (PalmPlugin<V, E> plugin : plugins) {
			plugin.initialize(graph, s);
		}
	}

	@Override
	public void teardown() {
		for (PalmPlugin<V, E> plugin : plugins) {
			plugin.teardown();
		}
	}

	@Override
	public void beforeTreeTraverse(V v, V w, E e) {
		for (PalmPlugin<V, E> plugin : plugins) {
			plugin.beforeTreeTraverse(v, w, e);
		}
	}

	@Override
	public void notTraversedBackwards(V v, V w, E e) {
		for (PalmPlugin<V, E> plugin : plugins) {
			plugin.notTraversedBackwards(v, w, e);
		}
	}
	
	
	
}
