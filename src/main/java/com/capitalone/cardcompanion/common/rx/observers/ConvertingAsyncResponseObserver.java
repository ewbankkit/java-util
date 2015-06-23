//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.rx.observers;

import com.capitalone.cardcompanion.common.jaxrs.Responses;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.AsyncResponse;

/**
 * AsyncResponse observer that converts a single item to a response entity.
 */
public class ConvertingAsyncResponseObserver<T, U> extends AbstractAsyncResponseObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertingAsyncResponseObserver.class);

    private final Function<T, U> converter;

    /**
     * Constructor.
     */
    public ConvertingAsyncResponseObserver(AsyncResponse asyncResponse, Function<T, U> converter) {
        super(asyncResponse);
        Preconditions.checkNotNull(converter);
        this.converter = converter;
    }

    /**
     * Notifies the Observer that the Observable has finished sending push-based notifications.
     */
    @Override
    public void onCompleted() {
        LOGGER.trace("Observable finished");

        Optional<U> optionalResponseEntity = responseEntity();
        resume(optionalResponseEntity.isPresent() ? Responses.ok(optionalResponseEntity.get()) : notFound());
    }

    /**
     * Returns the response entity.
     */
    protected Optional<U> responseEntity() {
        Optional<T> item = item();
        return item.isPresent() ? Optional.fromNullable(converter.apply(item.get())) : Optional.<U>absent();
    }
}
