package de.uniwue.smooth.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uniwue.smooth.collision.segments.Segment;
import de.uniwue.smooth.collision.segments.SmoothEdge;
import de.uniwue.smooth.collision.segments.SmoothEdgeGenerator;
import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import de.uniwue.smooth.orthogonal.Quadrant;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.util.Pair;

public class CollisionAvoidingSmoothLayout<V, E> extends AbstractLayout<V, E> implements OrthogonalLayout<V, E> {
	
	private SmoothEdgeGenerator<V, E> edgeGenerator = new SmoothEdgeGenerator<V, E>(this);
	
	private CompressingLiuEtAlLayout<V, E> liuLayout;
	
	private Map<V, Integer> vertexColumns = null;
	private Map<E, Integer> edgeColumns = null;
	private Map<Set<V>, Integer> setHeights = null;
	private Map<V, Set<V>> vertexSetMap = null;
	
	public CollisionAvoidingSmoothLayout(CompressingLiuEtAlLayout<V, E> liuEtAlLayout) {
		super(liuEtAlLayout.getGraph());
		this.liuLayout = liuEtAlLayout;
	}
	
	@Override
	public void initialize() {
		
		liuLayout.initialize();
		
		vertexColumns = new HashMap<V, Integer>();
		edgeColumns = new HashMap<E, Integer>();
		
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
			
			if(first) {
				V v1 = vertices.get(0);
				vertexColumns.put(v1, liuLayout.getVertexLocation(v1).getFirst());
				addEdgeLocations(v1);
				first = false;
				continue;
			}
			
			V v1 = vertices.get(0);
			{
				int x;
				Map<Port, E> portAssignment = liuLayout.getPortAssignment(v1);
				x = getEdgeLocation(portAssignment.get(Port.B)).getFirst();
				vertexColumns.put(v1, x);
				/*
				if(portAssignment.get(Port.L) != null) {
					E leftEdge = portAssignment.get(Port.L);
					
					if(getEdgeLocation(leftEdge) != null) {// not an outgoing edge
						// Cut at v1
						Cut<V, E> v1cut = new Cut<V, E>(liuLayout, vertexPositions.keySet(), v1, Quadrant.II);
						CollisionManager leftCollisionManager = edgesCollisionManager(v1cut.getLeftEdges());
						CollisionManager rightCollisionManager = edgesCollisionManager(v1cut.getRightEdges());
						
						SmoothEdge smoothEdge = edgeGenerator.generateEdge(leftEdge);
						
						while(rightCollisionManager.collidesAny(smoothEdge.getSegments())) {
							moveVertexUp(v1, 1);
							smoothEdge = edgeGenerator.generateEdge(leftEdge);
						}
					}
							
				}
				*/
				// TODO: check v1/vn
				// cut through, build inner CD, move up, build outer CD, move left
				addEdgeLocations(v1);
			}
			
			V vN = vertices.get(vertices.size()-1);
			{
				int x;
				Map<Port, E> portAssignment = liuLayout.getPortAssignment(vN);
				x = getEdgeLocation(portAssignment.get(Port.B)).getFirst();
				vertexColumns.put(vN, x);
				addEdgeLocations(vN);
			}
			
			for (int i = 1; i < vertices.size() - 1; i++) {
				V vI = vertices.get(i);
				Map<Port, E> portAssignment = liuLayout.getPortAssignment(vI);
				int x = getEdgeLocation(portAssignment.get(Port.B)).getFirst();
				vertexColumns.put(vI, x);
				addEdgeLocations(vI);
			}
			
		}
		
	}
	
	private void addEdgeLocations(V v) {
		for(E e : getGraph().getIncidentEdges(v)) {
			if(edgeColumns.get(e) == null) {
				int x = liuLayout.getEdgeLocation(e).getFirst() - liuLayout.getVertexLocation(v).getFirst() + getVertexLocation(v).getFirst();
				edgeColumns.put(e, x);
			}
		}
	}

	private void moveVertexUp(V v, int offset) {
		Set<V> set = vertexSetMap.get(v);
		setHeights.put(set, setHeights.get(set) + offset);
		// TODO: move other sets above!
	}
	
	private CollisionManager edgesCollisionManager(Collection<E> edges) {
		CollisionManager collisionManager = new CollisionManager();
		for(E e : edges) {
			if (getEdgeLocation(e) != null) {
				SmoothEdge smoothEdge = edgeGenerator.generateEdge(e);
				collisionManager.addAll(smoothEdge.getSegments());
			}
		}
		return collisionManager;
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
	
}
