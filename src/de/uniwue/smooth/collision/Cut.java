package de.uniwue.smooth.collision;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.orthogonal.Quadrant;

public class Cut<V, E> {
	
	private OrthogonalLayout<V, E> layout;
	private Set<V> scope;
	
	private List<V> leftStartVertices = null;
	private List<V> rightStartVertices = null;
	private Set<E> restrictions = null;
	private V vertex = null;
	private Quadrant quadrant = null;
	
	private Set<V> leftVertices = null;
	private Set<V> rightVertices = null;
	
	private Set<E> leftEdges = null;
	private Set<E> rightEdges = null;
	
	public Cut(OrthogonalLayout<V, E> layout, Set<V> scope, V vertex, Quadrant quadrant) {
		super();
		this.layout = layout;
		this.scope = scope;
		
		this.vertex = vertex;
		this.quadrant = quadrant;
		
		run();
	}
	
	private void run() {
		
		initialize();
		addFirstVertex();
		
		findCut();
		
		leftVertices = depthFirstFind(leftStartVertices);
		rightVertices = depthFirstFind(rightStartVertices);
		
		leftEdges.addAll(depthFirstFindEdges(leftStartVertices));
		rightEdges.addAll(depthFirstFindEdges(rightStartVertices));
	}

	public Set<V> getVerticesAt(Port direction) {
		if(direction == Port.L) {
			return leftVertices;
		} else if (direction == Port.R) {
			return rightVertices;
		} else {
			throw new IllegalArgumentException("Only left or right is allowed as direction, not: " + direction);
		}
	}

	public Set<E> getEdgesAt(Port direction) {
		if(direction == Port.L) {
			return leftEdges;
		} else if (direction == Port.R) {
			return rightEdges;
		} else {
			throw new IllegalArgumentException("Only left or right is allowed as direction, not: " + direction);
		}
	}

	private void initialize() {
		leftStartVertices = new LinkedList<>();
		rightStartVertices = new LinkedList<>();
		restrictions = new HashSet<>();
		leftEdges = new HashSet<>();
		rightEdges = new HashSet<>();
	}
	
	private void addFirstVertex() {
		(quadrant.getHorizontalPort() == Port.L ? rightStartVertices : leftStartVertices).add(vertex);
	}

	private void findCut() {
		
		while(!isAtBottom()) {
			if(quadrant.isUpper()) { // right above the current vertex
				E edge = layout.getPortAssignment(vertex).get(quadrant.getHorizontalPort());
				if(edge == null) { // no edge to cross
					quadrant = quadrant.getVerticalOpposite();
				} else { // there is an edge
					V target = layout.getGraph().getOpposite(vertex, edge);
					if(isHigher(target)) { // cut through outgoing I, L or C edge
						cutAt(edge, target);
						// stay at the same vertex
						quadrant = quadrant.getVerticalOpposite();
					} else {
						if(isEdgeAt(target, edge, Port.T)) { // cut downwards L edge
							cutAt(edge, target);
							// stay at the same vertex
							quadrant = quadrant.getVerticalOpposite();
						} else if (isEdgeAt(target, edge, quadrant.getHorizontalPort())) { // follow C edge
							vertex = target;
							quadrant = quadrant.getVerticalOpposite();
						} else if (isEdgeAt(target, edge, Port.B) && quadrant == Quadrant.I) { // G edge pointing to V1
							restrictions.add(edge); // cut, but do not add any stating vertices
							rightEdges.add(edge);
							arrivedAtBottom();
							vertex = target; // got to actual V1 bot vertex
							arrivedAtBottom();
						} else {
							throw new IllegalStateException("Bad edge. (1)");
						}
					}
					
				}
			} else { // right below the current vertex
				E edge = layout.getPortAssignment(vertex).get(Port.B);
				if(edge == null) { // no edge to follow
					arrivedAtBottom(); // Only vertex "s" can have no edge on its bottom port, we're done.
				} else { // follow an edge downwards
					V target = layout.getGraph().getOpposite(vertex, edge);
					if(isHigher(target)) { // might happen for the last U edge from first vertex
						restrictions.add(edge); // cut, but do not add any stating vertices
						rightEdges.add(edge);
						arrivedAtBottom();
					} else {
						if(isEdgeAt(target, edge, quadrant.getHorizontalPort().getOpposite())) { // follow L edge above
							vertex = target;
							quadrant = quadrant.getOpposite();
						} else if(isEdgeAt(target, edge, quadrant.getHorizontalPort())) { // follow L edge below
							vertex = target;
							// quadrant stays the same at new edge
						} else if(isEdgeAt(target, edge, Port.T)) { // follow straight edge
							vertex = target;
							quadrant = quadrant.getVerticalOpposite();
						} else if(isEdgeAt(target, edge, Port.B)) { // U edge to first vertex
							restrictions.add(edge); // cut, but do not add any stating vertices
							leftEdges.add(edge);
							arrivedAtBottom();
						} else {
							throw new IllegalStateException("Bad port assignment.");
						}
					}
				}
			}
		}
		
		// special case: inverted U edge from bottommost vertex to topmost one.
		E topEdgeAtBottom = layout.getPortAssignment(vertex).get(Port.T);
		if (vertex != null && isEdgeAt(layout.getGraph().getOpposite(vertex, topEdgeAtBottom), topEdgeAtBottom,  Port.T)) {
			restrictions.add(topEdgeAtBottom);
			leftEdges.add(topEdgeAtBottom);
		}

	}
	
	private boolean isEdgeAt(V v, E e, Port p) {
		E actualE = layout.getPortAssignment(v).get(p);
		return e.equals(actualE);
	}
	
	private boolean isAtBottom() {
		return quadrant == null;
	}
	
	private boolean isHigher(V target) {
		return  layout.getVertexLocation(target).getSecond().compareTo(layout.getVertexLocation(vertex).getSecond()) >= 0;
	}
	
	private void cutAt(E edge, V target) {
		if(edge == null) throw new IllegalStateException("Edge was null (A)");
		Port targetVertexDirection = quadrant.getHorizontalPort();
		if(targetVertexDirection == Port.L) {
			cutAt(edge, target, vertex);
			leftEdges.add(edge);
		} else if (targetVertexDirection == Port.R) {
			cutAt(edge, vertex, target);
			rightEdges.add(edge);
		} else {
			throw new IllegalArgumentException("Only left or right are valid directions! Not: " + targetVertexDirection);
		}
	}
	
	private void cutAt(E edge, V leftVertex, V rightVertex) {
		restrictions.add(edge);
		leftStartVertices.add(leftVertex);
		rightStartVertices.add(rightVertex);
	}
	
	private void arrivedAtBottom() {
		quadrant = null;
	}
	
	private Set<V> depthFirstFind(Collection<V> starts) {
		Set<V> results = new HashSet<>();
		for(V v : starts)
			depthFirstTraverse(v, results);
		return results;
	}
	
	private void depthFirstTraverse(V v, Set<V> results) {
		if(!scope.contains(v)) return;
		results.add(v);
		for(V w : layout.getGraph().getNeighbors(v)) {
			E edge = layout.getGraph().findEdge(v, w);
			if(edge == null) edge = layout.getGraph().findEdge(w, v); // directed graph hack
			if(!results.contains(w) && !restrictions.contains(edge)) {
				depthFirstTraverse(w, results);
			}
		}
	}
	
	private Set<E> depthFirstFindEdges(Collection<V> starts) {
		Set<V> visited = new HashSet<>();
		Set<E> results = new HashSet<>();
		for(V v : starts)
			depthFirstTraverse(v, visited, results);
		return results;
	}
	
	private void depthFirstTraverse(V v, Set<V> visited, Set<E> results) {
		if(!scope.contains(v)) return;
		visited.add(v);
		for(V w : layout.getGraph().getNeighbors(v)) {
			E edge = layout.getGraph().findEdge(v, w);
			if(edge == null) edge = layout.getGraph().findEdge(w, v); // directed graph hack
			if(edge == null) throw new IllegalStateException("Edge was null (B)");
			if(!restrictions.contains(edge)) {
				results.add(edge);
				if(!visited.contains(w)) {
					depthFirstTraverse(w, visited, results);
				}
			}
		}
	}
}
