package de.uniwue.smooth.util;

import edu.uci.ics.jung.graph.Hypergraph;

public class Util {
	
	public static <V> boolean copyVertices(Hypergraph<? extends V, ?> source, Hypergraph<? super V, ?> dest) {
		boolean allSuccessful = true;
		
		for (V v : source.getVertices())
			allSuccessful = allSuccessful && dest.addVertex(v);
		
		return allSuccessful;
	}
	
	public static String nullsafeToString(Object o) {
		return (o == null ? "null" : o.toString());
	}
}
