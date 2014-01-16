package de.uniwue.smooth.app;

import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;

import de.uniwue.smooth.StOrdering;
import de.uniwue.smooth.UndirectedTransformer;
import de.uniwue.smooth.generate.CompleteKPartitGraphGenerator;
import de.uniwue.smooth.generate.SimplePlanarGraphGenerator;
import de.uniwue.smooth.palm.PalmTree;
import de.uniwue.smooth.planar.BrandesEmbedding;
import de.uniwue.smooth.planar.DoublyConnectedEdgeListEmbedding;
import de.uniwue.smooth.planar.Embedding;
import de.uniwue.smooth.planar.EmbeddingIterator;
import de.uniwue.smooth.planar.EmbeddingIteratorTools;
import de.uniwue.smooth.planar.NotPlanarException;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

@SuppressWarnings("unused")
public class App {

	public static void main(String[] args) {
		new App().main();
	}
	
	public void main() {
		
		//draw(Generators.wheel(3));
		//draw(Generators.hexahedron());
		//draw(Generators.octahedron());
		
		draw(Generators.k33());
		System.out.println(isPlanar(Generators.k33())); // FIXME: huh?
		// TODO: Liu et al.
		
		//romeStats();
	}
	
	public boolean isPlanar(UndirectedGraph<Vertex, Edge> graph) {
		return BrandesEmbedding.isPlanar(graph);
	}
	
	private DirectedGraph<Vertex, Edge> loadOctahedron() {
		DirectedSparseMultigraph<Vertex, Edge> octahedron = new DirectedSparseMultigraph<Vertex, Edge>();
		Vertex[] vs = {new Vertex(0), new Vertex(1), new Vertex(2), new Vertex(3), new Vertex(4), new Vertex(5)};
		for (Vertex v : vs) octahedron.addVertex(v);
		Edge[] es = {new Edge(0),new Edge(1),new Edge(2),new Edge(3),new Edge(4),new Edge(5),new Edge(6),new Edge(7),new Edge(8),new Edge(9),new Edge(10),new Edge(11)};
		octahedron.addEdge(es[ 0], vs[0], vs[1]);
		octahedron.addEdge(es[ 1], vs[0], vs[2]);
		octahedron.addEdge(es[ 2], vs[0], vs[3]);
		octahedron.addEdge(es[ 3], vs[0], vs[5]);
		octahedron.addEdge(es[ 4], vs[1], vs[2]);
		octahedron.addEdge(es[ 5], vs[1], vs[4]);
		octahedron.addEdge(es[ 6], vs[1], vs[5]);
		octahedron.addEdge(es[ 7], vs[2], vs[3]);
		octahedron.addEdge(es[ 8], vs[2], vs[4]);
		octahedron.addEdge(es[ 9], vs[3], vs[4]);
		octahedron.addEdge(es[10], vs[3], vs[5]);
		octahedron.addEdge(es[11], vs[4], vs[5]);
		System.out.println("The graph = " + octahedron.toString());
		
		MapSettableTransformer<Edge, Edge> nextEdgeSource = new MapSettableTransformer<Edge, Edge>(new HashMap<Edge, Edge>());
		nextEdgeSource.set(es[ 0], es[ 3]);
		nextEdgeSource.set(es[ 1], es[ 0]);
		nextEdgeSource.set(es[ 2], es[ 1]);
		nextEdgeSource.set(es[ 3], es[ 2]);
		nextEdgeSource.set(es[ 4], es[ 5]);
		nextEdgeSource.set(es[ 5], es[ 6]);
		nextEdgeSource.set(es[ 6], es[ 0]);
		nextEdgeSource.set(es[ 7], es[ 8]);
		nextEdgeSource.set(es[ 8], es[ 4]);
		nextEdgeSource.set(es[ 9], es[ 7]);
		nextEdgeSource.set(es[10], es[ 9]);
		nextEdgeSource.set(es[11], es[ 5]);
		MapSettableTransformer<Edge, Edge> nextEdgeDest = new MapSettableTransformer<Edge, Edge>(new HashMap<Edge, Edge>());
		nextEdgeDest.set(es[ 0], es[ 4]);
		nextEdgeDest.set(es[ 1], es[ 7]);
		nextEdgeDest.set(es[ 2], es[10]);
		nextEdgeDest.set(es[ 3], es[ 6]);
		nextEdgeDest.set(es[ 4], es[ 1]);
		nextEdgeDest.set(es[ 5], es[ 8]);
		nextEdgeDest.set(es[ 6], es[11]);
		nextEdgeDest.set(es[ 7], es[ 2]);
		nextEdgeDest.set(es[ 8], es[ 9]);
		nextEdgeDest.set(es[ 9], es[11]);
		nextEdgeDest.set(es[10], es[ 3]);
		nextEdgeDest.set(es[11], es[10]);
		Embedding<Vertex, Edge> octahedronEmbedding = new DoublyConnectedEdgeListEmbedding<Vertex, Edge>(octahedron, nextEdgeSource, nextEdgeDest, vs[0], es[3]);
		
		EmbeddingIterator<Vertex, Edge> embeddingIterator = octahedronEmbedding.getEmbeddingIteratorOnOuterface();
		System.out.println(EmbeddingIteratorTools.listAroundVertex(embeddingIterator));
		System.out.println(EmbeddingIteratorTools.listAroundFace(embeddingIterator));
		embeddingIterator.nextAroundVertex();
		System.out.println(EmbeddingIteratorTools.listAroundFace(embeddingIterator));
		embeddingIterator.nextAroundVertex();
		System.out.println(EmbeddingIteratorTools.listAroundFace(embeddingIterator));
		embeddingIterator.nextAroundVertex();
		System.out.println(EmbeddingIteratorTools.listAroundFace(embeddingIterator));
		embeddingIterator.nextAroundVertex();
		System.out.println(EmbeddingIteratorTools.listAroundFace(embeddingIterator));
		embeddingIterator.oppositeOnEdge();
		System.out.println(EmbeddingIteratorTools.listAroundFace(embeddingIterator));
		System.out.println(EmbeddingIteratorTools.listAroundVertex(embeddingIterator));
		
		UndirectedGraph<Vertex, Edge> undirectedOctahedron = new UndirectedTransformer<Vertex, Edge>(UndirectedSparseGraph.<Vertex, Edge>getFactory()).transform(octahedron);
		//PalmTree<Vertex, Edge> octahedronPalm = new PalmTree<Vertex, Edge>(undirectedOctahedron);
		StOrdering<Vertex, Edge> octahedronSorted = new StOrdering<Vertex, Edge>(undirectedOctahedron, vs[0], vs[1]);
		
		System.out.println(octahedronSorted.getList());
		
		return octahedron;
	}
	
	public void draw(Graph<Vertex, Edge> graph) {
		Layout<Vertex, Edge> layout = new CircleLayout<Vertex, Edge>(graph);
		layout.setSize(new Dimension(300,300));
		BasicVisualizationServer<Vertex, Edge> vv = new BasicVisualizationServer<Vertex, Edge>(layout);
		vv.setPreferredSize(new Dimension(350,350));
		
		JFrame frame = new JFrame("Octahedron Graph view");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private void romeStats() {
		final int POOL_SIZE = 8;
		ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
		
		final ConcurrentHashMap<Integer, AtomicInteger> edgeCounts = new ConcurrentHashMap<Integer, AtomicInteger>();
		final ConcurrentHashMap<Integer, AtomicInteger> vertexCounts = new ConcurrentHashMap<Integer, AtomicInteger>();
		final AtomicInteger counter = new AtomicInteger();
		final AtomicInteger exceptionCounter = new AtomicInteger();
		final AtomicInteger planarCounter = new AtomicInteger();
		
		File[] files =  new File("resources/rome").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.substring(0,5).equals("grafo");
			}
		});
		
		for (final File file : files) {
			executor.execute(new Runnable() {
				@Override public void run() {
					try {
						GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
						Graph<Vertex, Edge> graph = graphReader.readGraph();
						
						int edgeCount = graph.getEdgeCount();
						int vertexCount = graph.getVertexCount();
						edgeCounts.putIfAbsent(edgeCount, new AtomicInteger());
						edgeCounts.get(edgeCount).incrementAndGet();
						vertexCounts.putIfAbsent(vertexCount, new AtomicInteger());
						vertexCounts.get(vertexCount).incrementAndGet();
						
						if(isPlanar((UndirectedGraph<Vertex, Edge>) graph))
							planarCounter.incrementAndGet();
						
						Iterator<Vertex> it = graph.getVertices().iterator();
						try {
							new StOrdering<Vertex, Edge>((UndirectedGraph<Vertex, Edge>) graph, it.next(), it.next());
						} catch (Exception e) {
							exceptionCounter.incrementAndGet();
						}
						
						PalmTree<Vertex, Edge> palmTree = new PalmTree<>((UndirectedGraph<Vertex, Edge>) graph);
						if(palmTree.getSpanningTrees().getVertexCount() != graph.getVertexCount())
							throw new IllegalStateException("Vertex loss!?");
						
						if(palmTree.getSpanningTrees().getTrees().size() > 1) System.out.println("Not connected.");
						
						graphReader.close();
					} catch (GraphIOException e) {
						e.printStackTrace();
					}
					
					int count = counter.getAndIncrement();
					if(count % 500 == 0) {
						System.out.println(count);
					}
				}
			});
		}
		
		long start = System.currentTimeMillis();
		executor.shutdown(); // run!
		try {
			executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		
		System.out.println(counter);
		System.out.println("Stats in " + (end-start)/1000 + "s");
		System.out.println("Counts:" + files.length);
		System.out.println("Failed:" + exceptionCounter);
		System.out.println("Planar:" + planarCounter);
		System.out.println(new TreeMap<Integer, AtomicInteger>(vertexCounts));
		System.out.println(new TreeMap<Integer, AtomicInteger>(edgeCounts));
	}

}
