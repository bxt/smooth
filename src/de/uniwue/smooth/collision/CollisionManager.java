package de.uniwue.smooth.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uniwue.smooth.collision.geom.LineSegment;
import de.uniwue.smooth.collision.segments.Segment;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Manages a collection of curves that might collide with each other and new curves.
 */
public class CollisionManager {
	
	private List<CollisionBody<?>> domains = new ArrayList<>();
	
	/**
	 * Add a segment to the collection and return if it collides with another segment.
	 * @param segment
	 * @return
	 */
	public boolean addAndCollides(Segment segment) {
		boolean collides = collides(segment);
		add(segment);
		return collides;
	}
	
	/**
	 * Add some segments to the collection.
	 * @param segments The segments to add.
	 */
	public void addAll(Collection<Segment> segments) {
		for(Segment segment : segments) {
			add(segment);
		}
	}
	
	/**
	 * Add one segment to the collection.
	 * @param segment
	 */
	public void add(Segment segment) {
		Object body = segment.getBody();
		if ((body instanceof LineSegment)
				&& ((LineSegment) body).getFrom().equals(((LineSegment) body).getTo())) return;
		addDomain(body);
	}
	
	/**
	 * Check if the given segment collides with any of the segments in the collection.
	 * @param segment Segement to check.
	 * @return True, iff the segment collides with another one.
	 */
	public boolean collides(Segment segment) {
		Object body = segment.getBody();
		if ((body instanceof LineSegment)
				&& ((LineSegment) body).getFrom().equals(((LineSegment) body).getTo())) return false;
		return collidesDomain(body);
	}
	
	/**
	 * Check if any of the given segments collides with any of the segments in the collection.
	 * @param segments Segments to check.
	 * @return True, if one of the segments collides with one from this collection.
	 */
	public boolean collidesAny(Collection<Segment> segments) {
		boolean collidesAny = false;
		for(Segment segment : segments) {
			collidesAny = collidesAny || collides(segment);
		}
		return collidesAny;
	}
	
	private void addDomain(Object body) {
		domains.add(new CollisionBody<Object>(body));
	}
	
	private boolean collidesDomain(Object body) {
		new CollisionBody<Object>(body);
		CollisionBody<?> a = new CollisionBody<Object>(body);
		for (CollisionBody<?> b : domains) {
			/*if (a.collides(b) != b.collides(a))
				throw new IllegalStateException("Collision detection is asymmetric!");*/
			// TODO: make sure collisions are not asymmetric?
			if (a.collides(b) && b.collides(a))
				return true;
		}
		return false;
	}
	
	/**
	 * Check for collisions in the bodies already in the collection.
	 * @return A list of pairs of colliding bodies. 
	 */
	public List<Pair<CollisionBody<?>>> collisions() {
		List<Pair<CollisionBody<?>>> collisions = new ArrayList<>();
		for (int i = 0; i < domains.size(); i++) {
			for (int k = i + 1; k < domains.size(); k++) {
				CollisionBody<?> a = domains.get(i), b = domains.get(k);
				//if(a.collides(b) != b.collides(a)) throw new IllegalStateException("Collision detection is asymmetric!");
				if(a.collides(b) && b.collides(a)) {
					collisions.add(new Pair<>(a, b));
				}
			}
		}
		return collisions;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + collisions();
	}

}
