package de.uniwue.smooth.collison;

public interface CollisionDomain {
	
	public abstract boolean collides(CollisionDomain domain);
	public abstract Integer collisionOffset(CollisionDomain domain);
	
}
