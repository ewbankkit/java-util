/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.advendor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.netsol.adagent.util.DateUtil;
import com.netsol.adagent.util.DoubleUtil;
import com.netsol.adagent.util.ThreadSafeDateFormat;

/**
 * Google utilities.
 */
public final class GoogleUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:48 GoogleUtil.java NSI";

    public static final int AD_HEADLINE_MAX_LEN = 25;
    public static final int AD_DESCRIPTION_LINE_MAX_LEN = 35;
    public static final int AD_DISPLAY_URL_MAX_LEN = 255;
    public static final int AD_DESTINATION_URL_MAX_LEN = 1024; // Doesn't include protocol.
    public static final int AD_GROUP_NAME_MAX_LEN = 255;
    public static final int CAMPAIGN_NAME_MAX_LEN = 128;
    public static final int KEYWORD_NAME_MAX_LEN = 80;
    public static final int KEYWORD_DESTINATION_URL_MAX_LEN = 2083; // Includes protocol.

    private static final DateFormat DATE_FORMAT = new ThreadSafeDateFormat(new SimpleDateFormat("yyyyMMdd"));

    /**
     * Constructor.
     */
    private GoogleUtil() {}

    /**
     * Return the Google micros amount in dollars.
     */
    public static double toDollars(long micros) {
        return DoubleUtil.roundToTwoDecimals((double)micros / 1000000D);
    }

    /**
     * Return the dollar amount in Google micros.
     */
    public static long toMicros(double dollars) {
        // Round to 2 decimal places.
        return Math.round(dollars * 100D) * 10000L;
    }

    /**
     * Returns a Date formatted yyyyMMdd.
     */
    public static String dateToString(Date date) {
        return DateUtil.dateToString(date, DATE_FORMAT);
    }

    /**
     * Get a Date from a String formatted yyyyMMdd.
     */
    public static Date stringToDate(String string) throws ParseException {
        return DateUtil.stringToDate(string, DATE_FORMAT);
    }
}
