package de.uniwue.smooth.collison;

import java.awt.geom.Point2D;
import java.util.Comparator;

import org.apache.commons.collections15.ComparatorUtils;

import de.uniwue.smooth.util.point2d.Point2DComparatorFactories;
import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents a filled 2D rectangle, aka bounding box.
 */
public class Box {
	
	private Point2D from;
	private Point2D to;

	public Box(Pair<Integer> from, Pair<Integer> to) {
		this(new Pair<>(from, to));
	}

	public Box(Pair<Pair<Integer>> endpoints) {
		this(Point2DOperations.fromIntegerPair(endpoints.getFirst() ),
			 Point2DOperations.fromIntegerPair(endpoints.getSecond()));
	}

	/**
	 * Create a box with two edge points.
	 * @param from One edge point.
	 * @param to Other edge point.
	 */
	public Box(Point2D from, Point2D to) {
		Comparator<Point2D> comparator = Point2DComparatorFactories.xy();
		this.from = ComparatorUtils.min(from, to, comparator);
		this.to   = ComparatorUtils.max(from, to, comparator);
	}
	
	/**
	 * The lower edge point.
	 * @return The lower left point.
	 */
	public Point2D getFrom() {
		return from;
	}

	/**
	 * The upper edge point.
	 * @return The upper right point.
	 */
	public Point2D getTo() {
		return to;
	}
	
	/**
	 * Check if a points lies on the line.
	 * @param point The point to check.
	 * @return If or not the point is on the line.
	 */
	public boolean contains(Point2D point) {
		return from.getX() < point.getX() && point.getX() < to.getX() &&
				(from.getY() < to.getY() ?
						(from.getY() < point.getY() && point.getY() < to.getY()) :
							(to.getY() < point.getY() && point.getY() < from.getY())); 
	}

	@Override
	public String toString() {
		return "Box [from=" + from + ", to=" + to + "]";
	}
}
