package de.uniwue.smooth;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.map.LazyMap;

import de.uniwue.smooth.planar.BrandesEmbedding;
import de.uniwue.smooth.planar.Embedding;
import de.uniwue.smooth.planar.EmbeddingIterator;
import de.uniwue.smooth.planar.EmbeddingTools;
import de.uniwue.smooth.planar.NotPlanarException;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class LiuAtAlLayout<V, E> extends AbstractLayout<V, E> implements Layout<V, E> {
	
	Embedding<V, E> embedding;
	StOrdering<V, E> stOrdering;
	DirectedGraph<V, E> directedGraph;
	
	private Map<V, Tier> vertexColumns = new HashMap<>();
	private Map<E, Tier> edgeColumns = new HashMap<>();
	private Map<V, Map<Port, E>> portAssignments = LazyMap.decorate(new HashMap<V, Map<Port, E>>(), new Factory<Map<Port, E>>() {
		@Override public Map<Port, E> create() {
			return new EnumMap<Port, E>(Port.class);
		}});
	private Tier initialTier = new Tier();
	
	public LiuAtAlLayout(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public void initialize() {
		Dimension d = getSize();
		
		UndirectedGraph<V, E> undirectedGraph = new UndirectedTransformer<V, E>(UndirectedSparseGraph.<V, E>getFactory()).transform(graph);
		
		try {
			embedding = new BrandesEmbedding<V, E>(undirectedGraph);
		} catch (NotPlanarException e) {
			throw new IllegalArgumentException("Can only layout planar graphs.", e);
		}
		EmbeddingIterator<V, E> embeddingIterator = embedding.getEmbeddingIteratorOnOuterface();
		V s = embeddingIterator.getVertex();
		embeddingIterator.nextAroundFace();
		V t = embeddingIterator.getVertex();
		stOrdering = new StOrdering<V, E>(undirectedGraph, s, t);
		List<V> vertexList = stOrdering.getList();
		
		for (int i = 0; i < vertexList.size(); i++) {
			V vetex = vertexList.get(i);
			Map<Port, E> portAssignment = portAssignments.get(vetex);
			Pair<List<E>> edgePartitions = edgePartitions(vetex);
			
			inPortAssignment(edgePartitions, portAssignment);
			outPortAssignment(edgePartitions, portAssignment);
			
			Tier tier = i == 0 ? initialTier : edgeColumns.get(portAssignment.get(Port.B));
			vertexColumns.put(vetex, tier);
			
			tierAssignment(edgePartitions, tier);
			
		}
		
		setTierCoordinates();
		
		if (d != null) {
			double width = d.getWidth() * 0.9;
			double height = d.getHeight() * 0.9;
			double xOffset = d.getWidth() * 0.05;
			double yOffset = d.getHeight() * 0.05;

			int i = 0;
			for (V v : vertexList) {
				Point2D coord = transform(v);

				double x = vertexColumns.get(v).coordinate / (double) getGraph().getVertices().size();
				double y = i / (double) getGraph().getVertices().size();

				coord.setLocation(x * width + xOffset, y * height + yOffset);
				
				i++;
			}
		}
	}
	
	private void inPortAssignment(Pair<List<E>> edgePartitions, Map<Port, E> portAssignment) {
		List<E> in = edgePartitions.getFirst();
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
	
	private void outPortAssignment(Pair<List<E>> edgePartitions, Map<Port, E> portAssignment) {
		List<E> out = edgePartitions.getSecond();
		if(out.size() == 0) {
			// nothing
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
	
	private void tierAssignment(Pair<List<E>> edgePartitions, Tier tier) {
		List<E> out = edgePartitions.getSecond();
		if(out.size() == 0) {
			// nothing
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
	
	private void setTierCoordinates() {
		Tier leftmost = initialTier;
		while (leftmost.prev != null) leftmost = leftmost.prev;
		for(int i = 1; leftmost != null; i++, leftmost = leftmost.next) leftmost.coordinate = i;
	}

	private Pair<List<E>> edgePartitions(V v) {
		List<List<E>> partitions = new ArrayList<>();
		List<Integer> partitionValues = new ArrayList<>();
		List<E> currentPartition = null;
		int lastComparator = 0;
		for (E e : EmbeddingTools.listAroundVertex(embedding, v)) {
			int comparator = Integer.signum(stOrdering.compare(v, getGraph().getOpposite(v, e)));
			if(comparator != lastComparator) {
				currentPartition = new ArrayList<>();
				partitions.add(currentPartition);
				partitionValues.add(comparator);
				lastComparator = comparator;
			}
			currentPartition.add(e);
		}
		if(partitions.size() == 0) {
			return new Pair<List<E>>(Collections.<E> emptyList(), Collections.<E> emptyList());
		} else if(partitions.size() == 1) {
			if(partitionValues.get(0) < 0) {
				return new Pair<List<E>>(Collections.<E> emptyList(), partitions.get(0));
			} else {
				return new Pair<List<E>>(partitions.get(0), Collections.<E> emptyList());
			}
		} else if(partitions.size() == 2) {
			if(partitionValues.get(0) < 0) {
				return new Pair<List<E>>(partitions.get(1), partitions.get(0));
			} else {
				return new Pair<List<E>>(partitions.get(0), partitions.get(1));
			}
		} else if(partitions.size() == 3) {
			if(partitionValues.get(0) < 0) {
				return new Pair<List<E>>(partitions.get(1), ListUtils.union(partitions.get(2), partitions.get(0)));
			} else {
				return new Pair<List<E>>(ListUtils.union(partitions.get(2), partitions.get(0)), partitions.get(1));
			}
		} else {
			throw new IllegalStateException("Bad partitoning.");
		}
	}
	
	private class Tier {
		public int coordinate;
		public Tier next;
		public Tier prev;
		
		@Override
		public String toString() {
			return coordinate + "~" + super.toString();
		}
		
		public Tier newLeftOf() {
			Tier newTier = new Tier();
			Tier prePrev = prev;
			if(prePrev!=null) prePrev.next = newTier;
			prev = newTier;
			newTier.next = this;
			newTier.prev = prePrev;
			return newTier;
		}
		
		public Tier newRightOf() {
			Tier newTier = new Tier();
			Tier preNext = next;
			if(preNext!=null) preNext.prev = newTier;
			next = newTier;
			newTier.prev = this;
			newTier.next = preNext;
			return newTier;
		}
	}
	
	private enum Port {L,R,T,B;}
	
	@Override
	public void reset() {
		initialize();
	}

}
