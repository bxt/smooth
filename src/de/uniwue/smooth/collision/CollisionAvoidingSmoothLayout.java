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
import de.uniwue.smooth.util.tuples.MutablePair;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class CollisionAvoidingSmoothLayout<V, E> extends AbstractLayout<V, E> implements OrthogonalLayout<V, E> {
	
	private static int MAXIMUM_MOVING_DISTANCE = 10000;
	
	private SmoothEdgeGenerator<V, E> edgeGenerator = new SmoothEdgeGenerator<V, E>(this);
	
	private CompressingLiuEtAlLayout<V, E> liuLayout;
	
	private Map<V, Integer> vertexColumns = null;
	private Map<E, Integer> edgeColumns = null;
	private Map<Set<V>, Integer> setHeights = null;
	private Map<V, Set<V>> vertexSetMap = null;
	private int currentHeight = 0;
	
	public CollisionAvoidingSmoothLayout(CompressingLiuEtAlLayout<V, E> liuLayout) {
		super(new UndirectedSparseGraph<V, E>());
		this.liuLayout = liuLayout;
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
			{
				int x;
				Map<Port, E> portAssignment = liuLayout.getPortAssignment(v1);
				x = getEdgeLocation(portAssignment.get(Port.B)).getFirst();
				vertexColumns.put(v1, x);
				getGraph().addVertex(v1);
				addEdgeLocations(v1);
				snapshot("placing initial vertex of tier");
				
				if(portAssignment.get(Port.L) != null) {
					E leftEdge = portAssignment.get(Port.L);
					
					if(vertexColumns.get(getOriginalGraph().getOpposite(v1, leftEdge)) != null) {// not an outgoing edge
						// Cut at v1
						Cut<V, E> v1cut = new Cut<V, E>(liuLayout, vertexColumns.keySet(), v1, Quadrant.II);
						CollisionManager rightCollisionManager = edgesCollisionManager(v1cut.getRightEdges(), leftEdge);
						
						SmoothEdge smoothEdge = edgeGenerator.generateEdge(leftEdge);
						
						
						for(int triesLeft = getMaximumMovingDistance(); rightCollisionManager.collidesAny(smoothEdge.getSegments()); triesLeft--) {
							if(triesLeft <= 0) throw new IllegalStateException("Reached maximum moving distance of " + getMaximumMovingDistance());
							moveSetUp(vertexSet, 1);
							currentHeight++;
							smoothEdge = edgeGenerator.generateEdge(leftEdge);
							snapshot("moving tier up to avoid collisions right of the leftmost edge at " + v1);
						}
						
						CollisionManager leftCollisionManager = edgesCollisionManager(v1cut.getLeftEdges(), leftEdge);
						for(int triesLeft = getMaximumMovingDistance(); leftCollisionManager.collidesAny(smoothEdge.getSegments()); triesLeft--) {
							if(triesLeft <= 0) {
								System.out.println(leftCollisionManager.collisions());
								leftCollisionManager.addAll(smoothEdge.getSegments());
								System.out.println(leftCollisionManager.collisions());
								throw new IllegalStateException("Reached maximum moving distance of " + getMaximumMovingDistance());
							}
							moveStuffRight(v1cut.getLeftVertices(), v1cut.getLeftEdges(), -1);
							smoothEdge = edgeGenerator.generateEdge(leftEdge);
							leftCollisionManager = edgesCollisionManager(v1cut.getLeftEdges(), leftEdge);
							snapshot("moving stuff right to avoid collision with leftmost edge at " + v1);
						}
						
					}
					
				}
				
				// TODO: check v1/vn
				// cut through, build inner CD, move up, build outer CD, move left
			}
			
			V vN = vertices.get(vertices.size()-1);
			if(!vN.equals(v1)) {
				int x;
				Map<Port, E> portAssignment = liuLayout.getPortAssignment(vN);
				x = getEdgeLocation(portAssignment.get(Port.B)).getFirst();
				vertexColumns.put(vN, x);
				getGraph().addVertex(vN);
				addEdgeLocations(vN);
				snapshot("placing last vertex of tier");
			}
			
			for (int i = 1; i < vertices.size() - 1; i++) {
				V vI = vertices.get(i);
				Map<Port, E> portAssignment = liuLayout.getPortAssignment(vI);
				int x = getEdgeLocation(portAssignment.get(Port.B)).getFirst();
				vertexColumns.put(vI, x);
				getGraph().addVertex(vI);
				addEdgeLocations(vI);
				snapshot("placing vertex " + i + " of tier");
			}
			
		}
		
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
				collisionManager.addAll(getOpenEdgeSements(endpoints, e));
			}			
		}
		return collisionManager;
	}
	
	protected List<Segment> getOpenEdgeSements(MutablePair<Pair<Integer>> endpoints, E e) {
		List<Segment> segments = new ArrayList<Segment>(2);
		int vertexColumn = endpoints.getFirst().getFirst();
		int vertexHeight = endpoints.getFirst().getSecond();
		int edgeColumn = edgeColumns.get(e);
		segments.add(new Segment.Line(new Pair<Integer>(vertexColumn, vertexHeight), new Pair<Integer>(edgeColumn, vertexHeight))); // TODO: necessary?
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
	
}
