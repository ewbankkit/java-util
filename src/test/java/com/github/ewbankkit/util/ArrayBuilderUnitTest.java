/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

public class ArrayBuilderUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:01 ArrayBuilderUnitTest.java NSI";

    @Test
    public void arrayBuilderTest1() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        assertTrue(ab.toArray().length == 0);
    }

    @Test
    public void arrayBuilderTest2() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        assertTrue(ab.toArray(String.class).length == 0);
    }

    @Test
    public void arrayBuilderTest3() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        ab.add("Hello");
        String[] array = ab.toArray(String.class);
        assertTrue(array.length == 1);
        assertEquals("Hello", array[0]);
    }

    @Test
    public void arrayBuilderTest4() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        ab.add("Hello", "Goodbye");
        ab.addAll(Collections.singleton("Third"));
        String[] array = ab.toArray(String.class);
        assertTrue(array.length == 3);
        assertEquals("Third", array[2]);
    }

    @Test
    public void arrayBuilderTest5() throws Exception {
        ArrayBuilder<Integer> ab = new ArrayBuilder<Integer>();
        ab.add(1);
        Integer[] array = ab.toArray(Integer.class);
        assertTrue(array.length == 1);
        assertEquals(1, array[0].intValue());
    }

    @Test
    public void arrayBuilderTest6() throws Exception {
        ArrayBuilder<Integer> ab = new ArrayBuilder<Integer>();
        ab.add(new Integer[] {1, 2});
        Integer[] array = ab.toArray(Integer.class);
        assertTrue(array.length == 2);
        assertEquals(2, array[1].intValue());
    }

    @Test
    public void arrayBuilderTest7() throws Exception {
        ArrayBuilder<Integer> ab = new ArrayBuilder<Integer>();
        ab.add(new Integer[] {1, 2});
        int[] array = ab.toArrayOfInt();
        assertTrue(array.length == 2);
        assertEquals(2, array[1]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void arrayBuilderTest8() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        ab.add("Hello");
        assertTrue(ab.toArrayOfInt().length == 1);
    }

    @Test
    public void arrayBuilderTest9() throws Exception {
        ArrayBuilder<Long> ab = new ArrayBuilder<Long>();
        ab.add(new Long[] {1L, 2L});
        long[] array = ab.toArrayOfLong();
        assertTrue(array.length == 2);
        assertEquals(2L, array[1]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void arrayBuilderTest10() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        ab.add("Hello");
        assertTrue(ab.toArrayOfLong().length == 1);
    }

    @Test
    public void arrayBuilderTest11() throws Exception {
        ArrayBuilder<Float> ab = new ArrayBuilder<Float>();
        ab.add(new Float[] {1.1F, 2.2F});
        double[] array = ab.toArrayOfDouble();
        assertTrue(array.length == 2);
        assertEquals(2.2F, array[1], 0F);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void arrayBuilderTest12() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        ab.add("Hello");
        assertTrue(ab.toArrayOfDouble().length == 1);
    }

    @Test
    public void arrayBuilderTest13() throws Exception {
        ArrayBuilder<String> ab = new ArrayBuilder<String>();
        ab.add("Hello");
        String[] array = ab.toArray(String.class);
        assertTrue(array.length == 1);
        assertEquals("Hello", array[0]);
        ab.add("Goodbye");
        assertTrue(array.length == 1);
        array = ab.toArray(String.class);
        assertTrue(array.length == 2);
        assertEquals("Hello", array[0]);
        assertEquals("Goodbye", array[1]);
    }

    @Test
    public void arrayBuilderTest14() throws Exception {
        ArrayBuilder<Long> ab = new ArrayBuilder<Long>();
        ab.add(new Long[] {1L, 2L});
        long[] array = ab.toArrayOfLong();
        assertTrue(array.length == 2);
        assertEquals(2L, array[1]);
        ab.add(3L);
        assertTrue(array.length == 2);
        array = ab.toArrayOfLong();
        assertTrue(array.length == 3);
        assertEquals(2L, array[1]);
        assertEquals(3L, array[2]);
    }
}
