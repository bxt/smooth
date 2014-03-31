package de.uniwue.smooth.planar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uniwue.smooth.util.tuples.HLPair;
import de.uniwue.smooth.util.tuples.LRPair;
import de.uniwue.smooth.util.tuples.MutablePair;
import de.uniwue.smooth.util.tuples.Pair;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * Embed a graph as a planar drawing. [U. Brandes 2009, unpublished]
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class BrandesEmbedding<V, E> extends AdjacencyListEmbedding<V, E> implements Embedding<V, E> {
	
	/**
	 * Uses this embedding to perform a planarity test on a graph.
	 * 
	 * @param graph Graph to test.
	 * @return If or not the graph may be embedding in a plane without crossings.
	 */
	public static <V, E> boolean isPlanar(UndirectedGraph<V, E> graph) {
		try {
			new BrandesEmbedding<V, E>(graph);
			return true;
		} catch (NotPlanarException e) {
			return false;
		}
	}
	
	private UndirectedGraph<V, E> graph;
	
	private List<V> roots = new ArrayList<>();
	private Map<V, List<E>> adjacencies = new HashMap<>();
	
	// Orientation
	private DirectedGraph<V, E> directedGraph = new DirectedSparseGraph<>();
	private Map<V, Integer> heights = new HashMap<V, Integer>();
	private Map<V, E> parentEdges = new HashMap<>();
	private Map<E, Integer> lowPointers = new HashMap<E, Integer>();
	private Map<E, Integer> low2Pointers = new HashMap<E, Integer>();
	private Map<E, Integer> nestingDepths = new HashMap<E, Integer>();
	
	// testing
	private Map<E, E> references = new HashMap<>();
	private Map<E, Integer> sides = new HashMap<>();
	private Deque<LRPair<HLPair<E>>> conflictStack = new LinkedList<>();
	private Map<E, LRPair<HLPair<E>>> stackBottoms = new HashMap<>();
	private Map<E, E> lowPointingEdges = new HashMap<E, E>();
	
	// embedding
	private Map<V, E> leftReferences = new HashMap<>();
	private Map<V, E> rightReferences = new HashMap<>();
	
	/**
	 * Initializes by embedding a given graph.
	 * 
	 * @param graph Graph to embed.
	 * @throws NotPlanarException Indicates that the given graph is not planar.
	 */
	public BrandesEmbedding(UndirectedGraph<V, E> graph) throws NotPlanarException {
		// check for simple graph here?
		
		if (graph.getEdgeCount() > graph.getVertexCount() *3 - 6)
			throw new NotPlanarException("Not planar, too many edges.");
		
		this.graph = graph;
		
		for(E e : graph.getEdges()) sides.put(e, 1);
		for(V v : graph.getVertices()) directedGraph.addVertex(v);
		
		orientation();
		testing();
		embedding();
	}
	
	/**
	 * STEP 1: Calculate an st ordering and collect some special attributes.
	 */
	private void orientation() {
		for (V v : graph.getVertices()) {
			if(heights.get(v) == null) {
				heights.put(v, 0);
				roots.add(v);
				orientationDfs(v);
			}
		}
	}
	
	private void orientationDfs(V v) {
		 E e  = parentEdges.get(v);
		 for (E f : graph.getIncidentEdges(v)) {
			 if (!directedGraph.containsEdge(f)) {
				 V w = graph.getOpposite(v, f);
				 directedGraph.addEdge(f, v, w);
				 lowPointers.put(f, heights.get(v));
				 low2Pointers.put(f, heights.get(v));
				 if (heights.get(w) == null) { // tree edge
					 parentEdges.put(w, f);
					 heights.put(w, heights.get(v) + 1);
					 orientationDfs(w);
				 } else { // back edge
					 lowPointers.put(f, heights.get(w));
				 }
				 calculateNestingDepth(f, v); 
				 updateLowpointsOfParentEdge(e, f);
			 }
		 }
	}
	
	private void calculateNestingDepth(E edge, V v) {
		 nestingDepths.put(edge, 2 * lowPointers.get(edge));
		 if(low2Pointers.get(edge) < heights.get(v)) { // chordal
			 nestingDepths.put(edge, nestingDepths.get(edge) + 1);
		 }
	}
	
	private void updateLowpointsOfParentEdge(E parent, E currentChild) {
		 if(parent != null) {
			 if(lowPointers.get(currentChild) < lowPointers.get(parent)) {
				 low2Pointers.put(parent, Math.min(lowPointers.get(parent), low2Pointers.get(currentChild)));
				 lowPointers.put(parent, lowPointers.get(currentChild));
			 } else if (lowPointers.get(currentChild) > lowPointers.get(parent)) {
				 low2Pointers.put(parent, Math.min(low2Pointers.get(parent), lowPointers.get(currentChild)));
			 } else {
				 low2Pointers.put(parent, Math.min(low2Pointers.get(parent), low2Pointers.get(currentChild)));
			 }
		 }
	}
	
	/**
	 * STEP 2: Tests the graph for planarity and collects values for a later embedding.
	 * This with {@link #testingDfs(Object)} is the main part of the algorithm.
	 * 
	 * @throws NotPlanarException If the graph is not planar.
	 */
	private void testing() throws NotPlanarException {
		for(V v : roots) testingDfs(v);
	}

	private void testingDfs(V v) throws NotPlanarException {
		E e = parentEdges.get(v);
		List<E> adjacent = adjacentOrderedByNestingDepth(v);
		for (E f : adjacent) {
			stackBottoms.put(f, conflictStack.peek());
			V target = directedGraph.getOpposite(v, f);
			if (f == parentEdges.get(target)) { // tree edge
				testingDfs(target);
			} else { // back edge
				lowPointingEdges.put(f, f);
				conflictStack.push(new MutablePair<HLPair<E>>(new MutablePair<E>(), new MutablePair<>(f, f)));
			}
			if (lowPointers.get(f) < heights.get(v)) { // f has return edge
				if(f == adjacent.get(0)) {
					lowPointingEdges.put(e, adjacent.get(0)); // TODO: check this
				} else {
					addConstraints(f, e);
				}
			}
		}
		if(e != null) { // v is not the root
			V u = directedGraph.getOpposite(v, e);
			//if(u != directedGraph.getSource(e)) throw new IllegalStateException();
			trimBackEdges(u); // trim back edges ending at parent u
			// side of e is side of a highest return edge
			if(lowPointers.get(e) < heights.get(u)) { // e has return edge
				E hL = conflictStack.peek().getLeft().getHigh();
				E hR = conflictStack.peek().getRight().getHigh();
				if(hL != null && (hR == null || lowPointers.get(hL) > lowPointers.get(hR))) {
					references.put(e, hL);
				} else {
					references.put(e, hR);
				}
			}
		}
	}
	
	private void trimBackEdges(V u) {
		// drop entire conflict pairs
		while (!conflictStack.isEmpty() && lowest(conflictStack.peek()) == heights.get(u)) {
			LRPair<HLPair<E>> p = conflictStack.pop();
			if(p.getLeft().getLow() != null) {
				sides.put(p.getLeft().getLow(), -1);
			}
		}
		if(!conflictStack.isEmpty()) {
			LRPair<HLPair<E>> p = conflictStack.pop();
			// trim left interval
			while (p.getLeft().getHigh() != null && directedGraph.getDest(p.getLeft().getHigh()) == u) {
				p.getLeft().setHigh(references.get(p.getLeft().getHigh()));
			}
			if (p.getLeft().getHigh() == null && p.getLeft().getLow() != null) {// just emptied
				references.put(p.getLeft().getLow(), p.getRight().getLow());
				sides.put(p.getLeft().getLow(), -1);
				p.getLeft().setLow(null);
			}
			// trim right interval
			while (p.getRight().getHigh() != null && directedGraph.getDest(p.getRight().getHigh()) == u) {
				p.getRight().setHigh(references.get(p.getRight().getHigh()));
			}
			if (p.getRight().getHigh() == null && p.getRight().getLow() != null) {// just emptied
				references.put(p.getRight().getLow(), p.getLeft().getLow());
				sides.put(p.getRight().getLow(), -1);
				p.getRight().setLow(null);
			}
			conflictStack.push(p);
		}		
	}
	
	private void addConstraints(E f, E e) throws NotPlanarException {
		LRPair<HLPair<E>> p = new MutablePair<HLPair<E>>(new MutablePair<E>(), new MutablePair<E>());
		// merge return edges of f into p.R
		do {
			LRPair<HLPair<E>> q = conflictStack.pop();
			if(!isEmpty(q.getLeft())) {
				swap(q);
			}
			if(!isEmpty(q.getLeft())) {
				throw new NotPlanarException("Not planar. (1)");
			} else {
				if(lowPointers.get(q.getRight().getLow()) > lowPointers.get(e)) { // merge intervals
					if(isEmpty(p.getRight())) {
						p.getRight().setHigh(q.getRight().getHigh());
					} else {
						references.put(p.getRight().getLow(), q.getRight().getHigh());
					}
					p.getRight().setLow(q.getRight().getLow());
				} else { //  make consistent
					references.put(q.getRight().getLow(), lowPointingEdges.get(e));
				}
			}
		} while (conflictStack.peek() != stackBottoms.get(f));
		// merge conflicting return edges of e1,...,ei-1 into p.L
		while (conflicting(conflictStack.peek().getLeft(),f) || conflicting(conflictStack.peek().getRight(), f)) {
			LRPair<HLPair<E>> q = conflictStack.pop();
			if (conflicting(q.getRight(), f)) {
				swap(q);
			}
			if (conflicting(q.getRight(), f)) {
				throw new NotPlanarException("Not planar. (2)");
			} else { // merge interval below lowpt f into P.R
				references.put(p.getRight().getLow(), q.getRight().getHigh());
				if (q.getRight().getLow() != null) {
					p.getRight().setLow(q.getRight().getLow());
				}
			}
			if (isEmpty(p.getLeft())) {
				p.getLeft().setHigh(q.getLeft().getHigh());
			} else {
				references.put(p.getLeft().getLow(), q.getLeft().getHigh());
			}
			p.getLeft().setLow(q.getLeft().getLow());
		}
		if(!isEmpty(p.getLeft()) || !isEmpty(p.getRight())) {
			conflictStack.push(p);
		}
	}
	
	private boolean isEmpty(HLPair<?> interval) {
		return interval.getLow() == null && interval.getHigh() == null;
	}
	
	private <T> void swap(Pair<T> pair) {
		T tmp = pair.getFirst();
		pair.setFirst(pair.getSecond());
		pair.setSecond(tmp);
	}
	
	private int lowest(LRPair<HLPair<E>> p) {
		if(isEmpty(p.getLeft())) return lowPointers.get(p.getRight().getLow());
		if(isEmpty(p.getRight())) return lowPointers.get(p.getLeft().getLow());
		return Math.min(lowPointers.get(p.getLeft().getLow()), lowPointers.get(p.getRight().getLow()));
	}

	private boolean conflicting(HLPair<E> interval, E edge) {
		return ! isEmpty(interval) && lowPointers.get(interval.getHigh()) > lowPointers.get(edge);
	}
	
	/**
	 * STEP 3: Calculate the adjacency lists using the sides and nesting depths of the previous steps.
	 */
	private void embedding() {
		for (E e  : graph.getEdges()) {
			nestingDepths.put(e, nestingDepths.get(e) * sign(e));
		}
		for (V v: graph.getVertices()) {
			adjacencies.put(v, adjacentOrderedByNestingDepth(v));
		}
		for (V s : roots) {
			embeddingDfs(s);
		}	
	}
	
	private List<E> adjacentOrderedByNestingDepth(V v) {
		ArrayList<E> adjacent = new ArrayList<>(directedGraph.getOutEdges(v));
		Collections.sort(adjacent, new Comparator<E>() {
			@Override public int compare(E o1, E o2) {
				return nestingDepths.get(o1).compareTo(nestingDepths.get(o2));
			}
		});
		return adjacent;
	}

	private int sign(E e) {
		if(references.get(e) != null) {
			sides.put(e, sides.get(e) *  sign(references.get(e)));
			references.remove(e);
		}
		return sides.get(e);
	}
	
	private void embeddingDfs(V v) {
		List<E> temp = new ArrayList<E>(adjacencies.get(v));
		for (E f : temp) {
			V w = directedGraph.getDest(f);
			if(v != w) { // outgoing
				List<E> adjacent = adjacencies.get(w);
				if(f == parentEdges.get(w)) { // tree edge
					adjacent.remove(f);
					adjacent.add(0, f);
					leftReferences.put(v, f);
					rightReferences.put(v, f);
					embeddingDfs(w);
				} else { // back edge
					if(sides.get(f) == 1) {
						adjacent.remove(f);
						adjacent.add(adjacent.indexOf(rightReferences.get(w)) + 1, f);
					} else {
						adjacent.remove(f);
						adjacent.add(adjacent.indexOf(leftReferences.get(w)), f);
						leftReferences.put(w, f);
					}
				}
			}
		}
	}

	@Override
	protected List<E> getAdjacent(V vertex) {
		return adjacencies.get(vertex);
	}
	
	@Override
	protected V getOpposite(V vertex, E edge) {
		return graph.getOpposite(vertex, edge);
		
	}
	
	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorOnOuterface() {
		return getEmbeddingIteratorAtVertex(roots.get(0));
	}

	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorAtEdge(E edge) {
		return getEmbeddingIterator(graph.getIncidentVertices(edge).iterator().next(), edge);
	}

}
