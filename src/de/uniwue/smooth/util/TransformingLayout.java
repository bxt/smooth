package de.uniwue.smooth.util;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

/**
 * Transforms the coordinates of a delegate layout.
 *
 * @param <L> Delegate layout type.
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class TransformingLayout<L extends Layout<V, E>, V, E> implements Layout<V, E> {
	
	private L delegate;
	private AffineTransform transform;
	
	/**
	 * Intialize a layout with a delegate and the transformation to apply.
	 * @param delegate Layout whose coordinates are transformed.
	 * @param transform Transformation to apply.
	 */
	public TransformingLayout(L delegate, AffineTransform transform) {
		super();
		this.delegate = delegate;
		this.transform = transform;
	}
	
	/**
	 * Access the underlying delegate.
	 * @return The original untransformed layout.
	 */
	public L getDelegate() {
		return delegate;
	}
	
	protected AffineTransform getTransform() {
		return transform;
	}

	@Override
	public Point2D transform(V input) {
		return transform.transform(delegate.transform(input), null);
	}

	@Override
	public void initialize() {
		delegate.initialize();
	}

	@Override
	public void setInitializer(Transformer<V, Point2D> initializer) {
		delegate.setInitializer(initializer);
	}

	@Override
	public void setGraph(Graph<V, E> graph) {
		delegate.setGraph(graph);
	}

	@Override
	public Graph<V, E> getGraph() {
		return delegate.getGraph();
	}

	@Override
	public void reset() {
		delegate.reset();
	}
	
	/**
	 * Not supported in this implementation. Use {@link #getDelegate()} and set the size
	 * of the unerlying layout.
	 */
	@Override
	public void setSize(Dimension d) {
		throw new UnsupportedOperationException("Set the size of the underlying layout!");
	}

	/**
	 * Not supported in this implementation. Use {@link #getDelegate()} and get the size
	 * of the unerlying layout.
	 */
	@Override
	public Dimension getSize() {
		throw new UnsupportedOperationException("Use the size of the underlying layout!");
	}

	@Override
	public void lock(V v, boolean state) {
		delegate.lock(v, state);
	}

	@Override
	public boolean isLocked(V v) {
		return delegate.isLocked(v);
	}

	@Override
	public void setLocation(V v, Point2D location) {
		try {
			delegate.setLocation(v, transform.inverseTransform(location, null));
		} catch (NoninvertibleTransformException e) {
			throw new UnsupportedOperationException("Set the location for the underlying layout or keep the transform invertible!", e);
		}
	}

}
