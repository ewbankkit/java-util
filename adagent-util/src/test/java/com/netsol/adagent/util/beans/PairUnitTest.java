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
import com.netsol.adagent.util.beans.Singleton;

public class PairUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:56 PairUnitTest.java NSI";

    @Test
    public void pairTest1() {
        Pair<String, Integer> pair = Pair.from("Hello", Integer.valueOf(1));
        assertEquals("Hello", pair.getFirst());
        assertEquals(1, pair.getSecond().intValue());
    }

    @Test
    public void pairTest2() {
        Pair<String, Integer> pair = Pair.from(Singleton.from("Hello"), Integer.valueOf(1));
        assertEquals("Hello", pair.get(0));
        assertEquals(1, pair.get(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void pairTest3() {
        Pair.from("Hello", Integer.valueOf(1)).get(2);
    }

    @Test
    public void pairTest4() {
        Pair<Object, Object> pair = Pair.from(null, null);
        assertTrue(pair.toArray().length == 2);
        assertArrayEquals(new Object[] {null, null}, pair.toArray());
    }

    @Test
    public void pairTest5() {
        Pair<String, Integer> pair = Pair.from("Hello", Integer.valueOf(1));
        assertTrue(pair.toArray().length == 2);
        assertArrayEquals(new Object[] {"Hello", 1}, pair.toArray());
    }

    @Test
    public void pairTest6() {
        Pair<Object, Object> pair = Pair.from(null, null);
        assertTrue(pair.toList().size() == 2);
        assertEquals(null, pair.toList().get(0));
        assertEquals(null, pair.toList().get(1));
    }

    @Test
    public void pairTest7() {
        Pair<String, Integer> pair = Pair.from("Hello", Integer.valueOf(1));
        assertTrue(pair.toList().size() == 2);
        assertEquals("Hello", pair.toList().get(0));
        assertEquals(1, pair.toList().get(1));
    }

    @Test
    public void pairTest8() {
        assertFalse(Pair.from("Hello", Integer.valueOf(1)).equals(Pair.from("Hello", Integer.valueOf(2))));
    }

    @Test
    public void pairTest9() {
        assertTrue(Pair.from("Hello", Integer.valueOf(1)).equals(Pair.from("Hello", Integer.valueOf(1))));
    }

    @Test
    public void pairTest10() {
        assertTrue(Pair.from(null, null).equals(Pair.from(null, null)));
    }

    @Test
    public void pairTest11() {
        assertFalse(Pair.from(null, Integer.valueOf(1)).equals(Pair.from(null, null)));
    }

    @Test
    public void pairTest12() {
        assertFalse(Pair.from("Hello", null).equals(Pair.from(null, null)));
    }
}
