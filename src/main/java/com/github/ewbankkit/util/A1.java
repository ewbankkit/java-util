/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

/**
 * Represents an action on T.
 */
public abstract class A1<T> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:47 A1.java NSI";

    public abstract void apply(T t) throws Exception;

    /**
     * Convert to an F1<T, Void>.
     */
    public final F1<T, Void> toF1() {
        return new F1<T, Void>() {
            @Override
            public Void apply(T t) throws Exception {
                A1.this.apply(t);
                return null;
            }};
    }
}
