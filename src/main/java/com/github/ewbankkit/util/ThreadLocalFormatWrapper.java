/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import java.text.Format;

/* package-private */ final class ThreadLocalFormatWrapper<T extends Format> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:30 ThreadLocalFormatWrapper.java NSI";

    private final ThreadLocal<T> localFormat;

    /**
     * Constructor.
     */
    public ThreadLocalFormatWrapper(final T format) {
        localFormat = new ThreadLocal<T>() {
            @Override
            @SuppressWarnings("unchecked")
            protected T initialValue() {
                return (T)format.clone();
            }
        };
    }

    public T getFormat() {
        return localFormat.get();
    }
}
