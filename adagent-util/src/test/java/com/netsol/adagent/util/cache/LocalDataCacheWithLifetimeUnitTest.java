/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.cache;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.netsol.adagent.util.cache.DataCache;
import com.netsol.adagent.util.cache.LocalDataCacheWithLifetime;

public class LocalDataCacheWithLifetimeUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:15 LocalDataCacheWithLifetimeUnitTest.java NSI";

    @Test
    public void putAndGetTest1() throws Exception {
        DataCache<Integer, String> dataCache = new LocalDataCacheWithLifetime<Integer, String>(10000L);
        dataCache.put(Integer.valueOf(0), "Zero");
        assertEquals("Zero", dataCache.get(Integer.valueOf(0)));
    }

    @Test
    public void putAndGetTest2() throws Exception {
        DataCache<Integer, String> dataCache = new LocalDataCacheWithLifetime<Integer, String>(10000L);
        dataCache.put(Integer.valueOf(0), "Zero");
        dataCache.put(Integer.valueOf(1), "One");
        assertEquals("One", dataCache.get(Integer.valueOf(1)));
    }

    @Test
    public void putAndGetTest3() throws Exception {
        DataCache<Integer, String> dataCache = new LocalDataCacheWithLifetime<Integer, String>(10000L);
        dataCache.put(Integer.valueOf(0), "Zero");
        assertEquals(null, dataCache.get(Integer.valueOf(1)));
    }
    
    @Test
    public void putAndGetTest4() throws Exception {
        DataCache<Integer, String> dataCache = new LocalDataCacheWithLifetime<Integer, String>(3000L);
        dataCache.put(Integer.valueOf(0), "Zero");
        assertEquals("Zero", dataCache.get(Integer.valueOf(0)));
        Thread.sleep(5000L);
        assertEquals(null, dataCache.get(Integer.valueOf(0)));
    }

    @Test
    public void putRemoveAndGetTest1() throws Exception {
        DataCache<Integer, String> dataCache = new LocalDataCacheWithLifetime<Integer, String>(10000L);
        dataCache.put(Integer.valueOf(0), "Zero");
        dataCache.remove(Integer.valueOf(0));
        assertEquals(null, dataCache.get(Integer.valueOf(0)));
    }

    @Test
    public void putRemoveAndGetTest2() throws Exception {
        DataCache<Integer, String> dataCache = new LocalDataCacheWithLifetime<Integer, String>(10000L);
        dataCache.put(Integer.valueOf(0), "Zero");
        dataCache.put(Integer.valueOf(1), "One");
        dataCache.remove(Integer.valueOf(0));
        assertEquals("One", dataCache.get(Integer.valueOf(1)));
    }

    @Test
    public void putRemoveAndGetTest3() throws Exception {
        DataCache<Integer, String> dataCache = new LocalDataCacheWithLifetime<Integer, String>(10000L);
        dataCache.put(Integer.valueOf(1), "One");
        dataCache.remove(Integer.valueOf(0));
        assertEquals("One", dataCache.get(Integer.valueOf(1)));
    }
}
