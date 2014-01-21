package de.uniwue.smooth.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.Pair;

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
	
	public static <T> List<T> subList(List<T> list, int fromIndex) {
		return list.subList(fromIndex, list.size());
	}
	
	/**
	 * Reverse lookup in a map.
	 * @author <a href="http://stackoverflow.com/a/2904266/935676">Vitalii Fedorenko</a>
	 * @param map The input map.
	 * @param value The value whose key is to be returned.
	 * @return The first key for which the given value is stored.
	 */
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	public static Pair<Integer> add(Pair<Integer> a, Pair<Integer> b) {
		return new Pair<Integer>(a.getFirst() + b.getFirst(), a.getSecond() + b.getSecond());
		
	}
	
	public static void writeFile(String filename, String contents) {
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename), "utf-8"));
		    writer.write(contents);
		} catch (IOException ex) {
		  throw new RuntimeException(ex);
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
}
