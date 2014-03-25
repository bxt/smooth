package de.uniwue.smooth.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uniwue.smooth.collision.geom.LineSegment;
import de.uniwue.smooth.collision.segments.Segment;
import edu.uci.ics.jung.graph.util.Pair;

public class CollisionManager {
	
	private List<CollisionDomain<?>> domains = new ArrayList<>();
	
	
	public boolean addAndCollides(Segment segment) {
		boolean collides = collides(segment);
		add(segment);
		return collides;
	}
	
	public void addAll(Collection<Segment> segments) {
		for(Segment segment : segments) {
			add(segment);
		}
	}
	
	public void add(Segment segment) {
		Object body = segment.getBody();
		if ((body instanceof LineSegment)
				&& ((LineSegment) body).getFrom().equals(((LineSegment) body).getTo())) return;
		addDomain(body);
	}
	
	public boolean collides(Segment segment) {
		Object body = segment.getBody();
		if ((body instanceof LineSegment)
				&& ((LineSegment) body).getFrom().equals(((LineSegment) body).getTo())) return false;
		return collidesDomain(body);
	}
	
	public boolean collidesAny(Collection<Segment> segments) {
		boolean collidesAny = false;
		for(Segment segment : segments) {
			collidesAny = collidesAny || collides(segment);
		}
		return collidesAny;
	}
	
	private void addDomain(Object body) {
		domains.add(new CollisionDomain<Object>(body));
	}
	
	private boolean collidesDomain(Object body) {
		new CollisionDomain<Object>(body);
		CollisionDomain<?> a = new CollisionDomain<Object>(body);
		for (CollisionDomain<?> b : domains) {
			/*if (a.collides(b) != b.collides(a))
				throw new IllegalStateException("Collision detection is asymmetric!");*/
			// TODO: make sure collisions are not asymmetric?
			if (a.collides(b) && b.collides(a))
				return true;
		}
		return false;
	}
	
	private List<Pair<CollisionDomain<?>>> collisions() {
		List<Pair<CollisionDomain<?>>> collisions = new ArrayList<>();
		for (int i = 0; i < domains.size(); i++) {
			for (int k = 0; k < domains.size(); k++) {
				CollisionDomain<?> a = domains.get(i), b = domains.get(k);
				if(a.collides(b) != b.collides(a)) throw new IllegalStateException("Collision detection is asymmetric!");
				if(a.collides(b)) {
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
