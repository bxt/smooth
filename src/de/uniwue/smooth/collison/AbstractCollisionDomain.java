package de.uniwue.smooth.collison;


public abstract class AbstractCollisionDomain implements CollisionDomain {

	@Override
	public boolean collides(CollisionDomain domain) {
		return collisionOffset(domain) != null;
	}

	@Override
	public Integer collisionOffset(CollisionDomain domain) {
		if(domain instanceof AbstractCollisionDomain) {
			if (domain instanceof CircleArc) {
				return collisionOffset((CircleArc) domain);
			} else if (domain instanceof Line) {
				return collisionOffset((Line) domain);
			} else {
				return collisionOffsetUnknownCombination((AbstractCollisionDomain) domain);
			}
		} else {
			return collisionOffsetSubclass(domain);
		}
	}
	
	protected abstract Integer collisionOffset(CircleArc cirecleArc);
	protected abstract Integer collisionOffset(Line line);
	
	protected Integer collisionOffsetUnknownCombination(AbstractCollisionDomain domain) {
		throw new UnsupportedOperationException("Can not detect collisions between " + this.getClass().getCanonicalName() + " and " + domain.getClass().getCanonicalName());
	}
	
	protected Integer collisionOffsetSubclass(CollisionDomain domain) {
		throw new UnsupportedOperationException("Can only detect collisions for subclasses of " + AbstractCollisionDomain.class.getCanonicalName());
	}
	
}
