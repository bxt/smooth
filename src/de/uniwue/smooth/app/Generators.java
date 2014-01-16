package de.uniwue.smooth.app;

import de.uniwue.smooth.generate.CompleteKPartitGraphGenerator;
import de.uniwue.smooth.generate.OctahedronGraphGenerator;
import de.uniwue.smooth.generate.SimplePlanarGraphGenerator;
import edu.uci.ics.jung.graph.UndirectedGraph;

public abstract class Generators {
	
	public static UndirectedGraph<Vertex, Edge> octahedron() {
		UndirectedGraph<Vertex, Edge> graph = new OctahedronGraphGenerator<Vertex, Edge>(Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> simplePlanarGraph() {
		UndirectedGraph<Vertex, Edge> graph = new SimplePlanarGraphGenerator<Vertex, Edge>(Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> k33() {
		return knn(3);
	}
	
	public static UndirectedGraph<Vertex, Edge> knn(int n) {
		int[] partitions = {n,n};
		UndirectedGraph<Vertex, Edge> graph = new CompleteKPartitGraphGenerator<Vertex, Edge>(true, partitions, Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> k5() {
		return kn(5);
	}

	public static UndirectedGraph<Vertex, Edge> kn(int n) {
		int[] partitions = new int[n];
		for(int i = 0; i < n; i++) {
			partitions[i] = 1;
		}
		UndirectedGraph<Vertex, Edge> graph = new CompleteKPartitGraphGenerator<Vertex, Edge>(true, partitions, Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}

}
