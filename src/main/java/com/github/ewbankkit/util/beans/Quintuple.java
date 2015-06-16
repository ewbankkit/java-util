/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import java.io.Serializable;

/**
 * Represents a quintuple.
 */
public final class Quintuple<First, Second, Third, Fourth, Fifth> extends Tuple implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:05 Quintuple.java NSI";

    private static final long serialVersionUID = 1L;

    private static final Quintuple<Object, Object, Object, Object, Object> QUINTUPLE_OF_NULLS = new Quintuple<Object, Object, Object, Object, Object>(null, null, null, null, null);

    private final First first;
    private final Second second;
    private final Third third;
    private final Fourth fourth;
    private final Fifth fifth;

    /**
     * Constructor.
     */
    private Quintuple(First first, Second second, Third third, Fourth fourth, Fifth fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
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

    public Fifth getFifth() {
        return fifth;
    }

    @Override
    public int arity() {
        return 5;
    }

    @SuppressWarnings("unchecked")
    public static <First, Second, Third, Fourth, Fifth> Quintuple<First, Second, Third, Fourth, Fifth> from(First first, Second second, Third third, Fourth fourth, Fifth fifth) {
        if ((first == null) && (second == null) && (third == null) && (fourth == null) && (fifth == null)) {
            return (Quintuple<First, Second, Third, Fourth, Fifth>)Quintuple.QUINTUPLE_OF_NULLS;
        }
        return new Quintuple<First, Second, Third, Fourth, Fifth>(first, second, third, fourth, fifth);
    }

    public static <First, Second, Third, Fourth, Fifth> Quintuple<First, Second, Third, Fourth, Fifth> from(Quadruple<First, Second, Third, Fourth> quadruple, Fifth fifth) {
        return from((quadruple == null) ? null : quadruple.getFirst(), (quadruple == null) ? null : quadruple.getSecond(), (quadruple == null) ? null : quadruple.getThird(), (quadruple == null) ? null : quadruple.getFourth(), fifth);
    }

    @Override
    public Object get(int index) {
        switch (index) {
            case 0: return first;
            case 1: return second;
            case 2: return third;
            case 3: return fourth;
            case 4: return fifth;
            default: throw new IndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quintuple)) {
            return false;
        }
        return equals(this, (Quintuple)o);
    }
}
