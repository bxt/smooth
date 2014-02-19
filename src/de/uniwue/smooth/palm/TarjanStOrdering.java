package de.uniwue.smooth.palm;

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

import edu.uci.ics.jung.graph.UndirectedGraph;

public class TarjanStOrdering<V, E> implements StOrdering<V, E> {
	
	private Set<V> oldVertices = new HashSet<>();
	private Set<E> oldEdges = new HashSet<>();
	
	private UndirectedGraph<V, E> graph;
	private PalmTree<V, E> palmTree;
	private V s;
	private V t;
	
	private List<V> vertexList = new ArrayList<>();
	private Map<V, Integer> numbers = new HashMap<V, Integer>();
	
	public TarjanStOrdering(UndirectedGraph<V, E> graph, V s, V t) {
		this.graph = graph;
		this.palmTree = new PalmTree<V, E>(graph, s, t);
		this.s = s;
		this.t = t;
		initialize();
	}
	
	private List<V> findPath(V start) {
		List<V> path = null;
		if (path == null) path = findSingleStepBackPath(start);
		if (path == null) path = findClimbingTreePath(start);
		if (path == null) path = findJumpOnTreePath(start);
		return path;
	}
	
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
		for (int i = 0; i < getList().size(); i++) {
			numbers.put(getList().get(i), i);
		}
	}

	@Override
	public List<V> getList() {
		return vertexList;
	}
	
	@Override
	public Map<V, Integer> asNumbers() {
		return numbers;
	}
	
	@Override
	public int compare(V o1, V o2) {
		return numbers.get(o1).compareTo(numbers.get(o2));
	}
}
