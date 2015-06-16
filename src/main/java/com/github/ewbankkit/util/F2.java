/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

/**
 * Represents a function from A and B to C.
 */
public abstract class F2<A, B, C> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:58 F2.java NSI";

    public abstract C apply(A a, B b) throws Exception;

    /**
     * Partial reduction.
     */
    public final F1<B, C> preduce(final A a) {
        return new F1<B, C>() {
            public C apply(B b) throws Exception {
                return F2.this.apply(a, b);
            }};
    }
}
