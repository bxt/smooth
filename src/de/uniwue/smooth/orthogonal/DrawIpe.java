package de.uniwue.smooth.orthogonal;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.util.OrthogonalIpeDrawing;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.util.Pair;

public class DrawIpe<V, E> implements Transformer<OrthogonalLayout<V, E>, String> {
	
	private OrthogonalLayout<V, E> layout;
	private OrthogonalIpeDrawing drawing;
	
	@Override
	public String transform(OrthogonalLayout<V, E> layout) {
		this.layout = layout;
		this.drawing = new OrthogonalIpeDrawing(new Pair<Integer>(10, 10));
		
		draw();
		String result = drawing.create();
		
		this.layout = null;
		this.drawing = null;
		
		return result;
	}
	
	private void draw() {
		drawVertices();
		drawEdges();
	}
	
	private void drawVertices() {
		for (V v : layout.getGraph().getVertices()) {
			Pair<Integer> coordinates = layout.getVertexLocation(v);
			drawing.vertex(coordinates);
		}
	}
	
	private void drawEdges() {
		for (final E e : layout.getGraph().getEdges()) {
			Pair<Pair<Integer>> edgeCoordinates = new Pair<>(CollectionUtils.collect(layout.getGraph().getEndpoints(e), new Transformer<V, Pair<Integer>>() {
				@Override
				public Pair<Integer> transform(V v) {
					return drawEdgeHalf(v, e);
				}
			}));
			
			drawing.line(edgeCoordinates);
			drawing.edgeMidpoint(layout.getEdgeLocation(e));
		}
	}
	
	private Pair<Integer> drawEdgeHalf(V v, E e) {
		Pair<Integer> vertexCoordinates = layout.getVertexLocation(v);
		Port port = Util.getKeyByValue(layout.getPortAssignment(v), e);
		Pair<Integer> portCoordinates = Util.add(vertexCoordinates, port.getDirection());
		Pair<Integer> edgeCoordinates = new Pair<Integer>(portCoordinates.getFirst(), layout.getEdgeLocation(e).getSecond());
		
		drawing.line(vertexCoordinates, portCoordinates);
		drawing.line(portCoordinates, edgeCoordinates);
		
		return edgeCoordinates;
	}
	
}