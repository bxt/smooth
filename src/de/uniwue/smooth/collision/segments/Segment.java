package de.uniwue.smooth.collision.segments;

import de.uniwue.smooth.collision.geom.CircleArc;
import de.uniwue.smooth.collision.geom.LineSegment;
import de.uniwue.smooth.draw.OrthogonalDrawing;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class Segment {
	
	public abstract <T> void draw(OrthogonalDrawing<T> orthogonalDrawing);
	
	public abstract <T> void draw(OrthogonalDrawing<T> orthogonalDrawing, boolean collides);
	
	public abstract Object getBody();
	
	public static class Line extends Segment {
		Pair<Pair<Integer>> endpoints;
	
		public Line(Pair<Pair<Integer>> endpoints) {
			super();
			this.endpoints = endpoints;
		}
	
		public Line(Pair<Integer> start, Pair<Integer> end) {
			super();
			this.endpoints = new Pair<Pair<Integer>>(start, end);
		}
	
		public Pair<Pair<Integer>> getEndpoints() {
			return endpoints;
		}

		@Override
		public <T> void draw(OrthogonalDrawing<T> orthogonalDrawing) {
			orthogonalDrawing.line(endpoints);
		}

		@Override
		public <T> void draw(OrthogonalDrawing<T> orthogonalDrawing,
				boolean collidesLine) {
			orthogonalDrawing.line(endpoints, collidesLine);
		}

		@Override
		public Object getBody() {
			return new LineSegment(getEndpoints());
		}
	}

	public static class Arc extends Segment {
		Pair<Integer> from;
		Pair<Integer> mid;
		Pair<Integer> to;

		public Arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
			super();
			this.from = from;
			this.mid = mid;
			this.to = to;
		}

		public Pair<Integer> getFrom() {
			return from;
		}

		public Pair<Integer> getMid() {
			return mid;
		}

		public Pair<Integer> getTo() {
			return to;
		}

		@Override
		public <T> void draw(OrthogonalDrawing<T> orthogonalDrawing) {
			orthogonalDrawing.arc(from, mid, to);
		}

		@Override
		public <T> void draw(OrthogonalDrawing<T> orthogonalDrawing,
				boolean collidesArc) {
			orthogonalDrawing.arc(from, mid, to, collidesArc);
		}

		@Override
		public Object getBody() {
			return CircleArc.getCircleArc(from, mid, to);
		}
	
	}

	public static class SemiArc extends Segment {
		Pair<Integer> from;
		Pair<Integer> to;
		
		public SemiArc(Pair<Integer> from, Pair<Integer> to) {
			super();
			this.from = from;
			this.to = to;
		}

		public Pair<Integer> getFrom() {
			return from;
		}

		public Pair<Integer> getTo() {
			return to;
		}

		public <T> void draw(OrthogonalDrawing<T> orthogonalDrawing) {
			orthogonalDrawing.arc(from, to);
		}

		@Override
		public <T> void draw(OrthogonalDrawing<T> orthogonalDrawing,
				boolean collidesArc) {
			orthogonalDrawing.arc(from, to, collidesArc);
		}

		@Override
		public Object getBody() {
			return CircleArc.getCircleArc(from, to);
		}
	}

}
