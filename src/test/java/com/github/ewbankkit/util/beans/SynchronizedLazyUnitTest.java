/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.junit.Test;

public class SynchronizedLazyUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:57 SynchronizedLazyUnitTest.java NSI";

    private final static Lazy<String> LAZY = new SimpleLazy<String>(new Callable<String>() {
        public String call() {
            return "BOOP";
        }});
    private final static Lazy<String> NULL_LAZY = new SimpleLazy<String>(new Callable<String>() {
        public String call() {
            return null;
        }});

    @Test
    public void lazyTest1() {
        Lazy<String> lazy = new SynchronizedLazy<String>(LAZY);
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest2() {
        Lazy<String> lazy = new SynchronizedLazy<String>(NULL_LAZY);
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest3() throws Exception {
        Lazy<String> lazy = new SynchronizedLazy<String>(LAZY);
        assertEquals("BOOP", lazy.getValue());
        assertTrue(lazy.isValueCreated());
    }

    @Test
    public void lazyTest4() throws Exception {
        Lazy<String> lazy = new SynchronizedLazy<String>(NULL_LAZY);
        assertNull(lazy.getValue());
        assertTrue(lazy.isValueCreated());
    }

    @Test
    public void lazyTest5() throws Exception {
        Lazy<String> lazy = new SynchronizedLazy<String>(LAZY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertEquals("BOOP", lazy.getValue());
    }

    @Test
    public void lazyTest6() throws Exception {
        Lazy<String> lazy = new SynchronizedLazy<String>(NULL_LAZY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertNull(lazy.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void lazyTest7() {
        Lazy<String> lazy = new SynchronizedLazy<String>(null);
        assertFalse(lazy.isValueCreated());
    }
}
