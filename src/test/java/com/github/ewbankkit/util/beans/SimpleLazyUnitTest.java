/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.junit.Test;

public class SimpleLazyUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:57 SimpleLazyUnitTest.java NSI";

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
        Lazy<String> lazy = new SimpleLazy<String>(FACTORY);
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest2() {
        Lazy<String> lazy = new SimpleLazy<String>(NULL_FACTORY);
        assertFalse(lazy.isValueCreated());
    }

    @Test
    public void lazyTest3() throws Exception {
        Lazy<String> lazy = new SimpleLazy<String>(FACTORY);
        assertEquals("BOOP", lazy.getValue());
        assertTrue(lazy.isValueCreated());
    }

    @Test
    public void lazyTest4() throws Exception {
        Lazy<String> lazy = new SimpleLazy<String>(NULL_FACTORY);
        assertNull(lazy.getValue());
        assertTrue(lazy.isValueCreated());
    }

    @Test
    public void lazyTest5() throws Exception {
        Lazy<String> lazy = new SimpleLazy<String>(FACTORY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertEquals("BOOP", lazy.getValue());
    }

    @Test
    public void lazyTest6() throws Exception {
        Lazy<String> lazy = new SimpleLazy<String>(NULL_FACTORY);
        lazy.getValue();
        assertTrue(lazy.isValueCreated());
        assertNull(lazy.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void lazyTest7() {
        Lazy<String> lazy = new SimpleLazy<String>(null);
        assertFalse(lazy.isValueCreated());
    }
}
