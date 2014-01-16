package de.uniwue.smooth.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Factory;

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
	
	public static <T> List<T> listFromFactory(Factory<T> factory, int size) {
		List<T> list = new ArrayList<>(size);
		for (int i = size; i > 0; i--) {
			list.add(factory.create());
		}
		return list;
	}
}
