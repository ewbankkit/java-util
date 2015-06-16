/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.netsol.adagent.util.F2;

public class F2UnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:04 F2UnitTest.java NSI";

    private static final F2<Integer, Long, String> IL2S = new F2<Integer, Long, String>() {
        @Override
        public String apply(Integer a, Long b) {
            return String.valueOf(a + b);
        }};

    @Test
    public void f2Test1() throws Exception {
        assertEquals("42", IL2S.apply(41, 1L));
    }

    @Test
    public void f2Test5() throws Exception {
        assertEquals("42", IL2S.preduce(41).apply(1L));
    }

    @Test
    public void f2Test3() throws Exception {
        assertEquals("42", IL2S.preduce(41).preduce(1L).call());
    }
}
