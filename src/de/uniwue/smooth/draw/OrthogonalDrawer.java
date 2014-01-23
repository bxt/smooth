package de.uniwue.smooth.draw;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.Tuple;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Performs the drawing of primitives into an {@link OrthogonalDrawing} given an {@link OrthogonalLayout}.
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 * @param <T> Type of resulting painting.
 */
public class OrthogonalDrawer<V, E, T> implements Transformer<Tuple<OrthogonalLayout<V, E>, OrthogonalDrawing<T>>, T> {
	
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
	
	/**
	 * Draws everything.
	 */
	private void draw() {
		drawVertices();
		drawEdges();
	}
	
	/**
	 * Draws the vertices, setting a marker at each vertex' position.
	 */
	private void drawVertices() {
		for (V v : layout.getGraph().getVertices()) {
			Pair<Integer> coordinates = layout.getVertexLocation(v);
			drawing.vertex(coordinates);
			drawing.label(coordinates, v.toString());
		}
	}
	
	/**
	 * Draws the edges with the following method:
	 * <ul>
	 *   <li>Start at one endpoint vertex.
	 *   <li>Move and draw 1 unit into the direction of the port
	 *   <li>Move and draw horizontally to the left-position of the edge midpoint
	 *   <li>Save that position
	 *   <li>Repeat these steps for the other endpoint vertex.
	 *   <li>Connect both saved positions with a vertical line.
	 * </ul>
	 */
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