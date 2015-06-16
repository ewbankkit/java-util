/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QuintupleUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:57 QuintupleUnitTest.java NSI";

    @Test
    public void quintupleTest1() {
        Quintuple<String, Integer, long[], Object, Singleton<String>> quintuple = Quintuple.from("Hello", Integer.valueOf(1), new long[] {2L}, null, Singleton.from("Goodbye"));
        assertEquals("Hello", quintuple.getFirst());
        assertEquals(1, quintuple.getSecond().intValue());
        assertArrayEquals(new long[] {2L}, quintuple.getThird());
        assertEquals(null, quintuple.getFourth());
        assertEquals("Goodbye", quintuple.getFifth().getFirst());
    }

    @Test
    public void quintupleTest2() {
        Quintuple<String, Integer, long[], Object, Singleton<String>> quintuple = Quintuple.from(Quadruple.from("Hello", Integer.valueOf(1), new long[] {2L}, null), Singleton.from("Goodbye"));
        assertEquals("Hello", quintuple.get(0));
        assertEquals(1, quintuple.get(1));
        assertArrayEquals(new long[] {2L},(long[])quintuple.get(2));
        assertEquals(null, quintuple.get(3));
        assertEquals("Goodbye", ((Singleton<?>)quintuple.get(4)).get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void quadrupleTest3() {
        Quintuple.from("Hello", Integer.valueOf(1), new long[] {2L}, null, Singleton.from("Goodbye")).get(5);
    }

    @Test
    public void quintupleTest4() {
        long[] array = new long[] {2L};
        Quintuple<String, Integer, long[], Object, Singleton<String>> quintuple1 = Quintuple.from("Hello", Integer.valueOf(1), array, null, Singleton.from("Goodbye"));
        Quintuple<String, Integer, long[], Object, Singleton<String>> quintuple2 = Quintuple.from("Hello", Integer.valueOf(1), array, null, Singleton.from("Goodbye"));
        assertTrue(quintuple1.equals(quintuple2));
    }

    @Test
    public void quintupleTest5() {
        long[] array = new long[] {2L};
        Quintuple<String, Integer, long[], Object, Singleton<String>> quintuple1 = Quintuple.from("Hello", Integer.valueOf(1), array, null, Singleton.from("Goodbye"));
        Quintuple<String, Integer, long[], Object, Singleton<String>> quintuple2 = Quintuple.from("Hello", Integer.valueOf(1), array, null, Singleton.from("Au revoir"));
        assertFalse(quintuple1.equals(quintuple2));
    }
}
