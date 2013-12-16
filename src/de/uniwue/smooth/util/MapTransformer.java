package de.uniwue.smooth.util;

import java.util.Map;

import org.apache.commons.collections15.Transformer;

public class MapTransformer<K, V> implements Transformer<K, V> {

	private Map<K, V> map;
	
	public MapTransformer(Map<K, V> map) {
		super();
		this.map = map;
	}

	@Override
	public V transform(K input) {
		return map.get(input);
	}
	
}
