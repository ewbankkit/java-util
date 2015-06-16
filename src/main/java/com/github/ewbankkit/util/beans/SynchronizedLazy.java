/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

/**
 * Provides support for thread-safe lazy initialization.
 */
public class SynchronizedLazy<T> implements Lazy<T> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:08 SynchronizedLazy.java NSI";

    private final Lazy<T> innerLazy;

    /**
     * Constructor.
     */
    public SynchronizedLazy(Lazy<T> innerLazy) {
        if (innerLazy == null) {
            throw new IllegalArgumentException();
        }
        this.innerLazy = innerLazy;
    }

    /**
     * Returns a value that indicates whether a value has been created for this instance.
     */
    public synchronized boolean isValueCreated() {
        return innerLazy.isValueCreated();
    }

    /**
     * Returns the lazily initialized value of the current instance.
     */
    @NotInToString
    public synchronized T getValue() throws Exception {
        return innerLazy.getValue();
    }
}
