package de.uniwue.smooth.app;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uniwue.smooth.draw.OrthogonalDrawer;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import de.uniwue.smooth.draw.OrthogonalIpeDrawing;
import de.uniwue.smooth.draw.TransformingOrthogonalDrawing;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.LiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.ImmutableTuple;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;

public class RenderTask implements Callable<String> {
	
	public static void main(String[] args) throws Exception {
		List<RenderTask> tasks = Arrays.asList
				( new RenderTask(15, 30, 30, Generators.wheel(3), "wheel_3")
				, new RenderTask(15, 30, 30, Generators.wheel(7), "wheel_7")
				, new RenderTask(15, 30, 30, Generators.wheel(99), "wheel_99")
				, new RenderTask(15, 30, 30, Generators.hexahedron(), "hexahedron")
				, new RenderTask(15, 30, 30, Generators.octahedron(), "octahedron")
				, new RenderTask(15, 30, 30, Generators.simplePlanarGraph(), "simplePlanarGraph")
				, new RenderTask(15, 30, 30, Generators.circle(3), "circle_3")
				, new RenderTask(15, 30, 30, Generators.circle(7), "circle_7")
				, new RenderTask(15, 30, 30, Generators.circle(99), "circle_99")
				, new RenderTask(15, 30, 30, getTestGraph(), "test")
				);
		ExecutorService executor = Executors.newFixedThreadPool(8);
		executor.invokeAll(tasks);
	}
	
	private static Graph<Vertex, Edge> getTestGraph() {
		File file = new File("resources/test.graphml");
		GraphReader<Graph<Vertex, Edge>, Vertex, Edge> graphReader = GraphReaderFactory.create(file);
		Graph<Vertex, Edge> graph;
		try {
			graph = graphReader.readGraph();
		} catch (GraphIOException e) {
			throw new RuntimeException(e);
		}
		return graph;
		
	}
	
	private double scale;
	private double translateX;
	private double translateY;
	private Graph<Vertex, Edge> graph;
	private String name;
	
	public RenderTask(double scale, double translateX, double translateY,
			Graph<Vertex, Edge> graph, String name) {
		super();
		this.scale = scale;
		this.translateX = translateX;
		this.translateY = translateY;
		this.graph = graph;
		this.name = name;
	}

	@Override
	public String call() throws Exception {
		Pair<OrthogonalLayout<Vertex, Edge>> layouts = new Pair<OrthogonalLayout<Vertex, Edge>>(
				new LiuEtAlLayout<Vertex, Edge>(graph), new CompressingLiuEtAlLayout<Vertex, Edge>(graph));
		for(OrthogonalLayout<Vertex, Edge> layout : layouts) {
			layout.initialize();
			AffineTransform transform = new AffineTransform();
			transform.scale(scale, scale);
			transform.translate(translateX, translateY);
			OrthogonalDrawing<String> drawing = new TransformingOrthogonalDrawing<>(new OrthogonalIpeDrawing(), transform);
			String ipeCode = new OrthogonalDrawer<Vertex, Edge, String>().transform(new ImmutableTuple<>(layout, drawing));
			Util.writeFile("resources/drawings/"+name+"-"+layout.getClass().getSimpleName()+".ipe", ipeCode);
		}
		return "Done :)";
	}
}
