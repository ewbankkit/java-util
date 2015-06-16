/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.cache;

/**
 * Interface implemented by data caches. 
 */
public interface DataCache<K, V> {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:14 DataCache.java NSI";
    
    /**
     * Return the value in the cache associated with the specified key.
     */
    public abstract V get(K key);
    
    /**
     * Put the specified value into the cache and associated it with the specified key.
     */
    public abstract void put(K key, V value);
    
    /**
     * Remove the value in the cache associated with the specified key.
     */
    public abstract void remove(K key);
}
