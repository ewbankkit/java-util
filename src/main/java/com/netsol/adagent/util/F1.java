/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import java.util.concurrent.Callable;

/**
 * Represents a function from A to B.
 */
public abstract class F1<A, B> {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:58 F1.java NSI";

    public abstract B apply(A a) throws Exception;

    /**
     * Function composition.
     * This function is applied last.
     */
    public final <C> F1<C, B> o(final F1<C, A> g) {
        return new F1<C, B>() {
            @Override
            public B apply(C c) throws Exception {
                return F1.this.apply(g.apply(c));
            }};
    }

    /**
     * Flipped function composition.
     * This function is applied first.
     */
    public final <C> F1<A, C> andThen(final F1<B, C> g) {
        return g.o(this);
    }

    /**
     * Partial reduction.
     */
    public final Callable<B> preduce(final A a) {
        return new Callable<B>() {
            public B call() throws Exception {
                return F1.this.apply(a);
            }};
    }
}
