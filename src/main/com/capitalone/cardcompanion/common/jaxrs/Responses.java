//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.net.URI;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.BAD_GATEWAY;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Static utility methods pertaining to Response instances.
 */
public final class Responses {
    private Responses() {}

    public static Response accepted() {
        return forStatus(ACCEPTED, Optional.absent());
    }

    public static Response badGateway(@Nullable Object entity) {
        return forStatus(BAD_GATEWAY, entity);
    }

    public static Response badRequest(@Nullable Object entity) {
        return forStatus(BAD_REQUEST, entity);
    }

    public static Response created() {
        return created(null);
    }

    public static Response created(@Nullable Object entity) {
        return forStatus(CREATED, entity);
    }

    public static Response created(URI location, @Nullable Object entity) {
        Preconditions.checkNotNull(location);

        return builder(CREATED, entity).location(location).build();
    }

    public static Response forbidden(@Nullable Object entity) {
        return forStatus(FORBIDDEN, entity);
    }

    public static Response internalServerError() {
        return internalServerError(null);
    }

    public static Response internalServerError(@Nullable Object entity) {
        return forStatus(INTERNAL_SERVER_ERROR, entity);
    }

    public static Response noContent() {
        return forStatus(NO_CONTENT, Optional.absent());
    }

    public static Response notFound() {
        return notFound(null);
    }

    public static Response notFound(@Nullable Object entity) {
        return forStatus(NOT_FOUND, entity);
    }

    public static Response notModified() {
        return forStatus(NOT_MODIFIED, Optional.absent());
    }

    public static Response ok() {
        return ok(null);
    }

    public static Response ok(@Nullable Object entity) {
        return forStatus(OK, entity);
    }

    public static Response ok(Object entity, MediaType mediaType) {
        Preconditions.checkNotNull(entity);
        Preconditions.checkNotNull(mediaType);

        return builder(OK, entity).type(mediaType).build();
    }

    public static Response serviceUnavailable(@Nullable Object entity) {
        return forStatus(SERVICE_UNAVAILABLE, entity);
    }

    public static Response unauthorized(@Nullable Object entity) {
        return forStatus(UNAUTHORIZED, entity);
    }

    public static Response unauthorized(String authorizationMethod, @Nullable Object entity) {
        Preconditions.checkNotNull(authorizationMethod);

        return builder(UNAUTHORIZED, entity).header(HttpHeaders.WWW_AUTHENTICATE, authorizationMethod).build();
    }

    public static Response forStatus(Status status) {
        Preconditions.checkNotNull(status);

        return forStatus(status, Optional.absent());
    }

    public static Response forStatus(Status status, Optional<?> optionalEntity) {
        Preconditions.checkNotNull(status);
        Preconditions.checkNotNull(optionalEntity);

        return forStatus(status, optionalEntity.orNull());
    }

    private static ResponseBuilder builder(Status status, @Nullable Object entity) {
        ResponseBuilder builder = Response.status(status);
        if (entity != null) {
            builder = builder.entity(entity);
            if (entity instanceof String) {
                builder = builder.type(TEXT_PLAIN_TYPE);
            }
        }
        return builder;
    }

    private static Response forStatus(Status status, @Nullable Object entity) {
        return builder(status, entity).build();
    }
}
