package hydraheadhunter.datastacks.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Copy pasted from stack exchange
 * Not yet used anywhere in the project
 */

public class ItemTagCache {
		/*
	private static ItemTagCache instance;
	private static Object monitor = new Object();
	private Map<String, Object> cache = Collections.synchronizedMap(new HashMap<String, Object>());
	
	public ItemTagCache() {
	}
	
	public void put(String cacheKey, Object value) {
		cache.put(cacheKey, value);
	}
	
	public Object get(String cacheKey) {
		return cache.get(cacheKey);
	}
	
	public void clear(String cacheKey) {
		cache.put(cacheKey, null);
	}
	
	public void clear() {
		cache.clear();
	}
	
	public static ItemTagCache getInstance() {
		if (instance == null) {
			synchronized (monitor) {
				if (instance == null) {
					instance = new ItemTagCache();
				}
			}
		}
		return instance;
	}*/
}
