package de.uniwue.smooth.app;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.draw.OrthogonalDrawer;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import de.uniwue.smooth.draw.OrthogonalIpeDrawing;
import de.uniwue.smooth.draw.SmoothOrthogonalDrawer;
import de.uniwue.smooth.draw.TransformingOrthogonalDrawing;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.ImmutableTuple;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class GEdgeTest<T> implements Runnable {
	
	public static void main(String[] args) throws Exception {
		
		OrthogonalDrawing<String> drawing = new OrthogonalIpeDrawing();
		
		new GEdgeTest<String>(Port.B, Port.R, drawing, 700, 500, 1, 3).run();
		new GEdgeTest<String>(Port.B, Port.L, drawing, 700, 500,-1, 3).run();
		new GEdgeTest<String>(Port.T, Port.R, drawing, 300, 500, 1,-3).run();
		new GEdgeTest<String>(Port.T, Port.L, drawing, 300, 500,-1,-3).run();
		new GEdgeTest<String>(Port.B, Port.R, drawing, 700, 500, 3, 1).run();
		new GEdgeTest<String>(Port.B, Port.L, drawing, 300, 500,-3, 1).run();
		new GEdgeTest<String>(Port.T, Port.R, drawing, 700, 500, 3,-1).run();
		new GEdgeTest<String>(Port.T, Port.L, drawing, 300, 500,-3,-1).run();
		
		new GEdgeTest<String>(Port.L, Port.T, drawing, 700, 500, 1, 3).run();
		new GEdgeTest<String>(Port.R, Port.T, drawing, 700, 500,-1, 3).run();
		new GEdgeTest<String>(Port.L, Port.B, drawing, 300, 500, 1,-3).run();
		new GEdgeTest<String>(Port.R, Port.B, drawing, 300, 500,-1,-3).run();
		new GEdgeTest<String>(Port.L, Port.T, drawing, 700, 500, 3, 1).run();
		new GEdgeTest<String>(Port.R, Port.T, drawing, 300, 500,-3, 1).run();
		new GEdgeTest<String>(Port.L, Port.B, drawing, 700, 500, 3,-1).run();
		new GEdgeTest<String>(Port.R, Port.B, drawing, 300, 500,-3,-1).run();
		
		Util.writeFile("resources/drawings/G-edgetest.ipe", drawing.create());
	}
	
	private Port startPort;
	private Port endPort;
	private OrthogonalDrawing<T> outputDrawing;
	private double translateX;
	private double translateY;
	private int endX;
	private int endY;
	
	public GEdgeTest(Port startPort, Port endPort,
			OrthogonalDrawing<T> outputDrawing, double translateX,
			double translateY, int endX, int endY) {
		super();
		this.startPort = startPort;
		this.endPort = endPort;
		this.outputDrawing = outputDrawing;
		this.translateX = translateX;
		this.translateY = translateY;
		this.endX = endX;
		this.endY = endY;
	}

	private List<Vertex> vertices = Util.listFromFactory(Vertex.getFactory(), 2);
	private Edge edge = Edge.getFactory().create();
	private Graph<Vertex, Edge> graph = new DirectedSparseGraph<>();
	
	@Override
	public void run() {
		
		for (Vertex v : vertices) graph.addVertex(v);
		graph.addEdge(edge, vertices.get(0), vertices.get(1));
		
		
		OrthogonalLayout<Vertex, Edge> layout = new MyOrthogonalLayout();
		OrthogonalDrawer<Vertex, Edge, T> drawer = new SmoothOrthogonalDrawer<Vertex, Edge, T>();
		layout.initialize();
		AffineTransform transform = new AffineTransform();
		transform.translate(translateX, translateY);
		transform.scale(30, 30);
		OrthogonalDrawing<T> drawing = new TransformingOrthogonalDrawing<>(outputDrawing, transform);
		drawer.transform(new ImmutableTuple<>(layout, drawing));
	}
	
	private class MyOrthogonalLayout implements OrthogonalLayout<Vertex, Edge> {
		@Override public void initialize() {}
		@Override public void setInitializer(Transformer<Vertex, Point2D> initializer) {}
		@Override public void setGraph(Graph<Vertex, Edge> graph) {}
		@Override public void reset() {}
		@Override public void setSize(Dimension d) {}
		@Override public Dimension getSize() { return null; }
		@Override public void lock(Vertex v, boolean state) {}
		@Override public boolean isLocked(Vertex v) { return false; }
		@Override public void setLocation(Vertex v, Point2D location) {}
		@Override public Point2D transform(Vertex input) { return null; }
		@Override public Pair<Integer> getEdgeLocation(Edge e) { return null; }

		@Override
		public Graph<Vertex, Edge> getGraph() {
			return graph;
		}

		@Override
		public Pair<Integer> getVertexLocation(Vertex v) {
			if(v == vertices.get(0)) return new Pair<>(0, 0);
			if(v == vertices.get(1)) return new Pair<>(endX, endY);
			throw new IllegalStateException();
		}
		
		@Override
		public Map<Port, Edge> getPortAssignment(Vertex v) {
			if(v == vertices.get(0)) return createPortAssignment(startPort, edge);
			if(v == vertices.get(1)) return createPortAssignment(endPort, edge);
			throw new IllegalStateException();
		}

	}
	

	private static Map<Port, Edge> createPortAssignment(Port port, Edge edge) {
		Map<Port, Edge> portAssignment = new EnumMap<>(Port.class);
		portAssignment.put(port, edge);
		return portAssignment;
	}
}
