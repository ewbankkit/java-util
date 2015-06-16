/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.BaseData.coalesce;

/**
 * Determines a true or false value for a given input.
 */
public abstract class Predicate<T> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:12 Predicate.java NSI";

    public abstract boolean apply(T t);

    public Predicate<T> and(final Predicate<T> predicate) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return Predicate.this.apply(t) && predicate.apply(t);
            }};
    }

    public static <T> Predicate<T> and(final Predicate<T> predicate1, final Predicate<T> predicate2) {
        return predicate1.and(predicate2);
    }

    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return !Predicate.this.apply(t);
            }};
    }

    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return predicate.not();
    }

    public Predicate<T> or(final Predicate<T> predicate) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return Predicate.this.apply(t)|| predicate.apply(t);
            }};
    }

    public static <T> Predicate<T> or(final Predicate<T> predicate1, final Predicate<T> predicate2) {
        return predicate1.or(predicate2);
    }

    public F1<T, Boolean> toF1() {
        return new F1<T, Boolean>() {
            @Override
            public Boolean apply(T t) throws Exception {
                return Boolean.valueOf(Predicate.this.apply(t));
            }};
    }

    public static <T> Predicate<T> fromF1(final F1<T, Boolean> f1) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                Boolean result = null;
                try {
                    result = f1.apply(t);
                }
                catch (Exception ex) {}
                return coalesce(result, Boolean.FALSE).booleanValue();
            }};
    }

    /**
     * Return a predicate that always evaluates to false.
     */
    public static <T> Predicate<T> alwaysFalse() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return false;
            }};
    }

    /**
     * Return a predicate that always evaluates to true.
     */
    public static <T> Predicate<T> alwaysTrue() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return true;
            }};
    }

    /**
     * Return a predicate that evaluates to true if the object reference being tested is null.
     */
    public static <T> Predicate<T> isNull() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return t == null;
            }};
    }

    /**
     * Return a predicate that evaluates to true if the object reference being tested is not.
     */
    public static <T> Predicate<T> notNull() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return t != null;
            }};
    }
}
