/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import java.io.Serializable;

/**
 * Represents a quadruple.
 */
public final class Quadruple<First, Second, Third, Fourth> extends Tuple implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:05 Quadruple.java NSI";

    private static final long serialVersionUID = 1L;

    private static final Quadruple<Object, Object, Object, Object> QUADRUPLE_OF_NULLS = new Quadruple<Object, Object, Object, Object>(null, null, null, null);

    private final First first;
    private final Second second;
    private final Third third;
    private final Fourth fourth;

    /**
     * Constructor.
     */
    private Quadruple(First first, Second second, Third third, Fourth fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
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

    public Fourth getFourth() {
        return fourth;
    }

    @Override
    public int arity() {
        return 4;
    }

    @SuppressWarnings("unchecked")
    public static <First, Second, Third, Fourth> Quadruple<First, Second, Third, Fourth> from(First first, Second second, Third third, Fourth fourth) {
        if ((first == null) && (second == null) && (third == null) && (fourth == null)) {
            return (Quadruple<First, Second, Third, Fourth>)Quadruple.QUADRUPLE_OF_NULLS;
        }
        return new Quadruple<First, Second, Third, Fourth>(first, second, third, fourth);
    }

    public static <First, Second, Third, Fourth> Quadruple<First, Second, Third, Fourth> from(Triple<First, Second, Third> triple, Fourth fourth) {
        return from((triple == null) ? null : triple.getFirst(), (triple == null) ? null : triple.getSecond(), (triple == null) ? null : triple.getThird(), fourth);
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0: return first;
            case 1: return second;
            case 2: return third;
            case 3: return fourth;
            default: throw new IndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quadruple)) {
            return false;
        }
        return equals(this, (Quadruple)o);
    }
}
