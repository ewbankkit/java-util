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

public class WeakSimpleLazyUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:58 WeakSimpleLazyUnitTest.java NSI";

    private final static Callable<String> FACTORY = new Callable<String>() {
        public String call() {
            return "BOOP";
        }};
    private final static Callable<String> NULL_FACTORY = new Callable<String>() {
        public String call() {
            return null;
        }};

    @Test
    public void lazyTest1() {
        Lazy<String> lazy = new WeakSimpleLazy<String>(FACTORY);
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest2() {
        Lazy<String> lazy = new WeakSimpleLazy<String>(NULL_FACTORY);
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest3() throws Exception {
        Lazy<String> lazy = new WeakSimpleLazy<String>(FACTORY);
        assertEquals("BOOP", lazy.getValue());
        assertTrue(lazy.isValueCreated());
    }

    @Test
    public void lazyTest4() throws Exception {
        Lazy<String> lazy = new WeakSimpleLazy<String>(NULL_FACTORY);
        assertNull(lazy.getValue());
        assertTrue(lazy.isValueCreated());
    }

    @Test
    public void lazyTest5() throws Exception {
        Lazy<String> lazy = new WeakSimpleLazy<String>(FACTORY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertEquals("BOOP", lazy.getValue());
    }

    @Test
    public void lazyTest6() throws Exception {
        Lazy<String> lazy = new WeakSimpleLazy<String>(NULL_FACTORY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertNull(lazy.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void lazyTest7() {
        Lazy<String> lazy = new WeakSimpleLazy<String>(null);
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest8() throws Exception {
        WeakSimpleLazy<String> lazy = new WeakSimpleLazy<String>(FACTORY);
        assertEquals("BOOP", lazy.getValue());
        assertTrue(lazy.isValueCreated());
        lazy.clear();
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest9() throws Exception {
        WeakSimpleLazy<String> lazy = new WeakSimpleLazy<String>(NULL_FACTORY);
        assertNull(lazy.getValue());
        assertTrue(lazy.isValueCreated());
        lazy.clear();
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest10() throws Exception {
        WeakSimpleLazy<String> lazy = new WeakSimpleLazy<String>(FACTORY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertEquals("BOOP", lazy.getValue());
        lazy.clear();
        assertEquals("BOOP", lazy.getValue());
    }

    @Test
    public void lazyTest11() throws Exception {
        WeakSimpleLazy<String> lazy = new WeakSimpleLazy<String>(NULL_FACTORY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertNull(lazy.getValue());
        lazy.clear();
        assertNull(lazy.getValue());
    }
}
