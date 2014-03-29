package de.uniwue.smooth.collision;

import java.awt.geom.Point2D;
import java.util.Collection;

import de.uniwue.smooth.collision.geom.CircleArc;
import de.uniwue.smooth.collision.geom.LineSegment;

/**
 * Represents a body/curve that can collide with some other body/curve.
 *
 * @param <T> Type of the contained body/line.
 */
public class CollisionBody<T> {
	
	private T body;
	
	/**
	 * Create a collision body for the given object.
	 * @param body The object this domain wraps.
	 */
	public CollisionBody(T body) {
		this.body = body;
	}

	/**
	 * If or not this body collides with another one.
	 * @param collisionBody Another collision body.
	 * @return True, if the bodies collide.
	 */
	public boolean collides(CollisionBody<?> collisionBody) {
		return collisionOffset(collisionBody) != null;
	}
	
	/**
	 * How far this body overlaps the other body.
	 * @param collisionBody The other body.
	 * @return How far the bodies overlap or <tt>null</tt> if they don't.
	 */
	public Integer collisionOffset(CollisionBody<?> collisionBody) {
		if (collisionBody.body instanceof CircleArc) {
			return collisionOffset((CircleArc) collisionBody.body);
		} else if (collisionBody.body instanceof LineSegment) {
			return collisionOffset((LineSegment) collisionBody.body);
		} else {
			return collisionOffsetUnknownCombination(collisionBody.body);
		}
	}
	
	private Integer collisionOffset(CircleArc cirecleArc) {
		if (body instanceof CircleArc) {
			return collisionOffset((CircleArc) body, cirecleArc);
		} else if (body instanceof LineSegment) {
			return collisionOffset((LineSegment) body, cirecleArc);
		} else {
			return collisionOffsetUnknownCombination(cirecleArc);
		}
	}
	private Integer collisionOffset(LineSegment lineSegment) {
		if (body instanceof CircleArc) {
			return nullsafeNegate(collisionOffset(lineSegment, (CircleArc) body));
		} else {
			return collisionOffset((LineSegment) body, lineSegment);
		}
	}
	
	private Integer collisionOffsetUnknownCombination(Object body) {
		throw new UnsupportedOperationException(String.format(
				"Can not detect collisions between %s and %s.",
				this.body.getClass().getCanonicalName(),
				body.getClass().getCanonicalName() ));
	}
	
	private static Integer collisionOffset(CircleArc cirecleArcA, CircleArc cirecleArcB) {
		Collection<Point2D> intersections = cirecleArcA.intersections(cirecleArcB);
		if (intersections == null) {
			return 1;
		} else {
			return intersections.size() > 0 ? 1 : null;
		}
	}
	
	private static Integer collisionOffset(LineSegment lineSegment, CircleArc cirecleArc) {
		Collection<Point2D> intersections = cirecleArc.intersections(lineSegment);
		return intersections.size() > 0 ? 1 : null;
	}
	
	private static Integer collisionOffset(LineSegment lineSegmentA, LineSegment lineSegmentB) {
		Collection<Point2D> intersections = lineSegmentA.intersections(lineSegmentB);
		if(intersections == null) return 1;
		if(intersections.size() == 0) return null;
		
		Point2D intersection = intersections.iterator().next();
		if(  ( intersection.equals(lineSegmentA.getFrom()) || intersection.equals(lineSegmentA.getTo()) )
		  && ( intersection.equals(lineSegmentB.getFrom()) || intersection.equals(lineSegmentB.getTo()) ) ) {
			return null; // ignore endpoint collision
		} else {
			return 1;
		}
	}
	
	private static Integer nullsafeNegate(Integer integer) {
		return integer == null ? null : -integer;
	}

	@Override
	public String toString() {
		return "CB{" + body + "}";
	}
}
