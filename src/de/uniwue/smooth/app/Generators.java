package de.uniwue.smooth.app;

import de.uniwue.smooth.generate.CircleGraphGenerator;
import de.uniwue.smooth.generate.CompleteKPartitGraphGenerator;
import de.uniwue.smooth.generate.HexahedronGraphGenerator;
import de.uniwue.smooth.generate.OctahedronGraphGenerator;
import de.uniwue.smooth.generate.PathGraphGenerator;
import de.uniwue.smooth.generate.SimplePlanarGraphGenerator;
import de.uniwue.smooth.generate.WheelGraphGenerator;
import edu.uci.ics.jung.graph.UndirectedGraph;

public abstract class Generators {
	
	public static UndirectedGraph<Vertex, Edge> octahedron() {
		UndirectedGraph<Vertex, Edge> graph = new OctahedronGraphGenerator<Vertex, Edge>(Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> hexahedron() {
		UndirectedGraph<Vertex, Edge> graph = new HexahedronGraphGenerator<Vertex, Edge>(Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> simplePlanarGraph() {
		UndirectedGraph<Vertex, Edge> graph = new SimplePlanarGraphGenerator<Vertex, Edge>(Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> k33() {
		return matching(3);
	}
	
	public static UndirectedGraph<Vertex, Edge> matching(int n) {
		int[] partitions = {n,n};
		UndirectedGraph<Vertex, Edge> graph = new CompleteKPartitGraphGenerator<Vertex, Edge>(true, partitions, Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> k5() {
		return completeGraph(5);
	}

	public static UndirectedGraph<Vertex, Edge> completeGraph(int n) {
		int[] partitions = new int[n];
		for(int i = 0; i < n; i++) {
			partitions[i] = 1;
		}
		UndirectedGraph<Vertex, Edge> graph = new CompleteKPartitGraphGenerator<Vertex, Edge>(true, partitions, Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}

	public static UndirectedGraph<Vertex, Edge> circle(int size) {
		UndirectedGraph<Vertex, Edge> graph = new CircleGraphGenerator<Vertex, Edge>(true, size, Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> triangle() {
		return circle(3);
	}
	
	public static UndirectedGraph<Vertex, Edge> wheel(int size) {
		UndirectedGraph<Vertex, Edge> graph = new WheelGraphGenerator<Vertex, Edge>(true, size, Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
	
	public static UndirectedGraph<Vertex, Edge> tetrahedron() {
		return wheel(3);
	}
	
	public static UndirectedGraph<Vertex, Edge> path(int size) {
		UndirectedGraph<Vertex, Edge> graph = new PathGraphGenerator<Vertex, Edge>(true, size, Vertex.getFactory(), Edge.getFactory()).create();
		System.out.println(graph);
		return graph;
	}
}
