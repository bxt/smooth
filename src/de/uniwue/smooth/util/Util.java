package de.uniwue.smooth.util;

import java.io.BufferedWriter;
import java.io.File;
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

/**
 * Some utility functions that did not fit anywhere else.
 */
public class Util {
	
	/**
	 * Copy all vertixes from one graph into another graph. The vertices themselves are
	 * not cloned.
	 * @param source Original graph to read the vertices from.
	 * @param dest Target graph to write the vertices into.
	 * @return If all vertices where new to the target graph.
	 */
	public static <V> boolean copyVertices(Hypergraph<? extends V, ?> source, Hypergraph<? super V, ?> dest) {
		boolean allSuccessful = true;
		
		for (V v : source.getVertices())
			allSuccessful = allSuccessful && dest.addVertex(v);
		
		return allSuccessful;
	}
	
	/**
	 * Call the {@link #toString()} method on objects when the are not <tt>null</tt>.
	 * @param o The object to stringify or null.
	 * @return Result of the objects {@link #toString()} method, or <tt>"null"</tt> when <tt>o</tt> is <tt>null</tt>.
	 */
	public static String nullsafeToString(Object o) {
		return (o == null ? "null" : o.toString());
	}
	
	/**
	 * Create a list with objects by repeatedly using the given factory.
	 * @param factory Factory to create the objects with.
	 * @param size Number of objects to create, target list size.
	 * @return A list with <tt>size</tt> newly created objects.
	 */
	public static <T> List<T> listFromFactory(Factory<T> factory, int size) {
		List<T> list = new ArrayList<>(size);
		for (int i = size; i > 0; i--) {
			list.add(factory.create());
		}
		return list;
	}
	
	/**
	 * Return a sub list of a list from a starting index inclusive to the end of the list.
	 * @param list The list to cut.
	 * @param fromIndex The index to start at.
	 * @return A sub list form the given index.
	 */
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
	
	/**
	 * Add two pairs compenent wise.
	 * @param a A pair.
	 * @param b Another pair.
	 * @return Sum of the pairs.
	 */
	public static Pair<Integer> add(Pair<Integer> a, Pair<Integer> b) {
		return new Pair<Integer>(a.getFirst() + b.getFirst(), a.getSecond() + b.getSecond());
		
	}
	
	/**
	 * Write a string to a file. 
	 * @param filename Path of the target file, will be overridden.
	 * @param contents String to write into the file, will be utf-8.
	 */
	public static void writeFile(String filename, String contents) {
		Writer writer = null;

		try {
			System.out.println(new File(filename).getCanonicalPath());
			
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
