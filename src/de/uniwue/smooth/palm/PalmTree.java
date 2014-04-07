package de.uniwue.smooth.palm;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.ImmutableTuple;
import de.uniwue.smooth.util.tuples.Tuple;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * Partitions a graph into a spanning tree (forest if not connected) and
 * back edges. Used as a basis for several algorithms on (planar) graphs.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class PalmTree<V,E> {
	
	/**
	 * A sequence of numbers is assinged to the vertices for the time they are first encountered by the DFS.
	 */
	private Map<V, Integer> discoveryTimes = new HashMap<V, Integer>();
	/**
	 * The graph to split.
	 */
	private UndirectedGraph<V, E> graph;
	/**
	 * The discovery time for the next vertex. Increased while the DFS is running.
	 */
	private int nextDiscoveryTime = 0;
	/**
	 * Vertex with the lowest discovery time reachable from a vertex using directed edges.
	 */
	private Map<V, V> lowerVertices = new HashMap<V, V>();
	
	/**
	 * Height (path length from root) in the resulting spanning forest.
	 */
	private Map<V, Integer> heights = new HashMap<V, Integer>();
	/**
	 * Edge and vertex to go to for reaching the vertex with the lowest discovery time
	 */
	private Map<V, Tuple<V, E>> backwards = new HashMap<V, Tuple<V, E>>();
	/**
	 * Edges not in the spanning forest.
	 */
	private DirectedGraph<V, E> cycle = new DirectedSparseGraph<V, E>();
	/**
	 * The spanning forest.
	 */
	private Forest<V, E> spanningTrees = new DelegateForest<V, E>();
	/**
	 * Optionally a fixed start vertex.
	 */
	V preselectedV = null;
	/**
	 * Optionally a fixed second vertex.
	 */
	V preselectedW = null;
	
	
	/**
	 * Build a palm tree from a graph.
	 * 
	 * @param graph Graph to work on.
	 * @param s Custom start node.
	 * @param t Custom second node.
	 */
	public PalmTree(UndirectedGraph<V, E> graph, V s, V t) {
		preselectedV = s;
		preselectedW = t;
		process(graph);
	}
	
	/**
	 * Build a palm tree from a graph.
	 * 
	 * @param graph Graph to work on.
	 * @param s Custom start node.
	 */
	public PalmTree(UndirectedGraph<V, E> graph, V s) {
		preselectedV = s;
		process(graph);
	}
	
	/**
	 * Build a palm tree from a graph. Start and end nodes are chosen arbitrarily. 
	 * 
	 * @param graph Graph to work on.
	 */
	public PalmTree(UndirectedGraph<V, E> graph) {
		process(graph);
	}
	
	
	/**
	 * Access the spanning forest created through DFS.
	 * @return The spanning forest.
	 */
	public Forest<V,E> getSpanningTrees() {
		return this.spanningTrees;
	}
	
	/**
	 * Access the graph of edges not in the spanning forest.
	 * @return
	 */
	public DirectedGraph<V, E> getCycle() {
		return this.cycle;
	}
	
	/**
	 * Next edge to go over for reaching the vertex with the lowest discovery time.
	 * @param v Vertex to start from.
	 * @return Next edge on the path to the lowest vertex.
	 */
	public E getBackwardEdge(V v) {
		return this.backwards.get(v).getSecond();
	}
	
	/**
	 * Next vertex to go to for reaching the vertex with the lowest discovery time.
	 * @param v Vertex to start from.
	 * @return Next vertex on the path to the lowest vertex.
	 */
	public V getBackwardVertex(V v) {
		return this.backwards.get(v).getFirst();
	}
	
	/**
	 * Vertex with the lowest discovery time reachable from a vertex using directed edges.
	 * @param v Vertex to start from.
	 * @return Lowest vertex to reach from <tt>v</tt> over a directed path.
	 */
	public V getLowerVertex(V v) {
		return lowerVertices.get(v);
	}
	
	
	/**
	 * Calculates the palm tree.
	 * @param graph The input graph to work on.
	 */
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
	
	/**
	 * Get the vertices in the order they should be processed.
	 * @return Ordered list of vertices when there are ordering constraints or an arbitrary vertex collection otherwise.
	 */
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
			backwards.put(v, new ImmutableTuple<V, E>(w, e));
		}
	}

	private void updateLowerVertexCycle(E e, V v, V w) {
		int vLowDiscoveryTime = discoveryTimes.get(lowerVertices.get(v));
		if(discoveryTimes.get(w) < vLowDiscoveryTime) {
			lowerVertices.put(v, w);
			backwards.put(v, new ImmutableTuple<V, E>(w, e));
		} else {
			// v is probably a separation point for the biconnected block
		}
	}

}
