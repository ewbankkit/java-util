/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.io.Serializable;

/**
 * Represents a value of one of two possible types.
 */
public abstract class Either<A, B> extends BaseData implements Serializable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:53 Either.java NSI";

    private static final long serialVersionUID = 1L;

    private final Option<A> left;
    private final Option<B> right;

    private Either(Option<A> left, Option<B> right) {
        this.left = left;
        this.right = right;
    }

    public final Option<A> getLeft() {
        return left;
    }

    public final Option<B> getRight() {
        return right;
    }

    @NotInToString
    public final boolean isLeft() {
        return left.isSome();
    }

    @NotInToString
    public final boolean isRight() {
        return right.isSome();
    }

    public static <A, B> Either<A, B> left(A a) {
        return new Left<A, B>(a);
    }

    public static <A, B> Either<A, B> right(B b) {
        return new Right<A, B>(b);
    }

    private static final class Left<A, B> extends Either<A, B> {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public Left(A a) {
            super(Option.some(a), (Option<B>)Option.none());
        }
    }

    private static final class Right<A, B> extends Either<A, B> {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public Right(B b) {
            super((Option<A>)Option.none(), Option.some(b));
        }
    }
}
