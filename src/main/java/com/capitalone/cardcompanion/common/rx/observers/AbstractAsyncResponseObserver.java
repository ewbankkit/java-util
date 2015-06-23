//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.rx.observers;

import com.capitalone.cardcompanion.common.base.Throwables;
import com.capitalone.cardcompanion.common.jaxrs.HttpStatusAndEntity;
import com.capitalone.cardcompanion.common.jaxrs.Responses;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observer;

import javax.annotation.Nullable;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.BAD_GATEWAY;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.GATEWAY_TIMEOUT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Abstract base class for JAX-RS AsyncResponse observers.
 */
public abstract class AbstractAsyncResponseObserver<T> implements Observer<T> {
    protected static final Map<Status, Status> DEFAULT_HTTP_STATUS_CODE_MAP = ImmutableMap.<Status, Status>builder().
        put(BAD_REQUEST, BAD_REQUEST).                     // 400 -> 400
        put(UNAUTHORIZED, UNAUTHORIZED).                   // 401 -> 401
        put(FORBIDDEN, FORBIDDEN).                         // 403 -> 403
        put(NOT_FOUND, NOT_FOUND).                         // 404 -> 404
        put(CONFLICT, CONFLICT).                           // 409 -> 409
        put(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR). // 500 -> 500
        put(BAD_GATEWAY, BAD_GATEWAY).                     // 502 -> 502
        put(SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE).     // 503 -> 503
        put(GATEWAY_TIMEOUT, GATEWAY_TIMEOUT).             // 504 -> 504
        build();

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAsyncResponseObserver.class);

    private final AsyncResponse       asyncResponse;
    private final Builder<T>          builder;
    private final Map<Status, Status> httpStatusCodeMap;
    private final Optional<String>    notFoundMessage;

    /**
     * Constructor.
     */
    protected AbstractAsyncResponseObserver(AsyncResponse asyncResponse) {
        this(asyncResponse, null);
    }

    /**
     * Constructor.
     */
    protected AbstractAsyncResponseObserver(AsyncResponse asyncResponse, @Nullable String notFoundMessage) {
        this(asyncResponse, DEFAULT_HTTP_STATUS_CODE_MAP, notFoundMessage);
    }

    /**
     * Constructor.
     */
    protected AbstractAsyncResponseObserver(AsyncResponse asyncResponse, Map<Status, Status> httpStatusCodeMap, @Nullable String notFoundMessage) {
        Preconditions.checkNotNull(asyncResponse);
        Preconditions.checkNotNull(httpStatusCodeMap);

        this.asyncResponse = asyncResponse;
        builder = ImmutableList.builder();
        this.httpStatusCodeMap = httpStatusCodeMap;
        this.notFoundMessage = Optional.fromNullable(notFoundMessage);
    }

    /**
     * Notifies the Observer that the Observable has finished sending push-based notifications.
     */
    @Override
    public abstract void onCompleted();

    /**
     * Notifies the Observer that the Observable has experienced an error condition.
     */
    @Override
    public void onError(Throwable e) {
        Response response = Responses.internalServerError();
        Optional<HttpStatusAndEntity> optionalHttpStatusAndEntity = HttpStatusAndEntity.getHttpStatusAndEntity(e);
        if (optionalHttpStatusAndEntity.isPresent()) {
            HttpStatusAndEntity httpStatusAndEntity = optionalHttpStatusAndEntity.get();
            Status httpStatus = httpStatusCodeMap.get(httpStatusAndEntity.getStatus());
            if (httpStatus != null) {
                response = Responses.forStatus(httpStatus, httpStatusAndEntity.getOptionalEntity());
            }
        }
        else {
            LOGGER.error("Observable experienced an error", Throwables.getUseful(e));
        }

        resume(response);
    }

    /**
     * Provides the Observer with new data.
     */
    @Override
    public final void onNext(T args) {
        builder.add(args);
    }

    protected final Iterable<T> data() {
        return builder.build();
    }

    /**
     * Returns the first data item.
     */
    protected final Optional<T> item() {
        return Optional.fromNullable(Iterables.getFirst(data(), null));
    }

    /**
     * Returns an HTTP 404 Not Found response.
     */
    protected final Response notFound() {
        return Responses.notFound(notFoundMessage.orNull());
    }

    protected final void resume(Response response) {
        asyncResponse.resume(response);
    }
}
