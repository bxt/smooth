package de.uniwue.smooth.collision.geom;



/**
 * Represents the space between two numbers on a coordinate axis.
 */
public class Interval {
	
	/**
	 * Create an interval between two points, inclusive.
	 * 
	 * @param from Starting point.
	 * @param to End point.
	 * @return Interval between these points.
	 */
	public static Interval getIntervalBetween(double from, double to) {
		return new Interval(from, to);
	}
	
	/**
	 * Create an interval from a point with a certain extend.
	 * @param from Starting point, inclusive.
	 * @param extend Maximum offset from starting point, inclusive.
	 * @return Interval at the given start with the given extend.
	 */
	public static Interval getIntervalFrom(double from, double extend) {
		return new Interval(from, from+extend);
	}

	
	/**
	 * Create an interval around a central value with a certain extend.
	 * @param center Central value.
	 * @param radius Extends of the interval on both sides of the center.
	 * @return Interval from <tt>center - radius</tt> to <tt>center + radius</tt>.
	 */
	public static Interval getIntervalCentered(double center, double radius) {
		return new Interval(center - radius, center + radius);
	}

	
	private double from;
	private double to;
	
	private Interval(double from, double to) {
		this.from = Math.min(from, to);
		this.to   = Math.max(from, to);
	}
	
	public double getFrom() {
		return from;
	}

	public double getTo() {
		return to;
	}

	public double getExtend() {
		return to - from;
	}
	
	/**
	 * Check if this interval is empty, i.e.
	 * it contains only one point,
	 * start and end points are equal
	 * and the extend is zero.
	 * 
	 * @return If or not this interval is empty.
	 */
	public boolean isEmpty() {
		return getExtend() == 0;
	}
	
	/**
	 * If or not this interval overlaps with another interval.
	 * @param interval The interval to test for intersections.
	 * @return If or not this interval overlaps with the given interval.
	 */
	public boolean intersects(Interval interval) {
		return intersect(interval) != null;
	}
	
	/**
	 * Get resulting interval when intersecting with another interval.
	 * @param interval Calculate overlap with this interval.
	 * @return The overlap or <tt>null</tt> if there is not overlap.
	 */
	public Interval intersect(Interval interval) {
		if(!interval.contains(getTo()) && !contains(interval.getTo())) return null;
		double intersectionFrom = contains(interval.getFrom()) ? interval.getFrom() : getFrom();
		double intersectionTo = contains(interval.getTo()) ? interval.getTo() : getTo();
		return Interval.getIntervalBetween(intersectionFrom, intersectionTo);
	}
	
	/**
	 * Get resulting interval when merging with another interval and filling the gap.
	 * @param interval Calculate union with this interval.
	 * @return The minimal interval containing both intervals.
	 */
	public Interval merge(Interval interval) {
		double unionFrom = Math.min(getFrom(), interval.getFrom());
		double unionTo = Math.max(getTo(), interval.getTo());
		return Interval.getIntervalBetween(unionFrom, unionTo);
	}
	
	/**
	 * Check if a point is in this interval or on its boundraies.
	 * @param angle The point to check.
	 * @return If or not the point is in this interval.
	 */
	public boolean contains(double point) {
		return getFrom() <= point && point <= getTo();
	}
	
	@Override
	public String toString() {
		return "Interval[" + getFrom()
				+ "+" + getExtend()
				+ "=" + getTo() + "]";
	}
	
}
