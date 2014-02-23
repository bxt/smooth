package de.uniwue.smooth.app;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uniwue.smooth.draw.OrthogonalDrawer;
import de.uniwue.smooth.draw.StraightlineOrthogonalDrawer;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import de.uniwue.smooth.draw.OrthogonalIpeDrawing;
import de.uniwue.smooth.draw.SmoothOrthogonalDrawer;
import de.uniwue.smooth.draw.TransformingOrthogonalDrawing;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.LiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.palm.ListStOrdering;
import de.uniwue.smooth.palm.StOrdering;
import de.uniwue.smooth.util.NotAnExecutorService;
import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.ImmutableTuple;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphReader;

public class RenderTask implements Runnable {
	
	public static void main(String[] args) throws Exception {
		List<RenderTask> tasks = Arrays.asList
				( new RenderTask(100, 400, 600, Generators.circle(3), "circle_3")
				, new RenderTask( 60, 400, 500, Generators.circle(7), "circle_7")
				, new RenderTask(  5, 400, 480, Generators.circle(99), "circle_99")
				, new RenderTask( 30, 400, 600, Generators.hexahedron(), "hexahedron")
				, new RenderTask( 30, 400, 600, Generators.octahedron(), "octahedron")
				, new RenderTask(100, 400, 600, Generators.simplePlanarGraph(), "simplePlanarGraph")
				, new RenderTask( 30, 400, 500, getTestGraph(), "test")
				, new RenderTask(100, 300, 600, Generators.wheel(3), "wheel_3")
				, new RenderTask( 80, 300, 550, Generators.wheel(4, false), "wheel_4")
				, new RenderTask( 80, 300, 550, Generators.wheel(4, false), "wheel_4.customSt", new int[]{0, 1, 3, 2, 4})
				);
		ExecutorService executor = new NotAnExecutorService(); Executors.newFixedThreadPool(8);
		for (Runnable task : tasks) executor.execute(task);
		executor.shutdown();
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
	private StOrdering<Vertex, Edge> stOrdering = null;
	
	public RenderTask(double scale, double translateX, double translateY,
			Graph<Vertex, Edge> graph, String name) {
		this.scale = scale;
		this.translateX = translateX;
		this.translateY = translateY;
		this.graph = graph;
		this.name = name;
	}

	public RenderTask(double scale, double translateX, double translateY,
			Graph<Vertex, Edge> graph, String name, int[] preStOrdering) {
		this(scale, translateX, translateY, graph, name);
		this.stOrdering = makeStOrdering(preStOrdering, graph);
	}

	private static StOrdering<Vertex, Edge> makeStOrdering(int[] preStOrdering, Graph<Vertex, Edge> graph) {
		List<Vertex> vertexInList = new ArrayList<>(graph.getVertices());
		List<Vertex> vertexOutList = new ArrayList<>();
		for(int index : preStOrdering) vertexOutList.add(vertexInList.get(index));
		if(new HashSet<>(vertexOutList).size() != vertexInList.size()) throw new IllegalStateException("Mising vertices in custom st order");
		return new ListStOrdering<>(vertexOutList);
	}

	@Override
	public void run() {
		/*Collection<OrthogonalLayout<Vertex, Edge>> layouts = new Pair<OrthogonalLayout<Vertex, Edge>>(
				new LiuEtAlLayout<Vertex, Edge>(graph, stOrdering), new CompressingLiuEtAlLayout<Vertex, Edge>(graph, stOrdering));*/
		Collection<OrthogonalLayout<Vertex, Edge>> layouts = Collections.<OrthogonalLayout<Vertex, Edge>>singleton(new CompressingLiuEtAlLayout<Vertex, Edge>(graph, stOrdering));
		Pair<OrthogonalDrawer<Vertex, Edge, String>> drawers = new Pair<OrthogonalDrawer<Vertex, Edge, String>>(
				new StraightlineOrthogonalDrawer<Vertex, Edge, String>(), new SmoothOrthogonalDrawer<Vertex, Edge, String>());
		for(OrthogonalLayout<Vertex, Edge> layout : layouts) {
			layout.initialize();
			AffineTransform transform = new AffineTransform();
			transform.translate(translateX, translateY);
			transform.scale(scale, scale);
			for(OrthogonalDrawer<Vertex, Edge, String> drawer : drawers) {
				if(layout.getClass() == LiuEtAlLayout.class && drawer.getClass() == SmoothOrthogonalDrawer.class) continue;
				OrthogonalDrawing<String> drawing = new TransformingOrthogonalDrawing<>(new OrthogonalIpeDrawing(), transform);
				String ipeCode = drawer.transform(new ImmutableTuple<>(layout, drawing));
				Util.writeFile("resources/drawings/"+name+"-"+layout.getClass().getSimpleName()+"-"+drawer.getClass().getSimpleName()+".ipe", ipeCode);
			}
		}
	}
}
