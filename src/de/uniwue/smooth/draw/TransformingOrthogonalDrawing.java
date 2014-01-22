package de.uniwue.smooth.draw;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.util.Pair;

public class TransformingOrthogonalDrawing<T> implements OrthogonalDrawing<T> {
	
	private OrthogonalDrawing<T> delegate;
	private AffineTransform transform;

	public TransformingOrthogonalDrawing(OrthogonalDrawing<T> delegate,
			AffineTransform transform) {
		super();
		this.delegate = delegate;
		this.transform = transform;
	}

	@Override
	public void vertex(Pair<Integer> position) {
		delegate.vertex(transform(position));
	}

	@Override
	public void edgeMidpoint(Pair<Integer> position) {
		delegate.edgeMidpoint(transform(position));
	}

	@Override
	public void line(Pair<Integer> from, Pair<Integer> to) {
		delegate.line(transform(from), transform(to));
	}

	@Override
	public void line(Pair<Pair<Integer>> endpoints) {
		line(endpoints.getFirst(), endpoints.getSecond());
	}

	@Override
	public T create() {
		return delegate.create();
	}

	private Pair<Integer> transform(Pair<Integer> position) {
		Point2D point = new Point2D.Double(position.getFirst().doubleValue(), position.getSecond().doubleValue());
		transform.transform(point, point);
		return new Pair<Integer>((int)(point.getX()+0.5), (int)(point.getY()+0.5));
	}

}
