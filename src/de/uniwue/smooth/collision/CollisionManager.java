package de.uniwue.smooth.collision;

import java.util.ArrayList;
import java.util.List;

import de.uniwue.smooth.collision.geom.CircleArc;
import de.uniwue.smooth.collision.geom.LineSegment;
import edu.uci.ics.jung.graph.util.Pair;

public class CollisionManager {
	
	private List<CollisionDomain<?>> domains = new ArrayList<>();
	
	
	public void addLine(Pair<Integer> from, Pair<Integer> to) {
		if(from.equals(to)) return;
		addDomain(new LineSegment(from, to));
	}

	public void addLine(Pair<Pair<Integer>> endpoints) {
		addLine(endpoints.getFirst(), endpoints.getSecond());
	}

	public void addArc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		addDomain(CircleArc.getCircleArc(from, mid, to));
	}

	public void addArc(Pair<Integer> from, Pair<Integer> to) {
		addDomain(CircleArc.getCircleArc(from, to));
	}
	
	
	public boolean collidesLine(Pair<Integer> from, Pair<Integer> to) {
		if(from.equals(to)) return false;
		return collidesDomain(new LineSegment(from, to));
	}

	public boolean collidesLine(Pair<Pair<Integer>> endpoints) {
		return collidesLine(endpoints.getFirst(), endpoints.getSecond());
	}

	public boolean collidesArc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		return collidesDomain(CircleArc.getCircleArc(from, mid, to));
	}

	public boolean collidesArc(Pair<Integer> from, Pair<Integer> to) {
		return collidesDomain(CircleArc.getCircleArc(from, to));
	}
	
	
	public boolean addAndCollidesLine(Pair<Integer> from, Pair<Integer> to) {
		boolean collides = collidesLine(from, to);
		addLine(from, to);
		return collides;
	}

	public boolean addAndCollidesLine(Pair<Pair<Integer>> endpoints) {
		return addAndCollidesLine(endpoints.getFirst(), endpoints.getSecond());
	}

	public boolean addAndCollidesArc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		boolean collides = collidesArc(from, mid, to);
		addArc(from, mid, to);
		return collides;
	}

	public boolean addAndCollidesArc(Pair<Integer> from, Pair<Integer> to) {
		boolean collides = collidesArc(from, to);
		addArc(from, to);
		return collides;
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
