package de.uniwue.smooth.orthogonal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.collections15.comparators.ComparableComparator;
import org.apache.commons.collections15.comparators.TransformingComparator;
import org.apache.commons.collections15.functors.MapTransformer;

import de.uniwue.smooth.palm.StOrdering;
import de.uniwue.smooth.planar.Embedding;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Based on {@link LiuEtAlLayout} but does the removal of s-shaped edges, too.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class CompressingLiuEtAlLayout<V, E> extends LiuEtAlLayout<V, E> {
	
	private List<Set<V>> sets;
	
	public CompressingLiuEtAlLayout(Graph<V, E> graph,
			StOrdering<V, E> stOrdering) {
		super(graph, stOrdering);
	}

	public CompressingLiuEtAlLayout(Graph<V, E> graph) {
		super(graph);
	}

	public CompressingLiuEtAlLayout(Graph<V, E> graph, Embedding<V, E> embedding) {
		super(graph, embedding);
	}

	public CompressingLiuEtAlLayout(Graph<V, E> graph,
			Embedding<V, E> embedding, StOrdering<V, E> stOrdering) {
		super(graph, embedding, stOrdering);
	}

	/**
	 * The list of vertex sets placed at one height, ordered by height.
	 * @return List of vertex sets placed at one height, ordered by height.
	 */
	public List<Set<V>> getSets() {
		return sets;
	}
	
	/**
	 * An optional step which moves vertices connected by s-shaped edges
	 * into the same row and optimizes the height of the drawing.
	 */
	@Override
	protected void compression() { // TODO: remove columns of s-edges?
		DirectedGraph<Integer, E> setGraph = new DirectedSparseGraph<>();
		final Map<V, Integer> vertexSet = new HashMap<>();
		final Map<Integer, Set<V>> sets = MapUtils.lazyMap(new HashMap<Integer, Set<V>>(), new Factory<Set<V>>() {
			@Override public Set<V> create() { return new HashSet<>(); }
		});
		
		
		int setCounter = 0;
		for (V v : getGraph().getVertices()) {
			if(!vertexSet.containsKey(v)) {
				assignVertexSet(setCounter, vertexSet, v, Port.L, sets.get(setCounter));
				assignVertexSet(setCounter, vertexSet, v, Port.R, sets.get(setCounter));
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
		
		List<Integer> setsSortedByHeight = new ArrayList<Integer>(setHeights.keySet());
		Collections.sort(setsSortedByHeight, new TransformingComparator<>(MapTransformer.getInstance(setHeights), new ComparableComparator<Integer>()));
		this.sets = new ArrayList<Set<V>>();
		CollectionUtils.collect(setsSortedByHeight, MapTransformer.getInstance(sets), this.sets);
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
	private void assignVertexSet(int setCounter, Map<V, Integer> vertexSet, V v, Port direction, Set<V> set) {
		V u, w = v; E e = null;
		do {
			if(e != null) {
				edgeColumns.get(e).remove();
				edgeColumns.put(e, vertexColumns.get(v));
			}
			vertexSet.put(w, setCounter);
			set.add(w);
			u = w;
			e = getPortAssignment(u).get(direction);
			if(e != null) w = getGraph().getOpposite(u, e);
		} while (e != null && e.equals(getPortAssignment(w).get(direction.getOpposite())));
	}
	
}
