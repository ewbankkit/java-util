/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.cache;

import java.util.Map;
import java.util.WeakHashMap;

import com.netsol.adagent.util.beans.Pair;

/**
 * Local (per-class loader) data cache.
 * The cache can be used by multiple threads simultaneously.
 * Each value has an associated lifetime.
 */
public class LocalDataCacheWithLifetime<K, V> implements DataCache<K, V> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:15 LocalDataCacheWithLifetime.java NSI";

    private final long lifetimeInMillis;
    private final Map<K, Pair<Long, V>> map = new WeakHashMap<K, Pair<Long, V>>();

    /**
     * Constructor.
     */
    public LocalDataCacheWithLifetime(long lifetimeInMillis) {
        if (lifetimeInMillis <= 0L) {
            throw new IllegalArgumentException();
        }
        this.lifetimeInMillis = lifetimeInMillis;
    }

    /**
     * Return the value in the cache associated with the specified key.
     */
    public synchronized V get(K key) {
        Pair<Long, V> entry = map.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.getFirst().longValue() + lifetimeInMillis < System.currentTimeMillis()) {
            map.remove(key);
            return null;
        }
        return entry.getSecond();
    }

    /**
     * Put the specified value into the cache and associated it with the specified key.
     */
    public synchronized void put(K key, V value) {
        map.put(key, Pair.from(Long.valueOf(System.currentTimeMillis()), value));
    }

    /**
     * Remove the value in the cache associated with the specified key.
     */
    public synchronized void remove(K key) {
        map.remove(key);
    }
}
