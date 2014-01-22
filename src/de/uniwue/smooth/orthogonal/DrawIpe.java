package de.uniwue.smooth.orthogonal;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.util.OrthogonalDrawing;
import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.Tuple;
import edu.uci.ics.jung.graph.util.Pair;

public class DrawIpe<V, E, T> implements Transformer<Tuple<OrthogonalLayout<V, E>, OrthogonalDrawing<T>>, T> {
	
	private OrthogonalLayout<V, E> layout;
	private OrthogonalDrawing<T> drawing;
	
	@Override
	public T transform(Tuple<OrthogonalLayout<V, E>, OrthogonalDrawing<T>> input) {
		this.layout = input.getFirst();
		this.drawing = input.getSecond();
		
		draw();
		T result = drawing.create();
		
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
		Pair<Integer> edgeCoordinates = new Pair<Integer>(layout.getEdgeLocation(e).getFirst(), portCoordinates.getSecond());
		
		drawing.line(vertexCoordinates, portCoordinates);
		drawing.line(portCoordinates, edgeCoordinates);
		
		return edgeCoordinates;
	}
	
}