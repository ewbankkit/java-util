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
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Converting AsyncResponse observer that optionally creates a new resource.
 */
public class ResourceCreatingAsyncResponseObserver<T, U> extends ConvertingAsyncResponseObserver<T, U> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCreatingAsyncResponseObserver.class);

    private final Function<T, Optional<URI>> locationSupplier;

    /**
     * Constructor.
     */
    public ResourceCreatingAsyncResponseObserver(AsyncResponse asyncResponse, Function<T, U> converter, Function<T, Optional<URI>> locationSupplier) {
        super(asyncResponse, converter);
        Preconditions.checkNotNull(locationSupplier);
        this.locationSupplier = locationSupplier;
    }

    /**
     * Notifies the Observer that the Observable has finished sending push-based notifications.
     */
    @Override
    public void onCompleted() {
        LOGGER.trace("Observable finished");

        Response response;
        Optional<U> optionalResponseEntity = responseEntity();
        if (optionalResponseEntity.isPresent()) {
            U responseEntity = optionalResponseEntity.get();
            Optional<URI> optionalLocation = locationSupplier.apply(item().get());
            assert optionalLocation != null;
            response = optionalLocation.isPresent() ?
                Responses.created(optionalLocation.get(), responseEntity)
                :
                Responses.ok(responseEntity);
        }
        else {
            response = notFound();
        }
        resume(response);
    }
}
