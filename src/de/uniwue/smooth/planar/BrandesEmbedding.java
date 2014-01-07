package de.uniwue.smooth.planar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class BrandesEmbedding<V, E> {
	
	private UndirectedGraph<V, E> graph;
	
	private List<V> roots = new ArrayList<>();
	
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
	private Deque<Pair<Pair<E>>> conflictStack;
	private Map<E, Pair<Pair<E>>> stackBottoms;
	private Map<E, E> lowPointingEdges = new HashMap<E, E>();
	
	// embedding
	private Map<V, E> leftReferences = new HashMap<>();
	private Map<V, E> rightReferences = new HashMap<>();
	
	public BrandesEmbedding(UndirectedGraph<V, E> graph) {
		// check for simple graph here?
		
		if (graph.getEdgeCount() > graph.getVertexCount() *3 - 6)
			throw new IllegalArgumentException("Graph is not planar, too many edges.");
		
		this.graph = graph;
		
		orientation();
		testing();
		embedding();
	}

	private void orientation() {
		for (V v : graph.getVertices()) {
			if(heights.get(v) == null) {
				heights.put(v, 0);
				roots.add(v);
				dfs1(v);
			}
		}
	}
	
	private void dfs1(V v) {
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
					 dfs1(w);
				 } else { // back edge
					 lowPointers.put(f, heights.get(w));
				 }
				 // nesting depth
				 nestingDepths.put(f, 2 * lowPointers.get(f));
				 if(low2Pointers.get(f) < heights.get(v)) { // chordal
					 nestingDepths.put(f, nestingDepths.get(f) + 1);
				 }
				 // update lowpoints of parent edge e
				 if(e != null) {
					 if(lowPointers.get(f) < lowPointers.get(e)) {
						 low2Pointers.put(e, Math.min(lowPointers.get(e), low2Pointers.get(f)));
						 lowPointers.put(e, lowPointers.get(f));
					 } else if (lowPointers.get(f) > lowPointers.get(e)) {
						 low2Pointers.put(e, Math.min(low2Pointers.get(e), lowPointers.get(f)));
					 } else {
						 low2Pointers.put(e, Math.min(low2Pointers.get(e), low2Pointers.get(f)));
					 }
				 }
			 }
		 }
	}
	
	private void testing() {
		for(V v : roots) dfs2(v);
		
	}

	private void dfs2(V v) {
		E e = parentEdges.get(v);
		List<E> adjacent = adjacentOrderedByNestingDepth(v);
		for (E f : adjacent) {
			stackBottoms.put(f, conflictStack.peek());
			V target = directedGraph.getOpposite(v, f);
			if (f == parentEdges.get(target)) { // tree edge
				dfs2(target);
			} else { // back edge
				lowPointingEdges.put(f, f);
				conflictStack.push(new Pair<Pair<E>>(null, new Pair<>(f, f))); // TODO: Pair with null
			}
			if (lowPointers.get(f) < heights.get(v)) { // f has return edge
				if(f == adjacent.get(0)) {
					lowPointingEdges.put(f, adjacent.get(0));
				} else {
					// add contraints of f
					Pair<Pair<E>> p = new Pair<>(null, null);
					// merge return edges of f into p.R
					do {
						Pair<Pair<E>> q = conflictStack.pop();
						if(q.getFirst() != null) {
							q = new Pair<>(q.getSecond(), q.getFirst());
						}
						if(q.getFirst() != null) {
							throw new IllegalArgumentException("Not planar. (1)");
						} else {
							if(lowPointers.get(q.getSecond().getFirst()) > lowPointers.get(e)) { // merge intervals
								if(p.getSecond() == null) {
									p = new Pair<>(p.getFirst(), new Pair<>(p.getSecond().getFirst(), q.getSecond().getSecond()));
								} else {
									references.put(p.getSecond().getFirst(), q.getSecond().getSecond());
								}
								p = new Pair<>(p.getFirst(), new Pair<>(q.getSecond().getFirst(), p.getSecond().getSecond()));
							} else { //  make consisten
								references.put(q.getSecond().getFirst(), lowPointingEdges.get(e));
							}
						}
					} while (conflictStack.peek() != stackBottoms.get(f));
					// merge conflicting return edges of e1,...,ei-1 into p.L
					while (conflicting(conflictStack.peek().getFirst(),f) || conflicting(conflictStack.peek().getSecond(), f)) {
						Pair<Pair<E>> q = conflictStack.pop();
						if (conflicting(q.getSecond(), f)) {
							q = new Pair<>(q.getSecond(), q.getFirst());
						}
						if (conflicting(q.getSecond(), f)) {
							throw new IllegalArgumentException("Not planar. (2)");
						} else { // merge interval below lowpt f into P.R
							references.put(p.getSecond().getFirst(), q.getSecond().getSecond());
							if (q.getSecond().getFirst() != null) {
								p = new Pair<>(p.getFirst(), new Pair<>(q.getSecond().getFirst(), p.getSecond().getSecond()));
							}
						}
						if (p.getFirst() != null) {
							p = new Pair<>(new Pair<>(p.getFirst().getFirst(), q.getFirst().getSecond()), p.getSecond());
						} else {
							references.put(p.getFirst().getFirst(), q.getFirst().getSecond());
						}
						p = new Pair<>(new Pair<>(q.getFirst().getFirst(), p.getFirst().getSecond()), p.getSecond());
					}
					if(p.getFirst() != null || p.getSecond() != null) {
						conflictStack.push(p);
					}
				}
			}
		}
		if(e != null) {
			V u = directedGraph.getOpposite(v, e);
			// trim back edges ending at parent u
			// drop entire conflict pairs
			while (!conflictStack.isEmpty() && lowest(conflictStack.peek()) == heights.get(u)) {
				Pair<Pair<E>> p = conflictStack.pop();
				if(p.getFirst().getFirst() != null) {
					sides.put(p.getFirst().getFirst(), -1);
				}
			}
			if(!conflictStack.isEmpty()) {
				Pair<Pair<E>> p = conflictStack.pop();
				// trim left interval
				while (p.getFirst().getSecond() != null && directedGraph.getDest(p.getFirst().getSecond()) == u) {
					p = new Pair<>(new Pair<> (p.getFirst().getFirst(), references.get(p.getFirst().getSecond())), p.getSecond());
				}
				if (p.getFirst().getSecond() == null && p.getFirst().getFirst() != null) {// just emptied
					references.put(p.getFirst().getFirst(), p.getSecond().getFirst());
					sides.put(p.getFirst().getFirst(), -1);
					p = new Pair<>(new Pair<> (null, p.getFirst().getSecond()), p.getSecond());
				}
				// trim right interval
				while (p.getSecond().getSecond() != null && directedGraph.getDest(p.getSecond().getSecond()) == u) {
					p = new Pair<>(p.getFirst(), new Pair<> (p.getSecond().getFirst(), references.get(p.getSecond().getSecond())));
				}
				if (p.getSecond().getSecond() == null && p.getSecond().getFirst() != null) {// just emptied
					references.put(p.getSecond().getFirst(), p.getFirst().getFirst());
					sides.put(p.getSecond().getFirst(), -1);
					p = new Pair<>(p.getFirst(), new Pair<> (null, p.getSecond().getSecond()));
				}
				conflictStack.push(p);
			}
			// side of e is side of a highest return edge
			if(lowPointers.get(e) < heights.get(u)) { // e has return edge
				E hL = conflictStack.peek().getFirst().getFirst();
				E hR = conflictStack.peek().getSecond().getFirst();
				if(hL != null && (hR == null || lowPointers.get(hL) > lowPointers.get(hR))) {
					references.put(e, hL);
				} else {
					references.put(e, hR);
				}
			}
		}
	}

	private int lowest(Pair<Pair<E>> p) {
		if(p.getFirst() == null) return lowPointers.get(p.getSecond().getFirst());
		if(p.getSecond() == null) return lowPointers.get(p.getFirst().getFirst());
		return Math.min(lowPointers.get(p.getSecond().getFirst()), lowPointers.get(p.getFirst().getFirst()));
	}

	private boolean conflicting(Pair<E> i, E b) {
		return i != null && lowPointers.get(i.getSecond()) > lowPointers.get(b);
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

	private void embedding() {
		for (E e  : graph.getEdges()) {
			nestingDepths.put(e, nestingDepths.get(e) * sign(e));
		}
		for (V s : roots) {
			dfs3(s);
		}	
	}
	
	private int sign(E e) {
		if(references.get(e) != null) {
			sides.put(e, sides.get(e) *  sign(references.get(e)));
			references.remove(e);
		}
		return sides.get(e);
	}
	
	private void dfs3(V v) {
		List<E> adjacent = adjacentOrderedByNestingDepth(v);
		for (E f : adjacent) {
			V w = directedGraph.getDest(f);
			if(f == parentEdges.get(w)) { // tree edge
				adjacent.remove(f);
				adjacent.add(0, f);
				leftReferences.put(v, f);
				rightReferences.put(v, f);
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
