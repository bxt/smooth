package de.uniwue.smooth.collision.segments;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;

import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.util.Util;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * 
 *  
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class StraightlineEdgeGenerator<V, E> implements EdgeGenerator<V, E, StraightlineEdge> {
	
	OrthogonalLayout<V, E> layout;
	
	public StraightlineEdgeGenerator(OrthogonalLayout<V, E> layout) {
		this.layout = layout;
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
	 * 
	 */
	@Override
	public StraightlineEdge generateEdge(final E e) {
		final Collection<Segment> segments = new ArrayList<Segment>(2);
		
		Pair<Pair<Integer>> edgeCoordinates = new Pair<>(CollectionUtils.collect(layout.getGraph().getEndpoints(e), new Transformer<V, Pair<Integer>>() {
			@Override
			public Pair<Integer> transform(V v) {
				return drawEdgeHalf(v, e, segments);
			}
		}));
		
		segments.add(new Segment.Line(edgeCoordinates));
		
		return new StraightlineEdge(segments, layout.getEdgeLocation(e));
	}
	
	private Pair<Integer> drawEdgeHalf(V v, E e, Collection<Segment> segments) {
		Pair<Integer> vertexCoordinates = layout.getVertexLocation(v);
		Port port = Util.getKeyByValue(layout.getPortAssignment(v), e);
		Pair<Integer> portCoordinates = Util.add(vertexCoordinates, port.getDirection());
		Pair<Integer> edgeCoordinates = new Pair<Integer>(layout.getEdgeLocation(e).getFirst(), portCoordinates.getSecond());
		
		segments.add(new Segment.Line(vertexCoordinates, portCoordinates));
		segments.add(new Segment.Line(portCoordinates, edgeCoordinates));
		
		return edgeCoordinates;
	}
	

}