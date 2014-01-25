package de.uniwue.smooth.orthogonal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.collections15.functors.MapTransformer;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Based on {@link LiuEtAlLayout} but does the removal o s-shaped edges, too.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class CompressingLiuEtAlLayout<V, E> extends LiuEtAlLayout<V, E> {
	
	public CompressingLiuEtAlLayout(Graph<V, E> graph) {
		super(graph);
	}
	
	/**
	 * An optional step which moves vertices connected by s-shaped edges
	 * into the same row and optimizes the height of the drawing.
	 */
	@Override
	protected void compression() { // TODO: remove columns of s-edges?
		DirectedGraph<Integer, E> setGraph = new DirectedSparseGraph<>();
		final Map<V, Integer> vertexSet = new HashMap<>();
		
		int setCounter = 0;
		for (V v : getGraph().getVertices()) {
			if(!vertexSet.containsKey(v)) {
				assignVertexSet(setCounter, vertexSet, v, Port.L);
				assignVertexSet(setCounter, vertexSet, v, Port.R);
				setGraph.addVertex(setCounter);
				setCounter++;
			}
		}
		
		for (E e: getGraph().getEdges()) {
			List<V> originalEndpoints = new ArrayList<>(getGraph().getEndpoints(e));
			Collections.sort(originalEndpoints, stOrdering);
			Collection<Integer> endpoints = CollectionUtils.collect(originalEndpoints, MapTransformer.getInstance(vertexSet));
			if(!allEqual(endpoints)) // no self edge loops
				setGraph.addEdge(e, endpoints);
		}
		
		final Map<Integer, Integer> setHeights = new HashMap<>();
		
		Queue<Integer> setQueue = new LinkedList<>();
		Integer firstSet = vertexSet.get(stOrdering.getList().get(0));
		setQueue.add(firstSet);
		setHeights.put(firstSet, 0);
		while (!setQueue.isEmpty()) {
			Integer set = setQueue.remove();
			if(setGraph.getPredecessorCount(set) == 0) {
				for (Integer adjacentSet : new ArrayList<>(setGraph.getSuccessors(set))) {
					setGraph.removeEdge(setGraph.findEdge(set, adjacentSet));
					if(!setHeights.containsKey(adjacentSet)) setQueue.add(adjacentSet);
					setHeights.put(adjacentSet, Math.max(setHeights.get(set) + 1, MapUtils.getObject(setHeights, adjacentSet, 0)));
				}
			} else {
				setQueue.add(set);
			}
		}
		
		for (V v : getGraph().getVertices()) {
			vertexRows.put(v, setHeights.get(vertexSet.get(v)));
		}
	}
	
	/**
	 * Tests if all objects in a collection are the same.
	 * @param collection Collection to test.
	 * @return <tt>False</tt> for empty collections, <tt>true</tt> for collections with one item; Otherwise <tt>true</tt> iff all elements are the same.
	 */
	private static <E> boolean allEqual(Collection<E> collection) {
		if(collection.isEmpty()) return false;
		E ref = null;
		for (E e : collection) {
			if (ref == null) {
				ref = e;
			} else {
				if (!ref.equals(e)) return false;
			}
		}
		return true;
	}
	
	/**
	 * Move along S-shaped edges.
	 * @param setCounter Set number to assign to the vertices in this height.
	 * @param vertexSet Map of vertices to their set number.
	 * @param v Start vertex.
	 * @param direction Direction to find s-edges at.
	 */
	private void assignVertexSet(int setCounter, Map<V, Integer> vertexSet, V v, Port direction) {
		V u, w = v; E e = null;
		do {
			if(e != null) {
				edgeColumns.get(e).remove();
				edgeColumns.put(e, vertexColumns.get(v));
			}
			vertexSet.put(w, setCounter);
			u = w;
			e = getPortAssignment(u).get(direction);
			if(e != null) w = getGraph().getOpposite(u, e);
		} while (e != null && e.equals(getPortAssignment(w).get(direction.getOpposite())));
	}
	
}
