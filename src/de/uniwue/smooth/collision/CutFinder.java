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
		
		findCut();
		
		Pair<Set<V>> result = new Pair<>(depthFirstFind(leftStartVertices), depthFirstFind(rightStartVertices));
		
		return result;
	}
	
	private void findCut() {
		while(!isAtBottom()) {
			if(quadrant == Quadrant.II) {
				E edge = layout.getPortAssignment(vertex).get(Port.L);
				if(edge == null) {
					quadrant = Quadrant.III;
				} else {
					V target = layout.getGraph().getOpposite(vertex, edge);
					boolean isHigher = layout.getVertexLocation(target).getSecond().compareTo(layout.getVertexLocation(vertex).getSecond()) >= 0;
					if(isHigher) {
						cutAt(edge, target, vertex);
						quadrant = Quadrant.III;
					} else {
						vertex = target;
						if(layout.getPortAssignment(target).get(Port.T).equals(edge)) {
							quadrant = Quadrant.II;
						} else if (layout.getPortAssignment(target).get(Port.L).equals(edge)) {
							quadrant = Quadrant.III;
						} else {
							throw new IllegalStateException("Bad edge. (1)");
						}
					}
					
				}
			} else if(quadrant == Quadrant.III) {
				E edge = layout.getPortAssignment(vertex).get(Port.B);
				if(edge == null) {
					arrivedAtBottom();
				} else {
					V target = layout.getGraph().getOpposite(vertex, edge);
					boolean isHigher = layout.getVertexLocation(target).getSecond().compareTo(layout.getVertexLocation(vertex).getSecond()) >= 0;
					if(isHigher) {
						throw new IllegalStateException("Bad edge. (2)");
					} else {
						if(layout.getPortAssignment(target).get(Port.R).equals(edge)) {
							cutAt(edge, target, vertex);
							vertex = target;
							quadrant = Quadrant.IV;
						} else if(layout.getPortAssignment(target).get(Port.L).equals(edge)) {
							vertex = target;
							quadrant = Quadrant.IV;
						} else if(layout.getPortAssignment(target).get(Port.T).equals(edge)) {
							vertex = target;
							quadrant = Quadrant.II;
						} else if(layout.getPortAssignment(target).get(Port.B).equals(edge)) {
							restrictions.add(edge);
							arrivedAtBottom();
						} else {
							throw new IllegalStateException("Bad port assignment.");
						}
					}
				}
			} else if(quadrant == Quadrant.I) {
				throw new RuntimeException("Not implemented yet.");
			} else if(quadrant == Quadrant.III) {
				throw new RuntimeException("Not implemented yet.");
			}
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
	
	private boolean isAtBottom() {
		return vertex == null;
	}
	
	private void initialize(V vertex, Quadrant quadrant) {
		leftStartVertices = new LinkedList<>();
		rightStartVertices = new LinkedList<>();
		restrictions = new HashSet<>();
		this.vertex = vertex;
		this.quadrant = quadrant;
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
