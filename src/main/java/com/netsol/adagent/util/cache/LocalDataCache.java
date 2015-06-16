/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.cache;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Local (per-class loader) data cache.
 * The cache can be used by multiple threads simultaneously.
 */
public class LocalDataCache<K, V> implements DataCache<K, V> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:15 LocalDataCache.java NSI";

    private final Map<K, V> map = Collections.synchronizedMap(new WeakHashMap<K, V>());

    /**
     * Return the value in the cache associated with the specified key.
     */
    public V get(K key) {
        return map.get(key);
    }

    /**
     * Put the specified value into the cache and associated it with the specified key.
     */
    public void put(K key, V value) {
        map.put(key, value);
    }

    /**
     * Remove the value in the cache associated with the specified key.
     */
    public void remove(K key) {
        map.remove(key);
    }
}
