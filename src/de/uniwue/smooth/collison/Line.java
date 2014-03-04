package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;

import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

public class Line {
	
	private Point2D from;
	private Point2D to;

	public Line(Pair<Integer> from, Pair<Integer> to) {
		this(new Pair<>(from, to));
	}

	public Line(Pair<Pair<Integer>> endpoints) {
		this(Point2DOperations.fromIntegerPair(endpoints.getFirst() ),
			 Point2DOperations.fromIntegerPair(endpoints.getSecond()));
	}

	public Line(Point2D from, Point2D to) {
		this.from = from;
		this.to = to;
	}

	public Point2D getFrom() {
		return from;
	}

	public Point2D getTo() {
		return to;
	}
}
