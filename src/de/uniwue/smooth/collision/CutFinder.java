package de.uniwue.smooth.collision;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.orthogonal.Quadrant;
import edu.uci.ics.jung.graph.util.Pair;

public class CutFinder<V, E> {
	
	private OrthogonalLayout<V, E> layout;
	private Set<V> scope;
	
	private List<V> leftStartVertices = null;
	private List<V> rightStartVertices = null;
	private Set<E> restrictions = null;
	private V vertex = null;
	private Quadrant quadrant = null;
	
	public CutFinder(OrthogonalLayout<V, E> layout, Set<V> scope) {
		super();
		this.layout = layout;
		this.scope = scope;
	}
	
	public Pair<Set<V>> cut(V v, Quadrant q) {
		
		initialize(v, q);
		addFirstVertex();
		
		findCut();
		
		Pair<Set<V>> result = new Pair<>(depthFirstFind(leftStartVertices), depthFirstFind(rightStartVertices));
		
		return result;
	}
	
	private void initialize(V vertex, Quadrant quadrant) {
		leftStartVertices = new LinkedList<>();
		rightStartVertices = new LinkedList<>();
		restrictions = new HashSet<>();
		this.vertex = vertex;
		this.quadrant = quadrant;
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
		} else if (targetVertexDirection == Port.R) {
			cutAt(edge, vertex, target);
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
}
