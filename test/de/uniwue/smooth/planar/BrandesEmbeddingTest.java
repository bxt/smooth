package de.uniwue.smooth.planar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;
import de.uniwue.smooth.app.Edge;
import de.uniwue.smooth.app.Generators;
import de.uniwue.smooth.app.GraphReaderFactory;
import de.uniwue.smooth.app.Vertex;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;

public class BrandesEmbeddingTest {

	@Test
	public void testIsPlanar() {
		assertFalse(BrandesEmbedding.isPlanar(Generators.matching(3))); // K_{3,3}
		assertFalse(BrandesEmbedding.isPlanar(Generators.k5())); // K_5
		
		assertFalse(BrandesEmbedding.isPlanar(Generators.matching(99))); // K_{99,99}
		assertFalse(BrandesEmbedding.isPlanar(Generators.completeGraph(99))); // K_99

		assertTrue(BrandesEmbedding.isPlanar(Generators.circle(99)));
		assertTrue(BrandesEmbedding.isPlanar(Generators.hexahedron()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.octahedron()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.path(99)));
		assertTrue(BrandesEmbedding.isPlanar(Generators.simplePlanarGraph()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.tetrahedron()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.triangle()));
		assertTrue(BrandesEmbedding.isPlanar(Generators.wheel(99)));
	}
	
	@Test @Ignore("Takes too long")
	public void romeCheck() {
		// meh, 3 from 11534:
		// [grafo169.26.gml, false]
		// [grafo2939.19.gml, false]
		// [grafo3418.49.gml, false]
		
		final int POOL_SIZE = 8;
		ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
		List<String[]> filesCsvList;
		try {
			filesCsvList = Util.subList(new CSVReader(new FileReader("resources/rome-results-final.csv"), ';').readAll(), 1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		final AtomicInteger counter = new AtomicInteger();
		final AtomicInteger wrongCounter = new AtomicInteger();
				
		for (final String[] cvsLine : filesCsvList) {
			executor.execute(new Runnable() {
				@Override public void run() {
					try {
						boolean expected = cvsLine[1].equals("true");
						String filename = cvsLine[0].replace(".gml", ".graphml");
						
						File file = new File("resources/rome/" + filename);
						GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
						Graph<Vertex, Edge> graph = graphReader.readGraph();
						
						if(BrandesEmbedding.isPlanar((UndirectedGraph<Vertex, Edge>) graph) != expected) {
							System.out.println(Arrays.toString(cvsLine));
							wrongCounter.incrementAndGet();
						}
						
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
		System.out.println("Counts:" + filesCsvList.size());
		System.out.println("Wrong :" + wrongCounter);
		assertEquals(0, wrongCounter.get());
	}
	
	@Test
	public void romeCheck169_26() {
		File file = new File("resources/rome/grafo169.26.graphml");
		GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
		Graph<Vertex, Edge> graph;
		try {
			graph = graphReader.readGraph();
		} catch (GraphIOException e) {
			throw new RuntimeException(e);
		}
		
		assertFalse(BrandesEmbedding.isPlanar((UndirectedGraph<Vertex, Edge>) graph));
	}
	
	@Test
	public void romeCheck2939_19() {
		File file = new File("resources/rome/grafo2939.19.graphml");
		GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
		Graph<Vertex, Edge> graph;
		try {
			graph = graphReader.readGraph();
		} catch (GraphIOException e) {
			throw new RuntimeException(e);
		}
		
		assertFalse(BrandesEmbedding.isPlanar((UndirectedGraph<Vertex, Edge>) graph));
	}
	
	@Test
	public void romeCheck3418_49() {
		File file = new File("resources/rome/grafo3418.49.graphml");
		GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
		Graph<Vertex, Edge> graph;
		try {
			graph = graphReader.readGraph();
		} catch (GraphIOException e) {
			throw new RuntimeException(e);
		}
		
		assertFalse(BrandesEmbedding.isPlanar((UndirectedGraph<Vertex, Edge>) graph));
	}
	
	
}
