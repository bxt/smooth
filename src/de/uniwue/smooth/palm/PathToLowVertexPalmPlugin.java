package de.uniwue.smooth.palm;

import java.util.HashMap;
import java.util.Map;

import de.uniwue.smooth.util.Tuple;

public class PathToLowVertexPalmPlugin<V, E> extends NoopPalmPlugin<V, E> implements PalmPlugin<V, E> {

	private Map<V, Tuple<V, E>> backwards = new HashMap<V, Tuple<V, E>>();

	public E getBackwardEdge(V v) {
		return this.backwards.get(v).getSecond();
	}
	
	public V getBackwardVertex(V v) {
		return this.backwards.get(v).getFirst();
	}
	
	public void setLowVertexViaTree(V v, V w, E e) {
		backwards.put(v, new Tuple<V, E>(w, e));
	}
	
	public void setLowVertexViaCycle(V v, V w, E e) {
		backwards.put(v, new Tuple<V, E>(w, e));
	}
}
