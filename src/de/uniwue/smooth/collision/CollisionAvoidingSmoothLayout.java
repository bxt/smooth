package de.uniwue.smooth.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uniwue.smooth.collision.segments.Segment;
import de.uniwue.smooth.collision.segments.SmoothEdge;
import de.uniwue.smooth.collision.segments.SmoothEdgeGenerator;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.orthogonal.Quadrant;
import de.uniwue.smooth.util.Util;
import de.uniwue.smooth.util.tuples.MutablePair;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A smooth layout that adjusts the positions of the vertices, such that no edges overlap.
 * 
 * @param <V> Vertex type.
 * @param <E> Edge type.
 */
public class CollisionAvoidingSmoothLayout<V, E> extends AbstractLayout<V, E> implements OrthogonalLayout<V, E> {
	
	private static int MAXIMUM_MOVING_DISTANCE = 10000;
	
	private SmoothEdgeGenerator<V, E> edgeGenerator = new SmoothEdgeGenerator<V, E>(this);
	
	private CompressingLiuEtAlLayout<V, E> liuLayout;
	private EscalationLevel escalationLevel;
	
	private Map<V, Integer> vertexColumns = null;
	private Map<E, Integer> edgeColumns = null;
	private Map<Set<V>, Integer> setHeights = null;
	private Map<V, Set<V>> vertexSetMap = null;
	private int currentHeight = 0;
	
	public CollisionAvoidingSmoothLayout(CompressingLiuEtAlLayout<V, E> liuLayout) {
		this(liuLayout, EscalationLevel.getHightest());
	}
	
	public CollisionAvoidingSmoothLayout(CompressingLiuEtAlLayout<V, E> liuLayout, EscalationLevel escalationLevel) {
		super(new UndirectedSparseGraph<V, E>());
		this.liuLayout = liuLayout;
		this.escalationLevel = escalationLevel;
	}
	
	@Override
	public void initialize() {
		
		liuLayout.initialize();
		
		vertexColumns = new HashMap<V, Integer>();
		edgeColumns = new HashMap<E, Integer>();
		currentHeight = 0;
		
		List<Set<V>> vertexSets = liuLayout.getSets();
		
		setHeights = new HashMap<Set<V>, Integer>();
		vertexSetMap = new HashMap<V, Set<V>>();
		for (Set<V> vertexSet : vertexSets) {
			setHeights.put(vertexSet, liuLayout.getVertexLocation(vertexSet.iterator().next()).getSecond());
			for(V v: vertexSet) vertexSetMap.put(v, vertexSet);
		}
		
		boolean first = true;
		for (Set<V> vertexSet : liuLayout.getSets()) {
			List<V> vertices = new ArrayList<V>(vertexSet);
			Collections.sort(vertices, new Comparator<V>() {
				@Override public int compare(V v1, V v2) {
					return liuLayout.getVertexLocation(v1).getFirst().compareTo(liuLayout.getVertexLocation(v2).getFirst());
				}
			});
			currentHeight = setHeights.get(vertexSet);
			
			if(first) {
				V v1 = vertices.get(0);
				vertexColumns.put(v1, liuLayout.getVertexLocation(v1).getFirst());
				getGraph().addVertex(v1);
				addEdgeLocations(v1);
				snapshot("placing first vertex");
				first = false;
				continue;
			}
						
			V v1 = vertices.get(0);
			placeVertex(v1);
			snapshot("placing initial vertex of tier");
			if(escalatedAtLeast(EscalationLevel.CHEAP_ADJUSTMENTS))
				adjustForSideEdge(v1, Port.L, vertexSet);
			
			V vN = vertices.get(vertices.size()-1);
			if(!vN.equals(v1)) {
				placeVertex(vN);
				snapshot("placing last vertex of tier");
			}
			if(escalatedAtLeast(EscalationLevel.CHEAP_ADJUSTMENTS))
				adjustForSideEdge(vN, Port.R, vertexSet);
			
			for (int i = 1; i < vertices.size() - 1; i++) {
				V vI = vertices.get(i);
				placeVertex(vI);
				snapshot("placing vertex " + i + " of tier");
			}
			
			for (V vI : vertices) {
				if(escalatedAtLeast(EscalationLevel.CHEAP_ADJUSTMENTS))
					adjustForBottomLEdge(vI);
			}
			
		}
		
	}
	
	// cut through, build inner CD, move up, build outer CD, move left
	private void adjustForSideEdge(V v, Port side, Set<V> set) {
		E sideEdge = getEdgeAt(v, side);
		if(sideEdge != null) {
			
			V sideEdgeOpposite = getOriginalGraph().getOpposite(v, sideEdge);
			if(vertexColumns.get(sideEdgeOpposite) != null) {// not an outgoing edge
				
				Cut<V, E> cut = new Cut<V, E>(liuLayout, vertexColumns.keySet());
				cut.addRestriction(sideEdge);
				cut.goTo(sideEdgeOpposite, Quadrant.getQuadrant(side == Port.R, sideEdge.equals(getEdgeAt(sideEdgeOpposite, Port.T))));
				cut.goDownwards();
				
				SmoothEdge smoothEdge = edgeGenerator.generateEdge(sideEdge);
				
				adjustInnerCollisionsOfSideEdge(side, sideEdge, v, sideEdgeOpposite, smoothEdge, cut, set);
				
				adjustOuterCollisionsOfSideEdge(side, sideEdge, v, sideEdgeOpposite, smoothEdge, cut);
				
				if(escalatedAtLeast(EscalationLevel.ALL_ADJUSTMENTS))
					adjustSlopeOfSideLEdge(side, sideEdge, v, sideEdgeOpposite);
				
			}
			
		}
	}
	
	private void adjustInnerCollisionsOfSideEdge(Port side, E sideEdge, V v, V sideEdgeOpposite, SmoothEdge smoothEdge, Cut<V, E> cut, Set<V> set) {
		CollisionManager innerCollisionManager = edgesCollisionManager(cut.getEdgesAt(side.getOpposite()), sideEdge);
		for(int triesLeft = getMaximumMovingDistance(); innerCollisionManager.collidesAny(smoothEdge.getSegments()); triesLeft--) {
			if(triesLeft <= 0) {
				System.out.println(innerCollisionManager.collisions());
				innerCollisionManager.addAll(smoothEdge.getSegments());
				System.out.println(innerCollisionManager.collisions());
				throw new IllegalStateException("Reached maximum moving distance of " + getMaximumMovingDistance());
			}
			moveSetUp(set, 1);
			currentHeight++;
			smoothEdge = edgeGenerator.generateEdge(sideEdge);
			snapshot("moving tier up to avoid collisions inner of the " + side + "most edge at " + v);
		}
	}
	
	private void adjustOuterCollisionsOfSideEdge(Port side, E sideEdge, V v, V sideEdgeOpposite, SmoothEdge smoothEdge, Cut<V, E> cut) {
		CollisionManager outerCollisionManager = edgesCollisionManager(cut.getEdgesAt(side), sideEdge);
		for(int triesLeft = getMaximumMovingDistance(); outerCollisionManager.collidesAny(smoothEdge.getSegments()); triesLeft--) {
			if(triesLeft <= 0) {
				System.out.println(outerCollisionManager.collisions());
				outerCollisionManager.addAll(smoothEdge.getSegments());
				System.out.println(outerCollisionManager.collisions());
				throw new IllegalStateException("Reached maximum moving distance of " + getMaximumMovingDistance());
			}
			moveStuffRight(cut.getVerticesAt(side), cut.getEdgesAt(side), side.getDirection().getFirst());
			smoothEdge = edgeGenerator.generateEdge(sideEdge);
			outerCollisionManager = edgesCollisionManager(cut.getEdgesAt(side), sideEdge);
			snapshot("moving stuff outside to avoid collision with " + side + "most edge at " + v);
		}
	}
	
	private void adjustSlopeOfSideLEdge(Port side, E sideEdge, V v, V sideEdgeOpposite) {
		if(sideEdge.equals(getEdgeAt(sideEdgeOpposite, Port.T))) { // gotta check slope of L edge
			int dx = (getVertexLocation(v).getFirst() - getVertexLocation(sideEdgeOpposite).getFirst());
			int dy = (getVertexLocation(v).getSecond() - getVertexLocation(sideEdgeOpposite).getSecond());
			int d = side.getDirection().getFirst();
			if( -d*dx < dy) {
				Cut<V, E> cut = new Cut<V, E>(liuLayout, vertexColumns.keySet());
				cut.addRestriction(sideEdge);
				cut.goTo(sideEdgeOpposite, Quadrant.getQuadrant(side != Port.R, sideEdge.equals(getEdgeAt(sideEdgeOpposite, Port.T))));
				cut.goDownwards();
				int offset = d*dy + dx;
				moveStuffRight(cut.getVerticesAt(side), cut.getEdgesAt(side), offset);
				snapshot("adjusting slope of " + side + "most edge at " + v);
			}
		}
		
	}
	
	private void adjustForBottomLEdge(V v) {
		E botEdge = getEdgeAt(v, Port.B);
		if(botEdge != null) { // may happen at v1
			V botEdgeOpposite = getOriginalGraph().getOpposite(v, botEdge);
			Port side = Util.getKeyByValue(liuLayout.getPortAssignment(botEdgeOpposite), botEdge).getOpposite();
			if(side.isHorizontal()) { // gotta check collisions of L edge
				
				Cut<V, E> cut = new Cut<V, E>(liuLayout, vertexColumns.keySet());
				cut.goTo(v, Quadrant.getQuadrant(side == Port.R, true));
				cut.goDownwards();
				
				adjustCollisionsOfBottomLEdge(side, botEdge, v, botEdgeOpposite, cut);
				
				if(escalatedAtLeast(EscalationLevel.ALL_ADJUSTMENTS))
					adjustSlopeOfBottomLEdge(side, botEdge, v, botEdgeOpposite, cut);
				
			}
		}
	}
	
	private void adjustCollisionsOfBottomLEdge(Port side, E botEdge, V v, V botEdgeOpposite, Cut<V, E> cut) {
		SmoothEdge smoothEdge = edgeGenerator.generateEdge(botEdge);
		
		CollisionManager collisionManager = edgesCollisionManager(cut.getEdgesAt(side), botEdge);
		for(int triesLeft = getMaximumMovingDistance(); collisionManager.collidesAny(smoothEdge.getSegments()); triesLeft--) {
			if(triesLeft <= 0) {
				System.out.println(collisionManager.collisions());
				collisionManager.addAll(smoothEdge.getSegments());
				System.out.println(collisionManager.collisions());
				throw new IllegalStateException("Reached maximum moving distance of " + getMaximumMovingDistance());
			}
			moveStuffRight(cut.getVerticesAt(side), cut.getEdgesAt(side), side.getDirection().getFirst());
			smoothEdge = edgeGenerator.generateEdge(botEdge);
			collisionManager = edgesCollisionManager(cut.getEdgesAt(side), botEdge);
			snapshot("moving stuff outside to avoid collision with bot edge at " + v);
		}
	}

	private void adjustSlopeOfBottomLEdge(Port side, E botEdge, V v, V botEdgeOpposite, Cut<V, E> cut) {
		int dx = (getVertexLocation(v).getFirst() - getVertexLocation(botEdgeOpposite).getFirst());
		int dy = (getVertexLocation(v).getSecond() - getVertexLocation(botEdgeOpposite).getSecond());
		int d = side.getDirection().getFirst();
		if( -d*dx < dy) {
			int offset = d*dy + dx;
			moveStuffRight(cut.getVerticesAt(side), cut.getEdgesAt(side), offset);
			snapshot("adjusting slope of bot edge at " + v);
		}
	}

	private void placeVertex(V v) {
		vertexColumns.put(v, getEdgeLocation(getEdgeAt(v, Port.B)).getFirst());
		getGraph().addVertex(v);
		addEdgeLocations(v);
	}
	
	private E getEdgeAt(V v, Port p) {
		return liuLayout.getPortAssignment(v).get(p);
	}
	
	private void moveStuffRight(Set<V> vertices, Set<E> edges, int offset) {
		for(E e: edges) {
			if(edgeColumns.get(e) != null) {
				edgeColumns.put(e, edgeColumns.get(e) + offset);
			}
		}
		for(V v : vertices) {
			if(vertexColumns.get(v) != null) {
				vertexColumns.put(v, vertexColumns.get(v) + offset);
			}
		}
	}

	private void addEdgeLocations(V v) {
		for(E e : getOriginalGraph().getIncidentEdges(v)) {
			if(edgeColumns.get(e) == null) {
				int x = liuLayout.getEdgeLocation(e).getFirst() - liuLayout.getVertexLocation(v).getFirst() + getVertexLocation(v).getFirst();
				edgeColumns.put(e, x);
			} else {
				getGraph().addEdge(e, v, getOriginalGraph().getOpposite(v, e));
			}
		}
	}

	private void moveSetUp(Set<V> set, int offset) {
		int currentHeight = setHeights.get(set);
		for(Entry<Set<V>, Integer> entry : setHeights.entrySet()) {
			if(entry.getValue() >= currentHeight) {
				entry.setValue(entry.getValue() + offset);
			}
		}
	}
	
	private CollisionManager edgesCollisionManager(Collection<E> edges, E exception) {
		CollisionManager collisionManager = new CollisionManager();
		for(E e : edges) {
			if(e.equals(exception)) continue;
			MutablePair<Pair<Integer>> endpoints = getEndpointLocations(e);
			if(endpoints.getFirst() == null) throw new IllegalStateException("Nirvana-Edge in cut!?");
			if(endpoints.getSecond() != null) {
				if (edgeColumns.get(e) == null) throw new IllegalStateException("Gaaaah!");
				SmoothEdge smoothEdge = edgeGenerator.generateEdge(e);
				collisionManager.addAll(smoothEdge.getSegments());
			} else { // open edge
				collisionManager.addAll(getOpenEdgeSegmentsForCollisions(endpoints, e));
			}			
		}
		return collisionManager;
	}
	
	private List<Segment> getOpenEdgeSegmentsForCollisions(MutablePair<Pair<Integer>> endpoints, E e) {
		return getOpenEdgeSegments(endpoints, e, false);
	}
	
	protected List<Segment> getOpenEdgeSegmentsForDisplay(MutablePair<Pair<Integer>> endpoints, E e) {
		return getOpenEdgeSegments(endpoints, e, true);
	}
	
	private List<Segment> getOpenEdgeSegments(MutablePair<Pair<Integer>> endpoints, E e, boolean includeDisplayOnly) {
		List<Segment> segments = new ArrayList<Segment>(2);
		int vertexColumn = endpoints.getFirst().getFirst();
		int vertexHeight = endpoints.getFirst().getSecond();
		int edgeColumn = edgeColumns.get(e);
		if (includeDisplayOnly) // don't check collisions here, bad for U edge
			segments.add(new Segment.Line(new Pair<Integer>(vertexColumn, vertexHeight), new Pair<Integer>(edgeColumn, vertexHeight)));
		segments.add(new Segment.Line(new Pair<Integer>(edgeColumn, vertexHeight), new Pair<Integer>(edgeColumn, currentHeight)));
		return segments;
	}
	
	protected MutablePair<Pair<Integer>> getEndpointLocations(E e) {
		Pair<V> endpoints = getOriginalGraph().getEndpoints(e);
		Pair<Integer> locationA = getVertexLocation(endpoints.getFirst());
		Pair<Integer> locationB = getVertexLocation(endpoints.getSecond());
		if(locationA == null) {
			Pair<Integer> tmp = locationA;
			locationA = locationB;
			locationB = tmp;
		}
		return new MutablePair<Pair<Integer>>(locationA, locationB);
	}
	
	protected Graph<V, E> getOriginalGraph() {
		return liuLayout.getGraph();
	}
	
	@Override
	public void reset() {
		liuLayout.reset();
		initialize();
	}

	@Override
	public Pair<Integer> getVertexLocation(V v) {
		if(vertexColumns.get(v) == null) return null;
		return new Pair<Integer>(vertexColumns.get(v), setHeights.get(vertexSetMap.get(v)));
	}

	@Override
	public Pair<Integer> getEdgeLocation(E e) {
		if(edgeColumns.get(e) == null) return null;
		return new Pair<Integer>(edgeColumns.get(e), 0); // TODO: y coord
	}

	@Override
	public Map<Port, E> getPortAssignment(V v) {
		return liuLayout.getPortAssignment(v);
	}

	protected void snapshot(String comment) {
		// Overridden in subclass
	}
	
	protected int getMaximumMovingDistance() {
		return MAXIMUM_MOVING_DISTANCE;
	}
	
	private boolean escalatedAtLeast(EscalationLevel escalationLevel) {
		return this.escalationLevel.compareTo(escalationLevel) >= 0;
	}
	
	public EscalationLevel getEscalationLevel() {
		return escalationLevel;
	}
	
	public enum EscalationLevel {
		NO_ADJUSTMENTS, CHEAP_ADJUSTMENTS, ALL_ADJUSTMENTS;
		
		public EscalationLevel furtherEscalate() {
			if(ordinal()+1 >= values().length) throw new IllegalStateException("Can not further escalate than " + this);
			return values()[ordinal() + 1];
		}
		
		public static EscalationLevel getHightest() {
			return values()[values().length - 1];
		}
		
		public static EscalationLevel getLowest() {
			return values()[0];
		}
	}
	
}
