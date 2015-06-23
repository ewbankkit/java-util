//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.base;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Static utility methods pertaining to Future instances.
 */
public final class Futures {
    /**
     * Constructor.
     */
    private Futures() {}

    /**
     * Returns a Future that gets an optional value.
     */
    public static <T> Future<Optional<T>> optionalFuture(final Future<? extends T> future) {
        Preconditions.checkNotNull(future);

        return transform(future, new Function<T, Optional<T>>() {
            @Nullable
            @Override
            public Optional<T> apply(@Nullable T input) {
                return Optional.fromNullable(input);
            }
        });
    }

    /**
     * Returns a Future that gets its value synchronously.
     */
    public static <T> Future<T> synchronousFuture(final Callable<? extends T> callableOfT) {
        Preconditions.checkNotNull(callableOfT);

        return new Future<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                try {
                    return callableOfT.call();
                }
                catch (Exception ex) {
                    throw new ExecutionException(ex);
                }
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns a Future that transforms another future.
     */
    public static <A, B> Future<B> transform(final Future<A> future, final Function<? super A, ? extends B> function) {
        Preconditions.checkNotNull(future);
        Preconditions.checkNotNull(function);

        return new Future<B>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }

            @Override
            public B get() throws InterruptedException, ExecutionException {
                return get(future.get());
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public B get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return get(future.get(timeout, unit));
            }

            private B get(A a) throws ExecutionException {
                try {
                    return function.apply(a);
                }
                catch (RuntimeException ex) {
                    Throwable cause = ex.getCause();
                    if (cause != null) {
                        throw new ExecutionException(cause);
                    }
                    throw ex;
                }
            }
        };
    }
}
