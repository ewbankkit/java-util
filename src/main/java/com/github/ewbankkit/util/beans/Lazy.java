/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

/**
 * Provides support for lazy initialization.
 */
public interface Lazy<T> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:58 Lazy.java NSI";

    /**
     * Returns a value that indicates whether a value has been created for this instance.
     */
    public abstract boolean isValueCreated();

    /**
     * Returns the lazily initialized value of the current instance.
     */
    public abstract T getValue() throws Exception;
}
