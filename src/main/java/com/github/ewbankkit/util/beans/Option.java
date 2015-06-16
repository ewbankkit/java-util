/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

import com.github.ewbankkit.util.F1;

/**
 * Represents an optional value.
 */
public final class Option<T> extends BaseData implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:02 Option.java NSI";

    @SuppressWarnings("unchecked")
    public static final Option NONE = new Option();

    private static final Option<Object> OPTION_OF_NULL = new Option<Object>(null);
    private static final long serialVersionUID = 1L;

    private final T value;

    /**
     * Constructor.
     */
    private Option() {
        this(null);
    }

    /**
     * Constructor.
     */
    private Option(T value) {
        this.value = value;
    }

    /**
     * Returns true if the option has the NONE value.
     */
    public boolean isNone() {
        return (this == NONE);
    }

    /**
     * Returns true if the option has a value that is not NONE.
     */
    @NotInToString
    public boolean isSome() {
        return !isNone();
    }

    /**
     * Returns the underlying value, or throws a NullPointerException if the value is NONE.
     */
    public T getValue() {
        if (isNone()) {
            throw new NullPointerException();
        }
        return value;
    }

    /**
     * Monadic bind.
     */
    @SuppressWarnings("unchecked")
    public <U> Option<U> bind(F1<T, Option<U>> f1) throws Exception {
        return isNone() ? (Option<U>)none() : f1.apply(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Option)) {
            return false;
        }

        Option rhs = (Option)o;
        if (isNone() && rhs.isSome()) {
            return false;
        }
        if (isSome() && rhs.isNone()) {
            return false;
        }
        return (value == null) ? (rhs.value == null) : value.equals(rhs.value);
    }

    @Override
    public int hashCode() {
        int result = 17;
        int c = (value == null) ? 0 : value.hashCode();
        result = 31 * result + c;

        return result;
    }

    /**
     * Returns an option that the NONE value.
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> none() {
        return (Option<T>)NONE;
    }

    /**
     * Returns an option that has a value that is not NONE.
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> some(T value) {
        if (value == null) {
            return (Option<T>)OPTION_OF_NULL;
        }
        return new Option<T>(value);
    }

    /**
     * Convert the option to an array of length 0 or 1.
     */
    public T[] toArray() {
        int length = isNone() ? 0 : 1;
        @SuppressWarnings("unchecked")
        T[] array = (T[])Array.newInstance((value == null) ? Object.class : value.getClass(), length);
        if (length > 0) {
            array[0] = value;
        }
        return array;
    }

    /**
     * Convert the option to a list of length 0 or 1.
     */
    public List<T> toList() {
        List<T> emptyList = Collections.emptyList();
        return isNone() ? emptyList : Collections.singletonList(value);
    }
}
