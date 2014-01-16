package de.uniwue.smooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.ComparatorUtils;

import de.uniwue.smooth.palm.PalmTree;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class StOrdering<V,E> implements Comparator<V> {
	
	private Set<V> oldVertices = new HashSet<>();
	private Set<E> oldEdges = new HashSet<>();
	
	private UndirectedGraph<V, E> graph;
	private PalmTree<V, E> palmTree;
	private V s;
	private V t;
	
	private List<V> vertexList = new ArrayList<>();
	private Map<V, Integer> numbers = new HashMap<V, Integer>();
	
	public StOrdering(UndirectedGraph<V, E> graph, V s, V t) {
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

	public List<V> getList() {
		return vertexList;
	}
	
	public Map<V, Integer> asNumbers() {
		return numbers;
	}
	
	public DirectedGraph<V, E> getDirectedAcyclicGraph() {
		return new StOrderingDirectedAcyclicGraph();
	}
	
	private class StOrderingDirectedAcyclicGraph extends AbstractGraph<V, E> implements DirectedGraph<V, E> {
		private static final long serialVersionUID = 1L;

		@Override
		public V getSource(E directed_edge) {
			Pair<V> endpoints = graph.getEndpoints(directed_edge);
			return ComparatorUtils.min(endpoints.getFirst(), endpoints.getSecond(), StOrdering.this);
		}

		@Override
		public V getDest(E directed_edge) {
			Pair<V> endpoints = graph.getEndpoints(directed_edge);
			return ComparatorUtils.max(endpoints.getFirst(), endpoints.getSecond(), StOrdering.this);
		}

		@Override
		public boolean addEdge(E e, V v1, V v2) {
			throw new UnsupportedOperationException("Immutable. ");
		}

		@Override
		public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
			throw new UnsupportedOperationException("Immutable. ");
		}

		@Override
		public Pair<V> getEndpoints(E edge) {
			return graph.getEndpoints(edge);
		}

		@Override
		public V getOpposite(V vertex, E edge) {
			return graph.getOpposite(vertex, edge);
		}

		@Override
		public Collection<E> getEdges() {
			return graph.getEdges();
		}

		@Override
		public Collection<V> getVertices() {
			return graph.getVertices();
		}

		@Override
		public boolean containsVertex(V vertex) {
			return graph.containsVertex(vertex);
		}

		@Override
		public boolean containsEdge(E edge) {
			return graph.containsEdge(edge);
		}

		@Override
		public int getEdgeCount() {
			return graph.getEdgeCount();
		}

		@Override
		public int getVertexCount() {
			return graph.getVertexCount();
		}

		@Override
		public Collection<V> getNeighbors(V vertex) {
			return graph.getNeighbors(vertex);
		}

		@Override
		public Collection<E> getIncidentEdges(V vertex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<V> getIncidentVertices(E edge) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public E findEdge(V v1, V v2) {
			E candidate = graph.findEdge(v1, v2);
			if (candidate != null && compare(v1, v2) < 0) return candidate;
			return null;
		}

		@Override
		public Collection<E> findEdgeSet(V v1, V v2) {
			throw new UnsupportedOperationException("Currently not implemented. ");
		}

		@Override
		public boolean addVertex(V vertex) {
			throw new UnsupportedOperationException("Immutable. ");
		}

		@Override
		public boolean addEdge(E edge, Collection<? extends V> vertices) {
			throw new UnsupportedOperationException("Immutable. ");
		}

		@Override
		public boolean addEdge(E edge, Collection<? extends V> vertices,
				EdgeType edge_type) {
			throw new UnsupportedOperationException("Immutable. ");
		}

		@Override
		public boolean removeVertex(V vertex) {
			throw new UnsupportedOperationException("Immutable. ");
		}

		@Override
		public boolean removeEdge(E edge) {
			throw new UnsupportedOperationException("Immutable. ");
		}

		@Override
		public boolean isNeighbor(V v1, V v2) {
			return graph.isNeighbor(v1, v2);
		}

		@Override
		public boolean isIncident(V vertex, E edge) {
			return graph.isIncident(vertex, edge);
		}

		@Override
		public int degree(V vertex) {
			return graph.degree(vertex);
		}

		@Override
		public int getNeighborCount(V vertex) {
			return graph.getNeighborCount(vertex);
		}

		@Override
		public int getIncidentCount(E edge) {
			return graph.getIncidentCount(edge);
		}

		@Override
		public EdgeType getEdgeType(E edge) {
			return EdgeType.DIRECTED;
		}

		@Override
		public EdgeType getDefaultEdgeType() {
			return EdgeType.DIRECTED;
		}

		@Override
		public Collection<E> getEdges(EdgeType edge_type) {
			return edge_type == EdgeType.DIRECTED ? graph.getEdges() : Collections.<E>emptySet();
		}

		@Override
		public int getEdgeCount(EdgeType edge_type) {
			return edge_type == EdgeType.DIRECTED ? graph.getEdgeCount() : 0;
		}

	    public int inDegree(V vertex)
	    {
	        return this.getInEdges(vertex).size();
	    }

	    public int outDegree(V vertex)
	    {
	        return this.getOutEdges(vertex).size();
	    }

	    public boolean isPredecessor(V v1, V v2)
	    {
	        return this.getPredecessors(v1).contains(v2);
	    }

	    public boolean isSuccessor(V v1, V v2)
	    {
	        return this.getSuccessors(v1).contains(v2);
	    }

	    public int getPredecessorCount(V vertex)
	    {
	        return this.getPredecessors(vertex).size();
	    }

	    public int getSuccessorCount(V vertex)
	    {
	        return this.getSuccessors(vertex).size();
	    }
	    
		@Override
		public boolean isSource(V vertex, E edge) {
			return graph.isIncident(vertex, edge) && compare(vertex, graph.getOpposite(vertex, edge)) < 0;
		}

		@Override
		public boolean isDest(V vertex, E edge) {
			return graph.isIncident(vertex, edge) && compare(vertex, graph.getOpposite(vertex, edge)) > 0;
		}

		@Override
		public Collection<E> getInEdges(V vertex) {
			Collection<E> edges = new ArrayList<>();
			for(E edge : graph.getIncidentEdges(vertex)) {
				if (compare(vertex, graph.getOpposite(vertex, edge)) > 0) {
					edges.add(edge);
				}
			}
			return edges;
		}

		@Override
		public Collection<E> getOutEdges(V vertex) {
			Collection<E> edges = new ArrayList<>();
			for(E edge : graph.getIncidentEdges(vertex)) {
				if (compare(vertex, graph.getOpposite(vertex, edge)) < 0) {
					edges.add(edge);
				}
			}
			return edges;
		}

		@Override
		public Collection<V> getPredecessors(V vertex) {
			Collection<V> vertices = new ArrayList<>();
			for(V neighbor : graph.getNeighbors(vertex)) {
				if (compare(vertex, neighbor) < 0) {
					vertices.add(neighbor);
				}
			}
			return vertices;
		}

		@Override
		public Collection<V> getSuccessors(V vertex) {
			Collection<V> vertices = new ArrayList<>();
			for(V neighbor : graph.getNeighbors(vertex)) {
				if (compare(vertex, neighbor) > 0) {
					vertices.add(neighbor);
				}
			}
			return vertices;
		}

		@Override
		public boolean addEdge(E edge, Pair<? extends V> endpoints,
				EdgeType edgeType) {
			throw new UnsupportedOperationException("Immutable. ");
		}
		
	}

	@Override
	public int compare(V o1, V o2) {
		return numbers.get(o1).compareTo(numbers.get(o2));
	}
}
