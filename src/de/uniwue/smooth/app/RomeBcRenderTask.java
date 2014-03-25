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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphReader;

public class RomeBcRenderTask implements Runnable {
	
	public static void main(String[] args) throws Exception {
		new RomeBcRenderTask().run();
	}

	@Override
	public void run() {
		Benchmark b = new Benchmark();
		
		List<String[]> filesCsvList;
		try {
			filesCsvList = new CSVReader(new FileReader("resources/rome_bc/degree-4-rm_duplicates.txt"), ';').readAll();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Set<String> interesting = new HashSet<>();
		Collections.addAll(interesting, "bc_grafo10182.33.lgr.graphml", "bc_grafo10187.33.lgr.graphml");
		
		OrthogonalDrawing<Appendable> drawing = createDrawing("resources/drawings/rome_bc/all.ipe");
		
		for (final String[] cvsLine : filesCsvList) {
			try {
				String filename = cvsLine[0];
				
				OrthogonalDrawing<Appendable> interesingDrawing = null;
				if(interesting.contains(filename)) {
					interesingDrawing =  createDrawing("resources/drawings/rome_bc/" + filename + ".ipe");
				}
				
				File file = new File("resources/rome_bc/" + filename);
				System.out.println(filename);
				drawing.label(new Pair<Integer>(-1, -1), "\\verb|" + filename + "|");
				GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
				Graph<Vertex, Edge> graph = graphReader.readGraph();
				
				OrthogonalLayout<Vertex, Edge> layout = new CompressingLiuEtAlLayout<Vertex, Edge>(graph);
				OrthogonalDrawer<Vertex, Edge> drawer = new SmoothOrthogonalDrawer<Vertex, Edge>();
				layout.initialize();
				drawer.draw(layout, drawing);
				if(interesingDrawing != null) {
					drawer.draw(layout, interesingDrawing);
					Util.writeFile("resources/drawings/rome_bc/" + filename + ".ipe",interesingDrawing.create().toString());
				}
				
				graphReader.close();
			} catch (Exception e) {
				if (!e.getLocalizedMessage().contains("vertex degree of 4"))
					e.printStackTrace();
			} finally {
				drawing.newPage();
			}
		}
		
		System.out.println("Generated " + filesCsvList.size() +  " drawings. ");
		Util.writeFile("resources/drawings/rome_bc/all.ipe", drawing.create().toString());
		b.print();
	}
	
	OrthogonalDrawing<Appendable> createDrawing(String fileName) {
		AffineTransform transform = new AffineTransform();
		transform.translate(400, 100);
		transform.scale(30, 30);
		
		return new TransformingOrthogonalDrawing<>(new OrthogonalIpeDrawing(new IpeDrawing()), transform);
	}
	
}
