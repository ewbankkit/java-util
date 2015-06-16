/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

/**
 * @author fborghesi
 *
 * <p>
 * DateFormat implementation that can be safely used by concurrent threads. It
 * uses an internal DateFormat implementation that will be cloned to generate
 * thread-local copies of it, so the methods in this class can be concurrently
 * invoked.
 * </p>
 */
@SuppressWarnings("serial")
public class ThreadSafeDateFormat extends DateFormat {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:30 ThreadSafeDateFormat.java NSI";

    private final ThreadLocalFormatWrapper<DateFormat> dateFormatWrapper;

    /**
     * Constructor.
     */
    public ThreadSafeDateFormat(DateFormat original) {
        dateFormatWrapper = new ThreadLocalFormatWrapper<DateFormat>(original);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return getDateFormat().format(date, toAppendTo, fieldPosition);
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        return getDateFormat().parse(source, pos);
    }

    private DateFormat getDateFormat() {
        return dateFormatWrapper.getFormat();
    }
}
