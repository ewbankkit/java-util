/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.beans.Triple;

public class TripleUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:58 TripleUnitTest.java NSI";

    private static final long[] LONGS = new long[] {2L};

    @Test
    public void tripleTest1() {
        Triple<String, Integer, long[]> triple = Triple.from("Hello", Integer.valueOf(1), LONGS);
        assertEquals("Hello", triple.getFirst());
        assertEquals(1, triple.getSecond().intValue());
        assertArrayEquals(LONGS, triple.getThird());
    }

    @Test
    public void tripleTest2() {
        Triple<String, Integer, long[]> triple = Triple.from(Pair.from("Hello", Integer.valueOf(1)), LONGS);
        assertEquals("Hello", triple.get(0));
        assertEquals(1, triple.get(1));
        assertArrayEquals(LONGS, (long[])triple.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void tripleTest3() {
        Triple.from("Hello", Integer.valueOf(1), LONGS).get(3);
    }

    @Test
    public void tripleTest4() {
        Triple<Object, Object, Object> triple = Triple.from(null, null, null);
        assertTrue(triple.toArray().length == 3);
        assertArrayEquals(new Object[] {null, null, null}, triple.toArray());
    }

    @Test
    public void tripleTest5() {
        Triple<String, Integer, long[]> triple = Triple.from("Hello", Integer.valueOf(1), LONGS);
        assertTrue(triple.toArray().length == 3);
        assertArrayEquals(new Object[] {"Hello", 1, LONGS}, triple.toArray());
    }

    @Test
    public void tripleTest6() {
        Triple<Object, Object, Object> triple = Triple.from(null, null, null);
        assertTrue(triple.toList().size() == 3);
        assertEquals(null, triple.toList().get(0));
        assertEquals(null, triple.toList().get(1));
        assertEquals(null, triple.toList().get(2));
    }

    @Test
    public void tripleTest7() {
        Triple<String, Integer, long[]> triple = Triple.from("Hello", Integer.valueOf(1), LONGS);
        assertTrue(triple.toList().size() == 3);
        assertEquals("Hello", triple.toList().get(0));
        assertEquals(1, triple.toList().get(1));
        assertArrayEquals(LONGS, (long[])triple.toList().get(2));
    }

    @Test
    public void tripleTest8() {
        assertFalse(Triple.from("Hello", Integer.valueOf(1), null).equals(Triple.from("Hello", Integer.valueOf(2), null)));
    }

    @Test
    public void tripleTest9() {
        assertTrue(Triple.from("Hello", Integer.valueOf(1), null).equals(Triple.from("Hello", Integer.valueOf(1), null)));
    }

    @Test
    public void tripleTest10() {
        assertTrue(Triple.from("Hello", Integer.valueOf(1), LONGS).equals(Triple.from("Hello", Integer.valueOf(1), LONGS)));
    }

    @Test
    public void tripleTest11() {
        assertFalse(Triple.from("Hello", Integer.valueOf(1), LONGS).equals(Triple.from("Hello", Integer.valueOf(1), new long[] {1L})));
    }

    @Test
    public void tripleTest12() {
        assertFalse(Triple.from("Hello", Integer.valueOf(1), new long[] {1L, 2L}).equals(Triple.from("Hello", Integer.valueOf(1), new long[] {1L})));
    }

    @Test
    public void tripleTest13() {
        assertTrue(Triple.from(null, null, null).equals(Triple.from(null, null, null)));
    }
}
