/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import java.io.Serializable;

/**
 * Represents a pair.
 */
public final class Pair<First, Second> extends Tuple implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:03 Pair.java NSI";

    private static final long serialVersionUID = 1L;

    private static final Pair<Object, Object> PAIR_OF_NULLS = new Pair<Object, Object>(null, null);

    private final First first;
    private final Second second;

    /**
     * Constructor.
     */
    private Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    @Override
    public int arity() {
        return 2;
    }

    @SuppressWarnings("unchecked")
    public static <First, Second> Pair<First, Second> from(First first, Second second) {
        if ((first == null) && (second == null)) {
            return (Pair<First, Second>)Pair.PAIR_OF_NULLS;
        }
        return new Pair<First, Second>(first, second);
    }

    public static <First, Second> Pair<First, Second> from(Singleton<First> singleton, Second second) {
        return from((singleton == null) ? null : singleton.getFirst(), second);
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0: return first;
            case 1: return second;
            default: throw new IndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        return equals(this, (Pair)o);
    }
}
