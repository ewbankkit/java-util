/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.cache;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.cache.BaseRemoteDataCache;
import com.netsol.adagent.util.cache.DataCache;
import com.netsol.adagent.util.cache.MemcachedCache;

public class RemoteDataCacheUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:15 RemoteDataCacheUnitTest.java NSI";

    private static DataCache<Integer, String> dataCache;
    private static MemcachedCache memcachedCache;

    @BeforeClass
    public static void setup() throws IOException {
        memcachedCache = new MemcachedCache("x2100.dev.netsol.com:9000 quartz.dev.netsol.com:9000");
        dataCache = new RemoteDataCache(memcachedCache);
    }

    @AfterClass
    public static void teardown() {
        memcachedCache.close();
    }

    @Test
    public void putAndGetTest1() throws Exception {
        dataCache.put(Integer.valueOf(0), "Zero");
        assertEquals("Zero", dataCache.get(Integer.valueOf(0)));
        dataCache.remove(Integer.valueOf(0));
    }

    @Test
    public void putAndGetTest2() throws Exception {
        dataCache.put(Integer.valueOf(0), "Zero");
        dataCache.put(Integer.valueOf(1), "One");
        assertEquals("One", dataCache.get(Integer.valueOf(1)));
        dataCache.remove(Integer.valueOf(0));
        dataCache.remove(Integer.valueOf(1));
    }

    @Test
    public void putAndGetTest3() throws Exception {
        dataCache.put(Integer.valueOf(0), "Zero");
        assertEquals(null, dataCache.get(Integer.valueOf(1)));
        dataCache.remove(Integer.valueOf(0));
    }

    @Test
    public void putRemoveAndGetTest1() throws Exception {
        dataCache.put(Integer.valueOf(0), "Zero");
        dataCache.remove(Integer.valueOf(0));
        assertEquals(null, dataCache.get(Integer.valueOf(0)));
    }

    @Test
    public void putRemoveAndGetTest2() throws Exception {
        dataCache.put(Integer.valueOf(0), "Zero");
        dataCache.put(Integer.valueOf(1), "One");
        dataCache.remove(Integer.valueOf(0));
        assertEquals("One", dataCache.get(Integer.valueOf(1)));
        dataCache.remove(Integer.valueOf(1));
    }

    @Test
    public void putRemoveAndGetTest3() throws Exception {
        dataCache.put(Integer.valueOf(1), "One");
        dataCache.remove(Integer.valueOf(0));
        assertEquals("One", dataCache.get(Integer.valueOf(1)));
        dataCache.remove(Integer.valueOf(1));
    }

    private static class RemoteDataCache extends BaseRemoteDataCache<Integer, String> {
        public RemoteDataCache(MemcachedCache memcachedCache) {
            super(memcachedCache);
        }

        @Override
        protected String getKeyString(Integer key) {
            return "com.netsol.adagent.util.cache.junit.RemoteDataCacheUnitTest." + String.valueOf(key);
        }

        @Override
        protected int getLifetimeSeconds() {
            return 60;
        }

        @Override
        protected long getTimeoutMilliseconds() {
            return 1000L;
        }
    }
}
