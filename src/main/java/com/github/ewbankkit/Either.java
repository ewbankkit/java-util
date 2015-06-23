//
// Kit's Java Utils.
//

package com.github.ewbankkit;

import java.util.Optional;

/**
 * Represents a value of one of two possible types.
 */
public abstract class Either<A, B> {
    private final Optional<A> left;
    private final Optional<B> right;

    /**
     * Constructor.
     */
    private Either(Optional<A> left, Optional<B> right) {
        this.left = left;
        this.right = right;
    }

    public final A getLeft() {
        return left.get();
    }

    public final B getRight() {
        return right.get();
    }

    @ReflectiveRepresentation.Ignore
    public final boolean isLeft() {
        return left.isPresent();
    }

    @ReflectiveRepresentation.Ignore
    public final boolean isRight() {
        return right.isPresent();
    }

    public static <A, B> Either<A, B> left(A a) {
        return new Left<>(a);
    }

    public static <A, B> Either<A, B> right(B b) {
        return new Right<>(b);
    }

    @Override
    public String toString() {
        return ReflectiveRepresentation.toString(this);
    }

    private static final class Left<A, B> extends Either<A, B> {
        /**
         * Constructor.
         */
        public Left(A a) {
            super(Optional.of(a), Optional.<B>empty());
        }
    }

    private static final class Right<A, B> extends Either<A, B> {
        /**
         * Constructor.
         */
        public Right(B b) {
            super(Optional.<A>empty(), Optional.of(b));
        }
    }
}
