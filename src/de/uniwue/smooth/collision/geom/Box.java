package de.uniwue.smooth.collision.geom;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.collections15.ComparatorUtils;

import de.uniwue.smooth.util.point2d.Point2DComparatorFactories;
import de.uniwue.smooth.util.point2d.Point2DOperations;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Represents a filled 2D rectangle, aka bounding box.
 */
public class Box implements Bounded {
	
	public static Box merge(Collection<? extends Box> boxes) {
		return Box.merge(boxes.toArray(new Box[]{}));
	}
	
	public static Box merge(Box... boxes) {
		if(boxes.length < 1) throw new IllegalArgumentException("Must at least merge one box!");
		Box mergedBox = null;
		for(Box box : boxes) {
			if(mergedBox == null) {
				mergedBox = box;
			} else {
				mergedBox = mergedBox.merge(box);
			}
		}
		return mergedBox;
	}
	
	/**
	 * The spanning point with the the lower x coordinate.
	 */
	private Point2D from;
	
	/**
	 * The spanning point with the the higher x coordinate.
	 */
	private Point2D to;

	public Box(Pair<Integer> from, Pair<Integer> to) {
		this(new Pair<>(from, to));
	}

	public Box(Interval xInterval, Interval yInterval) {
		this(new Point2D.Double(xInterval.getFrom(), yInterval.getFrom()),
		     new Point2D.Double(xInterval.getTo(),   yInterval.getTo()  ) );
	}

	public Box(Pair<Pair<Integer>> endpoints) {
		this(Point2DOperations.fromIntegerPair(endpoints.getFirst() ),
			 Point2DOperations.fromIntegerPair(endpoints.getSecond()));
	}
	
	/**
	 * Creates a box that only includes a single point.
	 * @param point2d The point the box should contain.
	 */
	public Box(Point2D point2d) {
		this(point2d, point2d);
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
	 * Get the interval that this box occupies on the x axis.
	 * @return Interval that this box occupies on the x axis.
	 */
	public Interval getXInterval() {
		return Interval.getIntervalBetween(from.getX(), to.getX());
	}
	
	/**
	 * Get the interval that this box occupies on the y axis.
	 * @return Interval that this box occupies on the y axis.
	 */
	public Interval getYInterval() {
		return Interval.getIntervalBetween(from.getY(), to.getY());
	}
	
	/**
	 * Return of or not the area of the box is zero, i.e. start end end point are equal.
	 * @return True iff the box is empty.
	 */
	public boolean isEmpty() {
		return getXInterval().isEmpty() && getYInterval().isEmpty();
	}
	
	/**
	 * Check if or not this box intersects another one.
	 * @param box The other box.
	 * @return True iff this box overlaps with the given box.
	 */
	public boolean intersects(Box box) {
		return intersect(box) != null;
	}
	
	/**
	 * Calculate the intersection with another box.
	 * @param box The other box.
	 * @return Intersection box between this box and the given box.
	 */
	public Box intersect(Box box) {
		Interval xIntersect = getXInterval().intersect(box.getXInterval());
		Interval yIntersect = getYInterval().intersect(box.getYInterval());
		if(xIntersect != null && yIntersect != null) {
			return new Box(xIntersect, yIntersect);
		} else {
			return null;
		}
	}
	
	/**
	 * Calculate the union with another box, i.e. the minimal box that contains both boxes.
	 * @param box Other box to include.
	 * @return The smallest box that includes this box and the given one.
	 */
	public Box merge(Box box) {
		Interval xUnion = getXInterval().merge(box.getXInterval());
		Interval yUnion = getYInterval().merge(box.getYInterval());
		return new Box(xUnion, yUnion);
	}
	
	/**
	 * Check if a points lies in this box, or on its boundaries.
	 * @param point The point to check.
	 * @return If or not the point is in the box.
	 */
	public boolean contains(Point2D point) {
		return getXInterval().contains(point.getX()) && getYInterval().contains(point.getY());
	}

	@Override
	public String toString() {
		return "Box [from=" + from + ", to=" + to + "]";
	}

	@Override
	public Box getBoundingBox() {
		return this;
	}
}
