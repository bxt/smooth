package de.uniwue.smooth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import de.uniwue.smooth.util.Tuple;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class PalmTree<V,E> {

	private Map<V, Integer> discoveryTimes = new HashMap<V, Integer>();
	private UndirectedGraph<V, E> graph;
	private int nextDiscoveryTime = 0;
	private Map<V, V> lowerVertices = new HashMap<V, V>();
	
	private Map<V, Tuple<V, E>> backwards = new HashMap<V, Tuple<V, E>>();
	private DirectedGraph<V, E> cycle = new DirectedSparseGraph<V, E>();
	private Tree<V, E> spanningTree = new DelegateTree<V, E>();
	V preselectedW = null;
	
	public PalmTree(UndirectedGraph<V, E> graph, V s, V t) {
		preselectedW = t;
		process(graph, s);
	}
	
	public PalmTree(UndirectedGraph<V, E> graph, V s) {
		process(graph, s);
	}
	
	public PalmTree(UndirectedGraph<V, E> graph) {
		V s = graph.getVertices().iterator().next();
		process(graph, s);
	}
	
	public Tree<V,E> getSpanningTree() {
		return this.spanningTree;
	}
	
	public DirectedGraph<V, E> getCycle() {
		return this.cycle;
	}
	
	public E getBackwardEdge(V v) {
		return this.backwards.get(v).getSecond();
	}
	
	public V getBackwardVertex(V v) {
		return this.backwards.get(v).getFirst();
	}
	
	public V getLowerVertex(V v) {
		return lowerVertices.get(v);
	}
	
	private void process(UndirectedGraph<V, E> graph, V s) {
		initialize(graph, s);
		depthFirstTraverse(s);
		teardown();
	}
	
	private void initialize(UndirectedGraph<V, E> graph, V s) {
		this.graph = graph;
		Util.copyVertices(graph, cycle);
		spanningTree.addVertex(s);
	}
	
	private void teardown() {
		this.graph = null;
		this.discoveryTimes = null;
		this.lowerVertices = null;
	}
	
	private void depthFirstTraverse(V v) {
		int discoveryTime = nextDiscoveryTime;
		nextDiscoveryTime++;
		discoveryTimes.put(v, discoveryTime);
		lowerVertices.put(v, v);
		for(V w : getNeighbors(v)) {
			Integer wDiscoveryTime = discoveryTimes.get(w);
			E edge = graph.findEdge(v, w);
			if(wDiscoveryTime == null) {
				spanningTree.addEdge(edge, v, w);
				depthFirstTraverse(w);
				updateLowerVertexTree(edge, v, w);
			} else {
				if (wDiscoveryTime < discoveryTime && spanningTree.getParent(v) != w) {
					cycle.addEdge(edge, v, w);
					updateLowerVertexCycle(edge, v, w);
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
			while (it.hasNext()) {
				V w = it.next();
				if(w == preselectedW) {
					it.remove();
					found = true;
				}
			}
			if(!found) throw new IllegalArgumentException("The edge s-t must be part of the graph!");
			preselectedW = null;
			sortedNeighbors.addFirst(v);
		}
		return neighbors;
	}
	
	private void updateLowerVertexTree(E e, V v, V w) {
		V wLowVertex = lowerVertices.get(w);
		int vLowDiscoveryTime = discoveryTimes.get(lowerVertices.get(v));
		int wLowDiscoveryTime = discoveryTimes.get(wLowVertex);
		if(wLowDiscoveryTime < vLowDiscoveryTime) {
			lowerVertices.put(v, wLowVertex);
			backwards.put(v, new Tuple<V, E>(w, e));
		}
	}

	private void updateLowerVertexCycle(E e, V v, V w) {
		int vLowDiscoveryTime = discoveryTimes.get(lowerVertices.get(v));
		if(discoveryTimes.get(w) < vLowDiscoveryTime) {
			lowerVertices.put(v, w);
			backwards.put(v, new Tuple<V, E>(w, e));
		} else {
			// v is a separation point for the biconnected block
		}
	}

}
