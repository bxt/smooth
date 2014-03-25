package de.uniwue.smooth.app;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import de.uniwue.smooth.collision.CollisionAvoidingSmoothLayout;
import de.uniwue.smooth.draw.IpeDrawing;
import de.uniwue.smooth.draw.OrthogonalDrawer;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import de.uniwue.smooth.draw.OrthogonalIpeDrawing;
import de.uniwue.smooth.draw.SmoothOrthogonalDrawer;
import de.uniwue.smooth.draw.TransformingOrthogonalDrawing;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.util.Benchmark;
import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.ImmutableTuple;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;

public class RomeBcCollisionlessRenderTask implements Runnable {
	
	public static void main(String[] args) throws Exception {
		new RomeBcCollisionlessRenderTask().run();
	}

	@Override
	public void run() {
		
		try {
			System.in.read();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Benchmark b = new Benchmark();
		
		List<String[]> filesCsvList;
		try {
			filesCsvList = new CSVReader(new FileReader("resources/rome_bc/degree-4-rm_duplicates.txt"), ';').readAll();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Set<String> interesting = new HashSet<>();
		Collections.addAll(interesting, "bc_grafo1799.26.lgr.graphml", "bc_grafo1998.31.lgr.graphml", "bc_grafo2196.16.lgr.graphml", "bc_grafo2222.17.lgr.graphml", "bc_grafo2245.17.lgr.graphml"/*double C*/);
		
		OrthogonalDrawing<Appendable> drawing = createDrawing();
		
		for (final String[] cvsLine : filesCsvList) {
			try {
				String filename = cvsLine[0];
				
				OrthogonalDrawing<Appendable> interesingDrawing = null;
				if(interesting.contains(filename)) {
					interesingDrawing =  createDrawing();
				}
				
				File file = new File("resources/rome_bc/" + filename);
				System.out.println(filename);
				drawing.label(new Pair<Integer>(-1, -1), "\\verb|" + filename + "|");
				GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
				Graph<Vertex, Edge> graph = graphReader.readGraph();
				
				CompressingLiuEtAlLayout<Vertex, Edge> liuLayout = new CompressingLiuEtAlLayout<Vertex, Edge>(graph);
				OrthogonalLayout<Vertex, Edge> layout = new CollisionAvoidingSmoothLayout<Vertex, Edge>(liuLayout);
				OrthogonalDrawer<Vertex, Edge, Appendable> drawer = new SmoothOrthogonalDrawer<Vertex, Edge, Appendable>();
				layout.initialize();
				drawer.transform(new ImmutableTuple<>(layout, drawing));
				if(interesingDrawing != null) {
					drawer.transform(new ImmutableTuple<>(layout, interesingDrawing));
					Util.writeFile("resources/drawings/rome_bc/" + filename + "-nocollisions.ipe", interesingDrawing.create().toString());
				}
				
				graphReader.close();
			} catch (GraphIOException e) {
				throw new RuntimeException(e);
			/*} catch (Exception e) {
				e.printStackTrace();*/
			} finally {
				drawing.newPage();
			}
		}
		
		System.out.println("Generated " + filesCsvList.size() +  " drawings. ");
		Util.writeFile("resources/drawings/rome_bc/all-nocollisions.ipe", drawing.create().toString());
		b.print();
	}
	
	OrthogonalDrawing<Appendable> createDrawing() {
		AffineTransform transform = new AffineTransform();
		transform.translate(400, 100);
		transform.scale(30, 30);
		
		return new TransformingOrthogonalDrawing<>(new OrthogonalIpeDrawing(new IpeDrawing()), transform);
	}
	
}
