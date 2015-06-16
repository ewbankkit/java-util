/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.beans;

import java.io.Serializable;

/**
 * Represents a triple.
 */
public final class Triple<First, Second, Third> extends Tuple implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:09 Triple.java NSI";

    private static final long serialVersionUID = 1L;

    private static final Triple<Object, Object, Object> TRIPLE_OF_NULLS = new Triple<Object, Object, Object>(null, null, null);

    private final First first;
    private final Second second;
    private final Third third;

    /**
     * Constructor.
     */
    private Triple(First first, Second second, Third third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    public Third getThird() {
        return third;
    }

    @Override
    public int arity() {
        return 3;
    }

    @SuppressWarnings("unchecked")
    public static <First, Second, Third> Triple<First, Second, Third> from(First first, Second second, Third third) {
        if ((first == null) && (second == null) && (third == null)) {
            return (Triple<First, Second, Third>)Triple.TRIPLE_OF_NULLS;
        }
        return new Triple<First, Second, Third>(first, second, third);
    }

    public static <First, Second, Third> Triple<First, Second, Third> from(Pair<First, Second> pair, Third third) {
        return from((pair == null) ? null : pair.getFirst(), (pair == null) ? null : pair.getSecond(), third);
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0: return first;
            case 1: return second;
            case 2: return third;
            default: throw new IndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Triple)) {
            return false;
        }
        return equals(this, (Triple)o);
    }
}
