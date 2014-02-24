package de.uniwue.smooth.draw;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Transforms the positions of an underlying {@link OrthogonalDrawing} using an {@link AffineTransform}.
 *  
 * @param <T> Type of the underlying drawing.
 */
public class TransformingOrthogonalDrawing<T> implements OrthogonalDrawing<T> {
	
	private OrthogonalDrawing<T> delegate;
	private AffineTransform transform;
	
	/**
	 * Initialize.
	 * @param delegate Decorated drawing.
	 * @param transform Transformation to apply to the coordinates.
	 */
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
	public void edgeMidpoint(Pair<Integer> position, String color) {
		delegate.edgeMidpoint(transform(position), color);
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
	public void label(Pair<Integer> position, String labelText) {
		delegate.label(transform(position), labelText);
	}

	@Override
	public void arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		delegate.arc(transform(from), transform(mid), transform(to));
	}

	@Override
	public void arc(Pair<Integer> from, Pair<Integer> to) {
		delegate.arc(transform(from), transform(to));
	}

	@Override
	public void newPage() {
		delegate.newPage();
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
