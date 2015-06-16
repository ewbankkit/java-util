/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * Provides simple for thread-safe lazy initialization.
 * Only a weak reference to the value is held,
 */
public class WeakSimpleLazy<T> implements Lazy<T> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:12 WeakSimpleLazy.java NSI";

    private final Callable<T> callable;
    private WeakReference<Option<T>> optionReference;

    /**
     * Constructor.
     */
    public WeakSimpleLazy(Callable<T> callable) {
        if (callable == null) {
            throw new IllegalArgumentException();
        }
        this.callable = callable;
        Option<T> option = Option.none();
        optionReference = new WeakReference<Option<T>>(option);
    }

    /**
     * Returns a value that indicates whether a value has been created for this instance.
     */
    public boolean isValueCreated() {
        Option<T> option = optionReference.get();
        if (option != null) {
            return option.isSome();
        }
        return false;
    }

    /**
     * Returns the lazily initialized value of the current instance.
     */
    @NotInToString
    public T getValue() throws Exception {
        Option<T> option = optionReference.get();
        if ((option == null) || option.isNone()) {
            option = Option.some(callable.call());
            optionReference = new WeakReference<Option<T>>(option);
        }
        return option.getValue();
    }

    /**
     * Clear the weak reference to the value.
     */
    public void clear() {
        optionReference.clear();
    }
}
