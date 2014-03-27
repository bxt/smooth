package de.uniwue.smooth.collision;

import java.awt.geom.Point2D;
import java.util.Collection;

import de.uniwue.smooth.collision.geom.CircleArc;
import de.uniwue.smooth.collision.geom.LineSegment;


public class CollisionDomain<T> {
	
	private T body;
	
	public CollisionDomain(T body) {
		this.body = body;
	}

	public boolean collides(CollisionDomain<?> domain) {
		return collisionOffset(domain) != null;
	}

	public Integer collisionOffset(CollisionDomain<?> domain) {
		if (domain.body instanceof CircleArc) {
			return collisionOffset((CircleArc) domain.body);
		} else if (domain.body instanceof LineSegment) {
			return collisionOffset((LineSegment) domain.body);
		} else {
			return collisionOffsetUnknownCombination(domain.body);
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
	
	private Integer collisionOffsetUnknownCombination(Object domainBody) {
		throw new UnsupportedOperationException(String.format(
				"Can not detect collisions between %s and %s.",
				this.body.getClass().getCanonicalName(),
				domainBody.getClass().getCanonicalName() ));
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
		return "CD{" + body + "}";
	}
}
