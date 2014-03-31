package de.uniwue.smooth.app;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uniwue.smooth.collision.CollisionAvoidingSmoothLayout;
import de.uniwue.smooth.collision.CollisionAvoidingSmoothLayout.EscalationLevel;
import de.uniwue.smooth.draw.IpeDrawing;
import de.uniwue.smooth.draw.OrthogonalDrawer;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import de.uniwue.smooth.draw.OrthogonalIpeDrawing;
import de.uniwue.smooth.draw.SmoothOrthogonalDrawer;
import de.uniwue.smooth.draw.StraightlineOrthogonalDrawer;
import de.uniwue.smooth.draw.TransformingOrthogonalDrawing;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.LiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.palm.ListStOrdering;
import de.uniwue.smooth.palm.StOrdering;
import de.uniwue.smooth.planar.Embedding;
import de.uniwue.smooth.planar.LayoutEmbedding;
import de.uniwue.smooth.util.NotAnExecutorService;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;


public class ConflictingRenderTask implements Runnable {
	
	public static void main(String[] args) throws Exception {
		List<ConflictingRenderTask> tasks = Arrays.asList
				( new ConflictingRenderTask(30, 400, 400, "gates")
				, new ConflictingRenderTask(30, 400, 400, "three")
				, new ConflictingRenderTask(32, 384, 384, "exampleA", new int[]{0,1,2,3,4})
				);
		ExecutorService executor = new NotAnExecutorService(); Executors.newFixedThreadPool(8);
		for (Runnable task : tasks) executor.execute(task);
		executor.shutdown();
	}
		
	private double scale;
	private double translateX;
	private double translateY;
	private Graph<Vertex, Edge> graph;
	private String name;
	private StOrdering<Vertex, Edge> stOrdering = null;
	private Embedding<Vertex, Edge> embedding;
	
	public ConflictingRenderTask(double scale, double translateX, double translateY,
			String name) {
		this.scale = scale;
		this.translateX = translateX;
		this.translateY = translateY;
		this.name = name;
		loadGraphAndEmbedding();
	}

	private void loadGraphAndEmbedding() {
		File file = new File("resources/conflicting_graphs/" + name + ".graphml");
		GraphAndLayoutReader reader = new GraphAndLayoutReader(file);
		this.graph = reader.getGraph();
		this.embedding = new LayoutEmbedding<>(reader.getLayout());
	}
	
	public ConflictingRenderTask(double scale, double translateX, double translateY,
			String name, int[] preStOrdering) {
		this(scale, translateX, translateY, name);
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
		CompressingLiuEtAlLayout<Vertex, Edge> liuLayout = new CompressingLiuEtAlLayout<Vertex, Edge>(graph, embedding, stOrdering);
		Collection<OrthogonalLayout<Vertex, Edge>> layouts = Arrays.<OrthogonalLayout<Vertex, Edge>>asList(
		new LiuEtAlLayout<Vertex, Edge>(graph, embedding, stOrdering), liuLayout, new CollisionAvoidingSmoothLayout<Vertex, Edge>(liuLayout, EscalationLevel.CHEAP_ADJUSTMENTS), new CollisionAvoidingSmoothLayout<Vertex, Edge>(liuLayout, EscalationLevel.ALL_ADJUSTMENTS));
		Pair<OrthogonalDrawer<Vertex, Edge>> drawers = new Pair<OrthogonalDrawer<Vertex, Edge>>(
				new StraightlineOrthogonalDrawer<Vertex, Edge>(), new SmoothOrthogonalDrawer<Vertex, Edge>());
		for(OrthogonalLayout<Vertex, Edge> layout : layouts) {
			layout.initialize();
			AffineTransform transform = new AffineTransform();
			transform.translate(translateX, translateY);
			transform.scale(scale, scale);
			for(OrthogonalDrawer<Vertex, Edge> drawer : drawers) {
				if(layout.getClass() == LiuEtAlLayout.class && drawer.getClass() == SmoothOrthogonalDrawer.class) continue;
				if(layout.getClass() == CollisionAvoidingSmoothLayout.class && drawer.getClass() != SmoothOrthogonalDrawer.class) continue;
				IpeDrawing ipeDrawing = new IpeDrawing();
				OrthogonalDrawing<Appendable> drawing = new TransformingOrthogonalDrawing<>(new OrthogonalIpeDrawing(ipeDrawing), transform);
				drawer.draw(layout, drawing);
				String addition = (layout instanceof CollisionAvoidingSmoothLayout) ? "-" + (((CollisionAvoidingSmoothLayout<?,?>) layout).getEscalationLevel()) : "";
				Util.writeFile("resources/conflicting_graphs/drawings/"+name+"-"+layout.getClass().getSimpleName()+"-"+drawer.getClass().getSimpleName()+addition+".ipe", drawing.create().toString());
			}
		}
	}
}
