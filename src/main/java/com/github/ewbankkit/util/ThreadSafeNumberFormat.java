/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * @author kewbank
 *
 * <p>
 * NumberFormat implementation that can be safely used by concurrent threads. It
 * uses an internal NumberFormat implementation that will be cloned to generate
 * thread-local copies of it, so the methods in this class can be concurrently
 * invoked.
 * </p>
 */
@SuppressWarnings("serial")
public class ThreadSafeNumberFormat extends NumberFormat {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:31 ThreadSafeNumberFormat.java NSI";

    private final ThreadLocalFormatWrapper<NumberFormat> numberFormatWrapper;

    /**
     * Constructor.
     */
    public ThreadSafeNumberFormat(NumberFormat original) {
        numberFormatWrapper = new ThreadLocalFormatWrapper<NumberFormat>(original);
    }

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return getNumberFormat().format(number, toAppendTo, fieldPosition);
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return getNumberFormat().format(number, toAppendTo, fieldPosition);
    }

    @Override
    public Number parse(String number, ParsePosition pos) {
        return getNumberFormat().parse(number, pos);
    }

    private NumberFormat getNumberFormat() {
        return numberFormatWrapper.getFormat();
    }
}
