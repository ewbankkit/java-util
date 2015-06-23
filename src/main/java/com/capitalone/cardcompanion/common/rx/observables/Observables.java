//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.rx.observables;

import com.capitalone.cardcompanion.common.base.Either;
import com.capitalone.cardcompanion.common.base.Throwables;
import com.capitalone.cardcompanion.common.jaxrs.Responses;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Observable utilities.
 */
public final class Observables {
    private static final Logger LOGGER = LoggerFactory.getLogger(Observables.class);

    private Observables() {}

    /**
     * Returns an Observable that captures on any error from the source observable.
     */
    public static <T> Observable<Either<Throwable, T>> captureError(Observable<T> observable) {
        Preconditions.checkNotNull(observable);

        return observable.
            map(new Func1<T, Either<Throwable, T>>() {
                @Override
                public Either<Throwable, T> call(T t) {
                    return Either.right(t);
                }
            }).
            onErrorReturn(new Func1<Throwable, Either<Throwable, T>>() {
                @Override
                public Either<Throwable, T> call(Throwable throwable) {
                    return Either.left(throwable);
                }
            });
    }

    public static <T> Observable<T> flatten(Future<T[]> future) {
        Preconditions.checkNotNull(future);

        return Observable.from(future).flatMap(new Func1<T[], Observable<? extends T>>() {
            @Override
            public Observable<? extends T> call(T[] ts) {
                return Observable.from(ts);
            }
        });
    }

    /**
     * Converts a callable of an item to an Observable of item.
     */
    public static <T> Observable<T> fromCallable(final Callable<? extends T> callable) {
        Preconditions.checkNotNull(callable);

        return Observable.create(new OnSubscribe<T>() {
            /**
             * Invoked when Observable.subscribe is called.
             */
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    try {
                        subscriber.onNext(callable.call());
                        subscriber.onCompleted();
                    }
                    catch (Throwable e) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(e);
                        }
                    }
                }
            }
        });
    }

    /**
     * Returns an Observable that pushes an HTTP 404 Not Found error.
     */
    public static <T> Observable<T> notFoundError(String message) {
        return Observable.error(new WebApplicationException(Responses.notFound(message)));
    }

    /**
     * Returns an Observable that pushes an HTTP 404 Not Found error.
     */
    public static <T> Observable<T> notFoundError(Object message) {
        return Observable.error(new WebApplicationException(Responses.notFound(message)));
    }

    /**
     * Returns an Observable that pushes an HTTP 404 Not Found error.
     */
    public static <T> Observable<T> badRequest(Object message) {
        return Observable.error(new WebApplicationException(Responses.badRequest(message)));
    }

    /**
     * Returns an Observable that pushes an HTTP 403 Not Found error.
     */
    public static <T> Observable<T> forbidden(Object message) {
        return Observable.error(new WebApplicationException(Responses.forbidden(message)));
    }

    /**
     * Converts a Future of optional items to an Observable of items.
     */
    public static <T> Observable<T> required(Future<Optional<T>> future) {
        Preconditions.checkNotNull(future);

        return required(Observable.from(future));
    }

    /**
     * Converts an Observable of optional items to an Observable of items.
     */
    public static <T> Observable<T> required(Observable<Optional<T>> observable) {
        Preconditions.checkNotNull(observable);

        return observable.filter(new Func1<Optional<T>, Boolean>() {
            @Override
            public Boolean call(Optional<T> optional) {
                return ((optional != null) && optional.isPresent()) ? Boolean.TRUE : Boolean.FALSE;
            }
        }).map(new Func1<Optional<T>, T>() {
            @Override
            public T call(Optional<T> optional) {
                return optional.get();
            }
        });
    }

    /**
     * Returns an Observable that swallows any error from the Callable.
     */
    public static <T> Observable<Optional<T>> swallowError(Callable<Optional<T>> callable, @Nullable final String message) {
        Preconditions.checkNotNull(callable);

        return swallowError(fromCallable(callable), message);
    }

    /**
     * Returns an Observable that swallows any error from the Future.
     */
    public static <T> Observable<Optional<T>> swallowError(Future<Optional<T>> future, @Nullable final String message) {
        Preconditions.checkNotNull(future);

        return swallowError(Observable.from(future), message);
    }

    /**
     * Returns an Observable that swallows any error from the Observable.
     */
    public static <T> Observable<Optional<T>> swallowError(Observable<Optional<T>> observable, @Nullable final String message) {
        Preconditions.checkNotNull(observable);

        return observable.onErrorReturn(new Func1<Throwable, Optional<T>>() {
            @Override
            public Optional<T> call(Throwable throwable) {
                log(throwable, message);
                return Optional.absent();
            }
        });
    }

    private static void log(Throwable throwable, @Nullable String message) {
        if (message != null) {
            LOGGER.warn(message, Throwables.getUseful(throwable));
        }
    }
}
