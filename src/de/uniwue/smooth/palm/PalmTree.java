package de.uniwue.smooth.palm;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.uniwue.smooth.util.Tuple;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class PalmTree<V,E> {

	private Map<V, Integer> discoveryTimes = new HashMap<V, Integer>();
	private UndirectedGraph<V, E> graph;
	private int nextDiscoveryTime = 0;
	private Map<V, V> lowerVertices = new HashMap<V, V>();
	
	private Map<V, Integer> heights = new HashMap<V, Integer>();
	private Map<V, Tuple<V, E>> backwards = new HashMap<V, Tuple<V, E>>();
	private DirectedGraph<V, E> cycle = new DirectedSparseGraph<V, E>();
	private Forest<V, E> spanningTrees = new DelegateForest<V, E>();
	V preselectedV = null;
	V preselectedW = null;
	
	
	/**
	 * 
	 * 
	 * @param graph
	 * @param s
	 * @param t
	 */
	public PalmTree(UndirectedGraph<V, E> graph, V s, V t) {
		preselectedV = s;
		preselectedW = t;
		process(graph);
	}
	
	public PalmTree(UndirectedGraph<V, E> graph, V s) {
		preselectedV = s;
		process(graph);
	}
	
	public PalmTree(UndirectedGraph<V, E> graph) {
		process(graph);
	}
	
	
	
	public Forest<V,E> getSpanningTrees() {
		return this.spanningTrees;
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
	
	
	
	private void process(UndirectedGraph<V, E> graph) {
		initialize(graph);
		for(V v : getVertices()) {
			if(!spanningTrees.containsVertex(v)) {
				heights.put(v, 0);
				spanningTrees.addVertex(v);
				depthFirstTraverse(v);
			}
		}
		teardown();
	}
	
	private void initialize(UndirectedGraph<V, E> graph) {
		this.graph = graph;
		Util.copyVertices(graph, cycle);
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
				spanningTrees.addEdge(edge, v, w);
				heights.put(w, heights.get(v) + 1);
				depthFirstTraverse(w);
				updateLowerVertexTree(edge, v, w);
			} else {
				if (wDiscoveryTime < discoveryTime && spanningTrees.getParent(v) != w) {
					cycle.addEdge(edge, v, w);
					updateLowerVertexCycle(edge, v, w);
				}
			}
		}
	}
	
	private Collection<V> getNeighbors(V v) {
		Collection<V> neighbors = graph.getNeighbors(v);
		if (preselectedW == null) {
			return neighbors;
		} else {
			LinkedList<V> sortedNeighbors = new LinkedList<>(neighbors);
			if (!moveToFrontOfList(sortedNeighbors, preselectedW)) {
				throw new IllegalArgumentException("The edge s-t must be part of the graph!");
			}
			preselectedW = null;
			return sortedNeighbors;
		}
	}
	
	private Collection<V> getVertices() {
		Collection<V> vertices = graph.getVertices();
		if (preselectedV == null) {
			return vertices;
		} else {
			LinkedList<V> sortedVertices = new LinkedList<>(vertices);
			if (!moveToFrontOfList(sortedVertices, preselectedV)) {
				throw new IllegalArgumentException("The vertex s must be part of the graph!");
			}
			preselectedV = null;
			return sortedVertices;
		}
	}
	
	private <X> boolean moveToFrontOfList(LinkedList<X> list, X element) {
		boolean changed = list.remove(element);
		list.addFirst(element);
		return changed;
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
