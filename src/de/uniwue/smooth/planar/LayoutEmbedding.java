package de.uniwue.smooth.planar;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.comparators.ReverseComparator;
import org.apache.commons.collections15.comparators.TransformingComparator;

import de.uniwue.smooth.util.point2d.Point2DComparatorFactories;
import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.algorithms.layout.Layout;

/**
 * A planar embedding based on the node positions in an existing layout.
 * 
 * For this embedding it is assumed that the layout represents a planar drawing with
 * straight line edges.
 *
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class LayoutEmbedding<V, E> extends AdjacencyListEmbedding<V, E> implements Embedding<V, E> {

	Layout<V, E> layout;

	/**
	 * Build a layout based on a given embedding.
	 * @param layout A layout with the positions of the vertices in a planar straight line drawing.
	 */
	public LayoutEmbedding(Layout<V, E> layout) {
		super();
		this.layout = layout;
	}

	@Override
	protected List<E> getAdjacent(final V vertex) {
		final Point2D refPoint = layout.transform(vertex);
		List<E> adjacent = new ArrayList<E>(layout.getGraph().getIncidentEdges(vertex));
		Collections.sort(adjacent, new ReverseComparator<>(new TransformingComparator<>(new Transformer<E, Point2D>() {
			@Override
			public Point2D transform(E input) {
				Point2D inputPoint = layout.transform(getOpposite(vertex, input));
				return Point2DOperations.subtract(inputPoint, refPoint);
			}
		}, Point2DComparatorFactories.atan2())));
		return adjacent;
	}

	@Override
	protected V getOpposite(V vertex, E edge) {
		return layout.getGraph().getOpposite(vertex, edge);
	}

	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorOnOuterface() {
		V outerfaceVertex = Collections.min(layout.getGraph().getVertices(), new TransformingComparator<>(
				layout, Point2DComparatorFactories.y()));
		return getEmbeddingIteratorAtVertex(outerfaceVertex);
	}

	@Override
	public EmbeddingIterator<V, E> getEmbeddingIteratorAtEdge(E edge) {
		return getEmbeddingIterator(layout.getGraph().getIncidentVertices(edge).iterator().next(), edge);
	}
}
