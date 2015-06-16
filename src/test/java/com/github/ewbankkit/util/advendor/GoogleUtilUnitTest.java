/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.advendor;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import com.netsol.adagent.util.advendor.GoogleUtil;

public class GoogleUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:48 GoogleUtilUnitTest.java NSI";

    @Test
    public void toDollarsTest1() {
        long micros = 2500010L;
        assertEquals(2.50D, GoogleUtil.toDollars(micros), 0D);
    }

    @Test
    public void toDollarsTest2() {
        long micros = 20000L;
        assertEquals(0.02D, GoogleUtil.toDollars(micros), 0D);
    }

    @Test
    public void toMicrosTest1() {
        double dollars = 2.50D;
        assertEquals(2500000L, GoogleUtil.toMicros(dollars));
    }

    @Test
    public void toMicrosTest2() {
        double dollars = 0.019D;
        assertEquals(20000L, GoogleUtil.toMicros(dollars));
    }

    @Test
    public void toMicrosTest3() {
        double dollars = 2.07D;
        assertEquals(2070000L, GoogleUtil.toMicros(dollars));
    }

    @Test
    public void stringToDateTest1() throws ParseException {
        assertEquals("20111115", GoogleUtil.dateToString(GoogleUtil.stringToDate("20111115")));
    }

    @Test(expected=ParseException.class)
    public void stringToDateTest2() throws ParseException {
        GoogleUtil.stringToDate("2011/11/15");
    }
}
