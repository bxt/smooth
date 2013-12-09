package de.uniwue.smooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class StOrdering<V,E> {
	
	private Set<V> oldVertices = new HashSet<>();
	private Set<E> oldEdges = new HashSet<>();
	
	private UndirectedGraph<V, E> graph;
	private PalmTree<V, E> palmTree;
	private V s;
	private V t;
	
	private List<V> vertexList;
	
	public StOrdering(UndirectedGraph<V, E> graph, V s, V t) {
		this.graph = graph;
		this.palmTree = new PalmTree<V, E>(graph, s, t);
		this.s = s;
		this.t = t;
		this.vertexList = buildList();
	}
	
	private List<V> findPath(V start) {
		List<V> path = null;
		if (path == null) path = findSingeStepBackPath(start);
		if (path == null) path = findClimbingTreePath(start);
		if (path == null) path = findJumpOnTreePath(start);
		return path;
	}
	
	private List<V> findSingeStepBackPath(V start) {
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
	
	private List<V> findClimbingTreePath(V start) {
		Collection<E> treeEdges = palmTree.getSpanningTree().getOutEdges(start);
		for(E e : treeEdges) {
			if(!oldEdges.contains(e)) {
				oldEdges.add(e);
				V next = palmTree.getSpanningTree().getDest(e);
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
					e = palmTree.getSpanningTree().getParentEdge(next);
					oldEdges.add(e);
					oldVertices.add(next);
					next = palmTree.getSpanningTree().getParent(next);
					path.add(next);
				}
				return path;
			}
		}
		return null;
	}
	
	private List<V> buildList() {
		oldEdges.add(graph.findEdge(s, t));
		oldVertices.add(s);
		oldVertices.add(t);
		Deque<V> stack = new LinkedList<>();
		List<V> done = new ArrayList<>();
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
				done.add(v);
			}
		}
		return done;
	}

	public List<V> getList() {
		return this.vertexList;
	}
	
	public Map<V, Integer> asNumbers() {
		Map<V, Integer> numbers = new HashMap<V, Integer>();
		for (int i = 0; i < getList().size(); i++) {
			numbers.put(getList().get(i), i);
		}
		return numbers;
	}
	
	public DirectedGraph<V, E> getDirectedAcyclicGraph() {
		throw new NotImplementedException();
		// TODO
	}

}
