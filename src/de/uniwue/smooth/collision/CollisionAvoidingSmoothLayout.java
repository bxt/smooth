package de.uniwue.smooth.collision;

import java.util.Map;

import de.uniwue.smooth.orthogonal.CompressingLiuEtAlLayout;
import de.uniwue.smooth.orthogonal.OrthogonalLayout;
import de.uniwue.smooth.orthogonal.Port;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.util.Pair;

public class CollisionAvoidingSmoothLayout<V, E> extends AbstractLayout<V, E> implements OrthogonalLayout<V, E> {
	
	private CompressingLiuEtAlLayout<V, E> liuEtAlLayout;
	
	public CollisionAvoidingSmoothLayout(CompressingLiuEtAlLayout<V, E> liuEtAlLayout) {
		super(liuEtAlLayout.getGraph());
		this.liuEtAlLayout = liuEtAlLayout;
	}
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset() {
		initialize();
	}

	@Override
	public Pair<Integer> getVertexLocation(V v) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Pair<Integer> getEdgeLocation(E e) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<Port, E> getPortAssignment(V v) {
		return liuEtAlLayout.getPortAssignment(v);
	}
	
}
