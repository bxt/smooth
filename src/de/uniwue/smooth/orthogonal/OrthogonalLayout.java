package de.uniwue.smooth.orthogonal;

import java.util.Map;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.Pair;

public interface OrthogonalLayout<V, E> extends Layout<V, E> {
	
	public Pair<Integer> getVertexLocation(V v);
	public Pair<Integer> getEdgeLocation(E e);
	public Map<Port, E> getPortAssignment(V v);
	
}
