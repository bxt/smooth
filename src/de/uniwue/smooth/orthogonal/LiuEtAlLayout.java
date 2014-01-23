package de.uniwue.smooth.orthogonal;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import de.uniwue.smooth.StOrdering;
import de.uniwue.smooth.UndirectedTransformer;
import de.uniwue.smooth.planar.BrandesEmbedding;
import de.uniwue.smooth.planar.Embedding;
import de.uniwue.smooth.planar.EmbeddingIterator;
import de.uniwue.smooth.planar.EmbeddingTools;
import de.uniwue.smooth.planar.NotPlanarException;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Layout a graph in an orthogonal grid. [Liu et al. 1997]
 *
 * @param <V>
 * @param <E>
 */
public class LiuEtAlLayout<V, E> extends AbstractLayout<V, E> implements OrthogonalLayout<V, E> {
	
	/**
	 * The embedding which is used for the edge ordering.
	 */
	private Embedding<V, E> embedding;
	
	/**
	 * The st ordering for the order of vertices.
	 */
	private StOrdering<V, E> stOrdering;
	/**
	 * Leftmost (left outer face) edge at s
	 */
	private E sLeftmost;
	/**
	 * Leftmost edge at t
	 */
	private E tLeftmost;
	
	/**
	 * Pointer into the column linked list
	 */
	private Tier initialTier = new Tier();
	
	/**
	 * Which row a vertex belongs to.
	 */
	private Map<V, Integer> vertexRows = new HashMap<>();
	/**
	 * Which column a vertex belongs to.
	 */
	private Map<V, Tier> vertexColumns = new HashMap<>();
	/**
	 * Which column an (open) edge belongs to.
	 */
	private Map<E, Tier> edgeColumns = new HashMap<>();
	/**
	 * Which edges are at which port for each vertex.
	 */
	private Map<V, Map<Port, E>> portAssignments = LazyMap.decorate(new HashMap<V, Map<Port, E>>(), new Factory<Map<Port, E>>() {
		@Override public Map<Port, E> create() {
			return new EnumMap<Port, E>(Port.class);
		}});
	
	public LiuEtAlLayout(Graph<V, E> graph) {
		super(graph);
	}
	
	/**
	 * Embed the whole graph into an orthogonal drawing.
	 */
	@Override
	public void initialize() {
		
		UndirectedGraph<V, E> undirectedGraph = new UndirectedTransformer<V, E>(UndirectedSparseGraph.<V, E>getFactory()).transform(graph);
		
		try {
			embedding = new BrandesEmbedding<V, E>(undirectedGraph);
		} catch (NotPlanarException e) {
			throw new IllegalArgumentException("Can only layout planar graphs.", e);
		}
		
		buildStOrdering(undirectedGraph);
		
		embedEdgesAndVertices();
		
		yCompression();
		
		initialTier.setTierCoordinates();
		
		calculateCoordinates();
	}
	

	/**
	 * The main step which does the placement of vertices and edges and the port assignments.
	 */
	private void embedEdgesAndVertices() {
		List<V> vertexList = stOrdering.getList();
		for (int i = 0; i < vertexList.size(); i++) {
			V vetex = vertexList.get(i);
			Map<Port, E> portAssignment = portAssignments.get(vetex);
			
			Pair<List<E>> edgePartitions = edgePartitions(vetex);
			
			inPortAssignment(edgePartitions.getFirst(), portAssignment);
			outPortAssignment(edgePartitions.getSecond(), portAssignment);
			
			Tier tier = i == 0 ? initialTier : edgeColumns.get(portAssignment.get(Port.B));
			vertexColumns.put(vetex, tier);
			
			tierAssignment(edgePartitions.getSecond(), tier);
		}
	}
	
	private void yCompression() { // TODO: remove columns of s-edges?
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
	 * @param setCounter Set number to assing to the vertices in this height.
	 * @param vertexSet Map of vertices to their set number.
	 * @param v Start vertex.
	 * @param direction Direction to find s-edges at.
	 */
	private void assignVertexSet(int setCounter, Map<V, Integer> vertexSet, V v, Port direction) {
		V u, w = v; E e;
		do {
			vertexSet.put(w, setCounter);
			u = w;
			e = portAssignments.get(u).get(direction);
			if(e != null) w = getGraph().getOpposite(u, e);
		} while (e != null && e.equals(portAssignments.get(w).get(direction.getOpposite())));
	}
	
	/**
	 * Calculates the coordinates of the vertices, only used for {@link Layout} compatibility.
	 */
	private void calculateCoordinates() {
		Dimension d = getSize();
		if (d != null) {
			double width = d.getWidth() * 0.9;
			double height = d.getHeight() * 0.9;
			double xOffset = d.getWidth() * 0.05;
			double yOffset = d.getHeight() * 0.05;

			for (V v : stOrdering.getList()) {
				Point2D coord = transform(v);

				double x = vertexColumns.get(v).getCoordinate() / (double) getGraph().getVertices().size();
				double y = vertexRows.get(v) / (double) getGraph().getVertices().size();

				coord.setLocation(x * width + xOffset, y * height + yOffset);
			}
		}
	}
	
	/**
	 * Intialize the st ordering, also chose s and t and caluclate the leftmost edges.
	 * @param undirectedGraph Undirected version of the input graph.
	 */
	private void buildStOrdering(UndirectedGraph<V, E> undirectedGraph) {
		EmbeddingIterator<V, E> embeddingIterator = embedding.getEmbeddingIteratorOnOuterface();
		
		V s = embeddingIterator.getVertex();
		sLeftmost = embeddingIterator.getEdge();
		
		embeddingIterator.nextAroundFace();
		
		V t = embeddingIterator.getVertex();
		tLeftmost = embeddingIterator.getEdge();
		
		stOrdering = new StOrdering<V, E>(undirectedGraph, s, t);
		vertexRows = new HashMap<>(stOrdering.asNumbers());
	}
	
	/**
	 * Assign ports for the incoming edges depending on indegree and order in the adjacency list embedding.
	 * @param in Adjacency list of incoming edges.
	 * @param portAssignment Map to store the assinment into.
	 */
	private void inPortAssignment(List<E> in, Map<Port, E> portAssignment) {
		if(in.size() == 0) {
			// nothing
		} else if(in.size() == 1) {
			portAssignment.put(Port.B, in.get(0));
		} else if(in.size() == 2) {
			portAssignment.put(Port.L, in.get(1));
			portAssignment.put(Port.B, in.get(0));
		} else if(in.size() == 3) {
			portAssignment.put(Port.L, in.get(2));
			portAssignment.put(Port.B, in.get(1));
			portAssignment.put(Port.R, in.get(0));
		} else if(in.size() == 4) {
			portAssignment.put(Port.T, in.get(3));
			portAssignment.put(Port.L, in.get(2));
			portAssignment.put(Port.B, in.get(1));
			portAssignment.put(Port.R, in.get(0));
		} else {
			throw new IllegalStateException("Degree too high!");
		}
	}
	
	/**
	 * Assign ports for the outgoing edges depending on outdegree and order in the adjacency list embedding.
	 * @param out Adjacency list of outgoing edges.
	 * @param portAssignment Map to store the assinment into.
	 */
	private void outPortAssignment(List<E> out, Map<Port, E> portAssignment) {
		if(out.size() == 0) {
			// nothing to assign.
		} else if(out.size() == 1) {
			portAssignment.put(Port.T, out.get(0));
		} else if(out.size() == 2) {
			portAssignment.put(Port.T, out.get(0));
			portAssignment.put(Port.R, out.get(1));
		} else if(out.size() == 3) {
			portAssignment.put(Port.L, out.get(0));
			portAssignment.put(Port.T, out.get(1));
			portAssignment.put(Port.R, out.get(2));
		} else if(out.size() == 4) {
			portAssignment.put(Port.L, out.get(0));
			portAssignment.put(Port.T, out.get(1));
			portAssignment.put(Port.R, out.get(2));
			portAssignment.put(Port.B, out.get(3));
		} else {
			throw new IllegalStateException("Degree too high!");
		}
	}
	
	/**
	 * Assign columns for the new open edges.
	 * @param out List of outgoing edges.
	 * @param tier Column of source vertex.
	 */
	private void tierAssignment(List<E> out, Tier tier) {
		if(out.size() == 0) {
			// nothing to assign.
		} else if(out.size() == 1) {
			edgeColumns.put(out.get(0), tier);
		} else if(out.size() == 2) {
			edgeColumns.put(out.get(0), tier);
			edgeColumns.put(out.get(1), tier.newRightOf());
		} else if(out.size() == 3) {
			edgeColumns.put(out.get(0), tier.newLeftOf());
			edgeColumns.put(out.get(1), tier);
			edgeColumns.put(out.get(2), tier.newRightOf());
		} else if(out.size() == 4) {
			Tier right = tier.newRightOf();
			edgeColumns.put(out.get(0), tier.newLeftOf());
			edgeColumns.put(out.get(1), tier);
			edgeColumns.put(out.get(2), right);
			edgeColumns.put(out.get(3), right.newRightOf());
		} else {
			throw new IllegalStateException("Degree too high!");
		}
	}
	
	/**
	 * Get the list of incoming and the list of outgoing edges at a certain vertex.
	 * 
	 * Determines if an edge is incoming or outgoing using the st odering, checks
	 * if the edges are prepartitioned nicely and returns the two lists.
	 * For s (and t), where there are only outgoing (incoming) edges, also makes sure
	 * that the leftmost edges are at the start of the list.
	 *  
	 * @param v The vertex whose adjacency list is to be partitioned.
	 * @return A pair of incoming and outgoing list of edges.
	 */
	private Pair<List<E>> edgePartitions(V v) {
		List<E> adjacent = EmbeddingTools.listAroundVertex(embedding, v);
		if(adjacent.size() > 4)
			throw new IllegalArgumentException("Can only layout graphs with a maximum vertex degree of 4.");
		
		List<List<E>> partitions = new ArrayList<>();
		List<Integer> partitionValues = new ArrayList<>();
		
		// Figure if edge is incoming or outgoing and preorder in up to three lists accordingly
		List<E> currentPartition = null;
		int lastComparator = 0;
		for (E e : adjacent) {
			int comparator = Integer.signum(stOrdering.compare(v, getGraph().getOpposite(v, e)));
			if(comparator != lastComparator) {
				currentPartition = new ArrayList<>();
				partitions.add(currentPartition);
				partitionValues.add(comparator);
				lastComparator = comparator;
			}
			currentPartition.add(e);
		}
		
		// Merge the up to three lists to the two partitions
		if(partitions.size() == 0) {
			return new Pair<List<E>>(Collections.<E> emptyList(), Collections.<E> emptyList());
		} else if(partitions.size() == 1) { // only incoming or only outgoing edges
			if(partitionValues.get(0) < 0) { // s
				// currently the rotation at s is not necessary b/c of the way we do the st ordering, but add it anyway for later and symmetry
				return new Pair<List<E>>(Collections.<E> emptyList(), rotateToFrontOfList(partitions.get(0), sLeftmost));
			} else { // t
				return new Pair<List<E>>(rotateToFrontOfList(partitions.get(0), tLeftmost), Collections.<E> emptyList());
			}
		} else if(partitions.size() == 2) { // some incoming, some outgoing edges
			if(partitionValues.get(0) < 0) {
				return new Pair<List<E>>(partitions.get(1), partitions.get(0));
			} else {
				return new Pair<List<E>>(partitions.get(0), partitions.get(1));
			}
		} else if(partitions.size() == 3) { // some incoming, some outgoing edges, too but split was somewhere in the middle
			if(partitionValues.get(0) < 0) {
				return new Pair<List<E>>(partitions.get(1), ListUtils.union(partitions.get(2), partitions.get(0)));
			} else {
				return new Pair<List<E>>(ListUtils.union(partitions.get(2), partitions.get(0)), partitions.get(1));
			}
		} else {
			// The incoming and outgoing edges may not be wierdly mixed. [R. Tamassia & I.G. Tollis 1986]
			throw new IllegalStateException("Bad partitoning.");
		}
	}
	
	/**
	 * Rotate the list so that the given element is at its head.
	 * 
	 * Modifies the list in place but also returns it for convenience.
	 * 
	 * @param list List to be rotated.
	 * @param element Element to be at index 0 afterwards.
	 * @return The input list.
	 */
	private <T> List<T> rotateToFrontOfList(List<T> list, T element) {
		int index = list.indexOf(element);
		if (index < 0) throw new IllegalStateException("Element not contained in list to rotate!");
		Collections.rotate(list, -index);
		return list;
	}
	
	@Override
	public void reset() {
		initialize();
	}

	@Override
	public Pair<Integer> getVertexLocation(V v) {
		return new Pair<Integer>(vertexColumns.get(v).getCoordinate(), vertexRows.get(v));
	}

	@Override
	public Pair<Integer> getEdgeLocation(E e) {
		return new Pair<Integer>(edgeColumns.get(e).getCoordinate(), vertexRows.get(Collections.min(getGraph().getEndpoints(e), stOrdering)));
	}

	@Override
	public Map<Port, E> getPortAssignment(V v) {
		return portAssignments.get(v);
	}

}
