package de.uniwue.smooth.generate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Factory;

import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * Generate a complete k-partite graph.
 * 
 * A k-partite graph consists of k disjoint vertex sets such that
 * no two graph vertices within the same set are connected but
 * every pair of graph vertices in different sets are connected.
 * 
 * This factory may be parameterized by the number and cardinality
 * of the sets.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class CompleteKPartitGraphGenerator<V, E> implements Factory<UndirectedGraph<V, E>> {
	
	private boolean randomize;
	private int[] partitionSizes;
	private Factory<V> vertexFactory;
	private Factory<E> edgeFactory;
	
	/**
	 * Configure this graph generator.
	 * 
	 * @param randomize If or not the vertices are connected in a random order.
	 *   Useful for testing special cases.
	 * @param partitionSizes The sizes of the partitions, e.g. one partition 
	 *   of size 5 yields K_5 and two partition of size 3 yield K_{3,3}
	 * @param vertexFactory The factory to create the necessary vertices with.
	 * @param edgeFactory The factory to create the necessary edges with.
	 */
	public CompleteKPartitGraphGenerator(boolean randomize,
			int[] partitionSizes, Factory<V> vertexFactory,
			Factory<E> edgeFactory) {
		super();
		
		for(int i = 0; i < partitionSizes.length; i++)
			if(partitionSizes[i] < 0)
				throw new IllegalArgumentException("Partition sizes may not be negative."); 
		
		this.randomize = randomize;
		this.partitionSizes = partitionSizes; 
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
	}

	@Override
	public UndirectedGraph<V, E> create() {
		int vertexCount = 0;
		for (int s : partitionSizes) vertexCount += s;
		List<V> vertices = Util.listFromFactory(vertexFactory, vertexCount);
		if (randomize) Collections.shuffle(vertices);
		
		int edgeCount = 0;
		for (int s : partitionSizes) edgeCount += s * (vertexCount - s);
		edgeCount /= 2;
		List<E> edges    = Util.listFromFactory(edgeFactory  , edgeCount  );
		if (randomize) Collections.shuffle(edges);
		
		UndirectedGraph<V, E> graph = new UndirectedSparseGraph<>();
		for (V v : vertices) graph.addVertex(v);
		
		int partitionOffset = 0;
		int edgeIndex = 0;
		for(int p : partitionSizes) {
			int nextPartitionOffset = partitionOffset + p;
			for (int i = partitionOffset; i < nextPartitionOffset; i++) {
				for (int k = nextPartitionOffset; k < vertexCount; k++) {
					graph.addEdge(edges.get(edgeIndex++), vertices.get(i), vertices.get(k));
				}
			}
			partitionOffset = nextPartitionOffset;
		}
		
		return graph;
		
	}
	
}
