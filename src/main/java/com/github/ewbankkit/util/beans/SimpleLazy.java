/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import java.util.concurrent.Callable;

/**
 * Provides simple support for lazy initialization.
 */
public class SimpleLazy<T> implements Lazy<T> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:07 SimpleLazy.java NSI%";

    private final Callable<T> callable;
    private Option<T> option;

    /**
     * Constructor.
     */
    public SimpleLazy(Callable<T> callable) {
        if (callable == null) {
            throw new IllegalArgumentException();
        }
        this.callable = callable;
        option = Option.none();
    }

    /**
     * Returns a value that indicates whether a value has been created for this instance.
     */
    public boolean isValueCreated() {
        return option.isSome();
    }

    /**
     * Returns the lazily initialized value of the current instance.
     */
    @NotInToString
    public T getValue() throws Exception {
        if (option.isNone()) {
            option = Option.some(callable.call());
        }
        return option.getValue();
    }
}
