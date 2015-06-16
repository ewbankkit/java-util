/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.cache;

/**
 * Base class for remote data caches.
 */
public abstract class BaseRemoteDataCache<K, V> implements DataCache<K, V> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:14 BaseRemoteDataCache.java NSI";

    private final MemcachedCache memcachedCache;

    /**
     * Constructor.
     */
    protected BaseRemoteDataCache(MemcachedCache memcachedCache) {
        if (memcachedCache == null) {
            throw new IllegalArgumentException();
        }
        this.memcachedCache = memcachedCache;
    }

    /**
     * Return the value in the cache associated with the specified key.
     */
    @SuppressWarnings("unchecked")
    public final V get(K key) {
        return (V)memcachedCache.get(getKeyString(key), getTimeoutMilliseconds());
    }

    /**
     * Put the specified value into the cache and associated it with the specified key.
     */
    public void put(K key, V value) {
        memcachedCache.put(getKeyString(key), value, getLifetimeSeconds());
    }

    /**
     * Remove the value in the cache associated with the specified key.
     */
    public final void remove(K key) {
        memcachedCache.remove(getKeyString(key));
    }

    /**
     * Return the key string associated with the specified key.
     */
    protected abstract String getKeyString(K key);

    /**
     * Return the default lifetime seconds.
     */
    protected abstract int getLifetimeSeconds();

    /**
     * Return the default timeout milliseconds.
     */
    protected abstract long getTimeoutMilliseconds();
}
