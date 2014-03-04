package de.uniwue.smooth.collison;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.util.Pair;

public class CollisionManager {
	
	private List<CollisionDomain> domains = new ArrayList<>();
	
	public void line(Pair<Integer> from, Pair<Integer> to) {
		domains.add(new LineSegment(from, to));
	}

	public void line(Pair<Pair<Integer>> endpoints) {
		domains.add(new LineSegment(endpoints));
	}

	public void arc(Pair<Integer> from, Pair<Integer> mid, Pair<Integer> to) {
		domains.add(new CircleArc(from, mid, to));
	}

	public void arc(Pair<Integer> from, Pair<Integer> to) {
		domains.add(new CircleArc(from, to));
	}
	
	private List<Pair<CollisionDomain>> collisions() {
		List<Pair<CollisionDomain>> collisions = new ArrayList<>();
		for (int i = 0; i < domains.size(); i++) {
			for (int k = 0; k < domains.size(); k++) {
				CollisionDomain a = domains.get(i), b = domains.get(k);
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
