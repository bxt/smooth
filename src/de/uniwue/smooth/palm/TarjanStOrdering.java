package de.uniwue.smooth.palm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * Builds the st-Ordering for a graph based on Tarjan's algorithm.
 * [S. Even, R. E. Tarjan: "Computing an st-numbering"; Theoretical Computer Science, 2(3):339–344, 1976.]
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class TarjanStOrdering<V, E> extends AbstractStOrdering<V, E> implements StOrdering<V, E> {
	
	/**
	 * Vertices that are done and won't be visited anymore.
	 */
	private Set<V> oldVertices = new HashSet<>();
	/**
	 * Edges that are done and won't be traversed anymore.
	 */
	private Set<E> oldEdges = new HashSet<>();
	
	private UndirectedGraph<V, E> graph;
	private PalmTree<V, E> palmTree;
	private V s;
	private V t;
	
	/**
	 * The vertices end up ordered in this list.
	 */
	private List<V> vertexList = new ArrayList<>();
	
	/**
	 * Calculate an st-Ordering
	 * @param graph Graph whose vertices are ordered.
	 * @param s Will be the first vertex.
	 * @param t Will be the last vertex.
	 */
	public TarjanStOrdering(UndirectedGraph<V, E> graph, V s, V t) {
		this.graph = graph;
		this.palmTree = new PalmTree<V, E>(graph, s, t);
		this.s = s;
		this.t = t;
		initialize(); // Do the actual calculation
	}
	
	/**
	 * Pop a vertex from the stack. Then repeatedly
	 * find paths from new edges between the current
	 * vertex and an old vertex. Add the path vertices
	 * to the stack in reverse order. If the edges are
	 * exhausted, put the vertex in the list and pop the
	 * next vertex from the stack.
	 */
	private void initialize() {
		oldEdges.add(graph.findEdge(s, t));
		oldVertices.add(s);
		oldVertices.add(t);
		Deque<V> stack = new LinkedList<>();
		stack.push(t);
		stack.push(s);
		while(!stack.isEmpty()) {
			V v = stack.pop();
			List<V> path = findPath(v);
			if(path != null) {
				for(int i = path.size()-2; i >= 0; i--) {
					stack.push(path.get(i));
				}
			} else {
				vertexList.add(v);
			}
		}
	}
	
	/**
	 * Find a path between the current vertex and another
	 * old vertex consisting only of new edges.
	 *  
	 * @param start The current vertex to start the path from.
	 * @return A path from the given vertex to another old vertex, or <tt>null</tt> if the possibilities are exhausted.
	 */
	private List<V> findPath(V start) {
		List<V> path = null;
		if (path == null) path = findSingleStepBackPath(start);
		if (path == null) path = findClimbingTreePath(start);
		if (path == null) path = findJumpOnTreePath(start);
		return path;
	}
	
	/**
	 * If there is a new (non-old) outgoing cycle edge, it
	 * is our path. 
	 * 
	 * This doesn't change our stack a bit and is basically only
	 * marking edges as old.
	 * 
	 * @param start The current vertex to start the path from.
	 * @return A path consisting of the current vertex and the endpoint of the non-old back edge, or <tt>null</tt> if there is none.
	 */
	private List<V> findSingleStepBackPath(V start) {
		Collection<E> cycleOutEdges = palmTree.getCycle().getOutEdges(start);
		for(E e : cycleOutEdges) {
			if(!oldEdges.contains(e)) {
				oldEdges.add(e);
				V end = palmTree.getCycle().getDest(e);
				return Arrays.asList(start, end);
			}
		}
		return null;
		
	}
	
	/**
	 * If there is a new tree edge exiting our current vertex, we climb the
	 * tree and follow the route to the lowpointer of the vertex, until
	 * we hit an old vertex. The route is returned as a path.
	 * 
	 * @param start The current vertex to start the path from.
	 * @return A path consisting of the current vertex and some tree edges, or <tt>null</tt> if there is none.
	 */
	private List<V> findClimbingTreePath(V start) {
		Collection<E> treeEdges = palmTree.getSpanningTrees().getOutEdges(start);
		for(E e : treeEdges) {
			if(!oldEdges.contains(e)) {
				oldEdges.add(e);
				V next = palmTree.getSpanningTrees().getDest(e);
				List<V> path = new ArrayList<>();
				path.add(start);
				path.add(next);
				while(!oldVertices.contains(next)) {
					e = palmTree.getBackwardEdge(next);
					oldEdges.add(e);
					oldVertices.add(next);
					next = palmTree.getBackwardVertex(next);
					path.add(next);
				}
				return path;
			}
		}
		return null;
	}
	
	/**
	 * If there is an incoming back edge, use it to jump on the tree and traverse the
	 * tree backwards until we hit something old again.
	 * 
	 * @param start The current vertex to start the path from.
	 * @return A path consisting of the current vertex and a back edge, plus some tree edges, or <tt>null</tt> if there is none.
	 */
	private List<V> findJumpOnTreePath(V start) {
		Collection<E> cycleInEdges = palmTree.getCycle().getInEdges(start);
		for(E e : cycleInEdges) {
			if(!oldEdges.contains(e)) {
				oldEdges.add(e);
				V next = palmTree.getCycle().getSource(e);
				List<V> path = new ArrayList<>();
				path.add(start);
				path.add(next);
				while(!oldVertices.contains(next)) {
					e = palmTree.getSpanningTrees().getParentEdge(next);
					oldEdges.add(e);
					oldVertices.add(next);
					next = palmTree.getSpanningTrees().getParent(next);
					path.add(next);
				}
				return path;
			}
		}
		return null;
	}
	
	@Override
	public List<V> getList() {
		return vertexList;
	}
}
