/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.netsol.adagent.util.F1;

public class F1UnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:03 F1UnitTest.java NSI";

    private static final F1<Integer, String> I2S = new F1<Integer, String>() {
        @Override
        public String apply(Integer a) {
            return String.valueOf(a);
        }};

    private static final F1<String, Long> S2L = new F1<String, Long>() {
        @Override
        public Long apply(String a) {
            return Long.valueOf(a);
        }};

    @Test
    public void f1Test1() throws Exception {
        assertEquals("42", I2S.apply(42));
    }

    @Test
    public void f1Test2() throws Exception {
        assertEquals(42L, S2L.apply("42").longValue());
    }

    @Test
    public void f1Test3() throws Exception {
        assertEquals(42L, S2L.o(I2S).apply(42).longValue());
    }

    @Test
    public void f1Test4() throws Exception {
        assertEquals(42L, I2S.andThen(S2L).apply(42).longValue());
    }

    @Test
    public void f1Test5() throws Exception {
        assertEquals("42", I2S.preduce(42).call());
    }
}
