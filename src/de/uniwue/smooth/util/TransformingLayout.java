package de.uniwue.smooth.util;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

public class TransformingLayout<L extends Layout<V, E>, V, E> implements Layout<V, E> {
	
	private L delegate;
	private AffineTransform transform;
	
	public TransformingLayout(L delegate, AffineTransform transform) {
		super();
		this.delegate = delegate;
		this.transform = transform;
	}

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

	@Override
	public void setSize(Dimension d) {
		throw new UnsupportedOperationException("Set the size of the underlying layout!");
	}

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
