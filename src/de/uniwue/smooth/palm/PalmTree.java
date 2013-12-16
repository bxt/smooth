package de.uniwue.smooth.palm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import edu.uci.ics.jung.graph.UndirectedGraph;

public class PalmTree<V,E> {
	
	private PalmPlugin<V, E> plugin = new NoopPalmPlugin<V, E>();
	
	private Map<V, Integer> discoveryTimes;
	private UndirectedGraph<V, E> graph;
	private int nextDiscoveryTime;
	private Map<V, V> lowerVertices;
	
	V preselectedW = null;
	
	
	public void process(UndirectedGraph<V, E> graph) {
		V s = graph.getVertices().iterator().next();
		process(graph, s);
	}
	
	public void process(UndirectedGraph<V, E> graph, V s) {
		initialize(graph, s);
		depthFirstTraverse(s, null);
		teardown();
	}
	
	private void initialize(UndirectedGraph<V, E> graph, V s) {
		discoveryTimes = new HashMap<V, Integer>();
		nextDiscoveryTime = 0;
		lowerVertices = new HashMap<V, V>();
		this.graph = graph;
		plugin.initialize(graph, s);
	}
	
	private void teardown() {
		this.graph = null;
		this.discoveryTimes = null;
		this.lowerVertices = null;
		plugin.teardown();
	}
	
	private void depthFirstTraverse(V v, V parent) {
		int discoveryTime = nextDiscoveryTime;
		nextDiscoveryTime++;
		discoveryTimes.put(v, discoveryTime);
		lowerVertices.put(v, v);
		for(V w : getNeighbors(v)) {
			Integer wDiscoveryTime = discoveryTimes.get(w);
			E edge = graph.findEdge(v, w);
			if(wDiscoveryTime == null) {
				plugin.beforeTreeTraverse(v, w, edge);
				depthFirstTraverse(w, v);
				updateLowerVertexTree(v, w, edge);
			} else {
				if (wDiscoveryTime < discoveryTime && parent != w) {
					plugin.notTraversedBackwards(v, w, edge);
					updateLowerVertexCycle(v, w, edge);
				}
			}
		}
	}
	
	private Collection<V> getNeighbors(V v) {
		Collection<V> neighbors = graph.getNeighbors(v);
		if (preselectedW != null) {
			LinkedList<V> sortedNeighbors = new LinkedList<>(neighbors);
			Iterator<V> it = sortedNeighbors.iterator();
			boolean found = false;
			while (it.hasNext() && !found) {
				V w = it.next();
				if(w == preselectedW) {
					it.remove();
					found = true;
				}
			}
			if(!found) throw new IllegalArgumentException("The edge s-t must be part of the graph!");
			sortedNeighbors.addFirst(preselectedW);
			neighbors = sortedNeighbors;
			preselectedW = null;
		}
		return neighbors;
	}
	
	private void updateLowerVertexTree(V v, V w, E e) {
		V wLowVertex = lowerVertices.get(w);
		int vLowDiscoveryTime = discoveryTimes.get(lowerVertices.get(v));
		int wLowDiscoveryTime = discoveryTimes.get(wLowVertex);
		if(wLowDiscoveryTime < vLowDiscoveryTime) {
			lowerVertices.put(v, wLowVertex);
			plugin.setLowVertexViaTree(v, w, e);
			
		} else {
			plugin.findBridgeVertex(v, w, e);
		}
	}

	private void updateLowerVertexCycle(V v, V w, E e) {
		int vLowDiscoveryTime = discoveryTimes.get(lowerVertices.get(v));
		if(discoveryTimes.get(w) < vLowDiscoveryTime) {
			lowerVertices.put(v, w);
			plugin.setLowVertexViaCycle(v, w, e);
		} else {
			throw new IllegalStateException("Found strange cycle edge!");
		}
	}

}
