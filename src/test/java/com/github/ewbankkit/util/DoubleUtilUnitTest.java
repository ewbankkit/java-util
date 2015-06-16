/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DoubleUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:03 DoubleUtilUnitTest.java NSI";

    @Test
    public void roundToTwoDecimalsTest1() {
        double value = 23.4578D;
        assertEquals(23.46D, DoubleUtil.roundToTwoDecimals(value), 0D);
    }

    @Test
    public void roundToTwoDecimalsTest2() {
        double value = -45.321D;
        assertEquals(-45.32D, DoubleUtil.roundToTwoDecimals(value), 0D);
    }

    @Test
    public void roundToFourDecimalsTest1() {
        double value = 23.45789D;
        assertEquals(23.4579D, DoubleUtil.roundToFourDecimals(value), 0D);
    }

    @Test
    public void roundToFoutDecimalsTest2() {
        double value = -45.321D;
        assertEquals(-45.321D, DoubleUtil.roundToFourDecimals(value), 0D);
    }
}
