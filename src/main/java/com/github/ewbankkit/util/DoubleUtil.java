/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

public class DoubleUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:57 DoubleUtil.java NSI";

    /**
     * Constructor.
     */
    private DoubleUtil() {}

    public static double roundToTwoDecimals(double value) {
        return roundToDecimals(value, 2);
    }

    public static double roundToFourDecimals(double value) {
        return roundToDecimals(value, 4);
    }

    private static double roundToDecimals(double value, int decimals) {
        long l = 1L;
        for (int i = 0; i < decimals; i++) {
            l *= 10L;
        }
        double d = (double)l;
        return Math.round(value * d) / d;
    }
}
