//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.base;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import groovy.lang.Closure;

import javax.annotation.Nullable;

/**
 * Static utility methods pertaining to Closure instances.
 */
public final class Closures {
    private Closures() {}

    /**
     * Returns a function equivalent to the specified closure.
     */
    public static <F, T> Function<F, T> toFunction(final Closure<? extends T> closure) {
        Preconditions.checkNotNull(closure);

        return new Function<F, T>() {
            /**
             * Returns the result of applying this function to input.
             */
            @Nullable
            @Override
            public T apply(@Nullable F input) {
                return closure.call(input);
            }
        };
    }
}
