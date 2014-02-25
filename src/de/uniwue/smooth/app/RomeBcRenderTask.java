package de.uniwue.smooth.app;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
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
			filesCsvList = new CSVReader(new FileReader("resources/rome_bc/dir.txt"), ';').readAll();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		//List<String[]> filesCsvList = java.util.Arrays.asList(new String[]{"bc_grafo2173.21.lgr.graphml"}, new String[]{"bc_grafo2341.24.lgr.graphml"});

		AffineTransform transform = new AffineTransform();
		transform.translate(400, 100);
		transform.scale(30, 30);
		
		OrthogonalDrawing<String> drawing = new TransformingOrthogonalDrawing<>(new OrthogonalIpeDrawing(), transform);
		
		for (final String[] cvsLine : filesCsvList) {
			try {
				String filename = cvsLine[0];
				
				File file = new File("resources/rome_bc/" + filename);
				System.out.println(filename);
				drawing.label(new Pair<Integer>(-1, -1), "\\verb|" + filename + "|");
				GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
				Graph<Vertex, Edge> graph = graphReader.readGraph();
				
				OrthogonalLayout<Vertex, Edge> layout = new CompressingLiuEtAlLayout<Vertex, Edge>(graph);
				OrthogonalDrawer<Vertex, Edge, String> drawer = new SmoothOrthogonalDrawer<Vertex, Edge, String>();
				layout.initialize();
				drawer.transform(new ImmutableTuple<>(layout, drawing));
				
				graphReader.close();
				drawing.newPage();
			} catch (Exception e) {
				if (!e.getLocalizedMessage().contains("vertex degree of 4"))
					e.printStackTrace();
			}
		}
		
		System.out.println("Generated " + filesCsvList.size() +  " drawings. ");
		Util.writeFile("resources/drawings/rome_bc/all.ipe", drawing.create());
		b.print();
	}
	
	
	
}
