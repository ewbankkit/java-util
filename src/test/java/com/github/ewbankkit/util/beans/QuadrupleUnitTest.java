/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QuadrupleUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:56 QuadrupleUnitTest.java NSI";

    @Test
    public void quadrupleTest1() {
        Quadruple<String, Integer, long[], Object> quadruple = Quadruple.from("Hello", Integer.valueOf(1), new long[] {2L}, null);
        assertEquals("Hello", quadruple.getFirst());
        assertEquals(1, quadruple.getSecond().intValue());
        assertArrayEquals(new long[] {2L}, quadruple.getThird());
        assertEquals(null, quadruple.getFourth());
    }

    @Test
    public void quadrupleTest2() {
        Quadruple<String, Integer, long[], Object> quadruple = Quadruple.from(Triple.from("Hello", Integer.valueOf(1), new long[] {2L}), null);
        assertEquals("Hello", quadruple.get(0));
        assertEquals(1, quadruple.get(1));
        assertArrayEquals(new long[] {2L},(long[])quadruple.get(2));
        assertEquals(null, quadruple.get(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void quadrupleTest3() {
        Quadruple.from("Hello", Integer.valueOf(1), new long[] {2L}, null).get(4);
    }
}
