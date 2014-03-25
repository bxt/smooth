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
	
	public Set<V> getLeftVertices() {
		return leftVertices;
	}

	public Set<V> getRightVertices() {
		return rightVertices;
	}

	public Set<E> getLeftEdges() {
		return leftEdges;
	}

	public Set<E> getRightEdges() {
		return rightEdges;
	}

	private void initialize() {
		leftStartVertices = new LinkedList<>();
		rightStartVertices = new LinkedList<>();
		restrictions = new HashSet<>();
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
						if(layout.getPortAssignment(target).get(Port.T).equals(edge)) { // cut downwards L edge
							cutAt(edge, target);
							// stay at the same vertex
							quadrant = quadrant.getVerticalOpposite();
						} else if (layout.getPortAssignment(target).get(quadrant.getHorizontalPort()).equals(edge)) { // follow C edge
							vertex = target;
							quadrant = quadrant.getVerticalOpposite();
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
					if(isHigher(target)) { // TODO: might happen for the last U edge.
						throw new IllegalStateException("Bad edge. (2)");
					} else {
						if(layout.getPortAssignment(target).get(quadrant.getHorizontalPort().getOpposite()).equals(edge)) { // follow L edge above
							vertex = target;
							quadrant = quadrant.getOpposite();
						} else if(layout.getPortAssignment(target).get(quadrant.getHorizontalPort()).equals(edge)) { // follow L edge below
							vertex = target;
							// quadrant stays the same at new edge
						} else if(layout.getPortAssignment(target).get(Port.T).equals(edge)) { // follow straight edge
							vertex = target;
							quadrant = quadrant.getVerticalOpposite();
						} else if(layout.getPortAssignment(target).get(Port.B).equals(edge)) { // U edge TODO possible?
							restrictions.add(edge);
							arrivedAtBottom();
						} else {
							throw new IllegalStateException("Bad port assignment.");
						}
					}
				}
			}
		}

	}
	
	private boolean isAtBottom() {
		return vertex == null;
	}
	
	private boolean isHigher(V target) {
		return  layout.getVertexLocation(target).getSecond().compareTo(layout.getVertexLocation(vertex).getSecond()) >= 0;
	}
	
	private void cutAt(E edge, V target) {
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
		vertex = null;
		quadrant = null;
	}
	
	private Set<V> depthFirstFind(Collection<V> starts) {
		Set<V> results = new HashSet<>();
		for(V v : starts)
			depthFirstTraverse(v, results);
		return results;
	}
	
	private void depthFirstTraverse(V v, Set<V> results) {
		results.add(v);
		for(V w : layout.getGraph().getNeighbors(v)) {
			E edge = layout.getGraph().findEdge(v, w);
			if(!results.contains(w) && !restrictions.contains(edge) && scope.contains(w)) {
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
		visited.add(v);
		for(V w : layout.getGraph().getNeighbors(v)) {
			E edge = layout.getGraph().findEdge(v, w);
			if(!restrictions.contains(edge)) {
				results.add(edge);
				if(!results.contains(w) && scope.contains(w)) {
					depthFirstTraverse(w, visited, results);
				}
			}
		}
	}
}