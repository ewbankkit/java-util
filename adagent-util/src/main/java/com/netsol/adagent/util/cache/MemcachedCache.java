/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.cache;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 * Memcached cache.
 * The cache can be used by multiple threads simultaneously.
 */
public final class MemcachedCache {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:15 MemcachedCache.java NSI";
    
    public static final int MAX_KEY_LEN = MemcachedClient.MAX_KEY_LENGTH;

    private long defaultTimeoutMilliseconds;
    private final MemcachedClient memcachedClient;

    /**
     * Class constructor.
     */
    static {
        // Initialize spymemcached logging.
        // Wait for version 2.4.2.
        // System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
    }

    /**
     * Constructor.
     */
    public MemcachedCache(String serverPool) throws IOException {
        memcachedClient = new MemcachedClient(AddrUtil.getAddresses(serverPool));
    }

    public void setDefaultTimeoutMilliseconds(long defaultTimeoutMilliseconds) {
        this.defaultTimeoutMilliseconds = defaultTimeoutMilliseconds;
    }

    /**
     * Close this cache.
     */
    public void close() {
        memcachedClient.shutdown();
    }

    /**
     * Return the value in the cache associated with the specified key.
     * Wait at most the specified number of milliseconds for the value to become available.
     */
    public Object get(String key, long timeoutMilliseconds) {
        Future<Object> future = memcachedClient.asyncGet(key);
        try {
            return future.get(timeoutMilliseconds, TimeUnit.MILLISECONDS);
        }
        catch (Exception ex) {
            future.cancel(false);
        }
        return null;
    }

    /**
     * Put the specified value into the cache and associated it with the specified key.
     * The value is inserted only if there is no existing value for the key.
     * The value has a lifetime equal to the specified number of seconds.
     * Return the existing value (or null).
     */
    public Object put(String key, Object value, int lifetimeSeconds) {
        Object existingValue = get(key, defaultTimeoutMilliseconds);
        if (existingValue == null) {
            // No duplicate detected.
            memcachedClient.set(key, lifetimeSeconds, value);
        }
        return existingValue;
    }

    /**
     * Remove the value in the cache associated with the specified key.
     */
    public void remove(String key) {
        Future<Boolean> future = memcachedClient.delete(key);
        try {
            future.get(defaultTimeoutMilliseconds, TimeUnit.MILLISECONDS);
        }
        catch (Exception ex) {
            future.cancel(false);
        }
    }

    /**
     * Put the specified value into the cache and associated it with the specified key.
     * The value is inserted regardless of whether there is an existing value for the key.
     * The value has a lifetime equal to the specified number of seconds.
     */
    public void replace(String key, Object value, int lifetimeSeconds) {
        memcachedClient.set(key, lifetimeSeconds, value);
    }
}
