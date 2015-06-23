//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs;

import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation;
import com.capitalone.cardcompanion.common.base.Throwables;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Represents HTTP status and optional entity.
 */
public final class HttpStatusAndEntity {
    private final Optional<Object> optionalEntity;
    private final Status           status;

    /**
     * Constructor.
     */
    public HttpStatusAndEntity(int statusCode, Optional<Object> optionalEntity) {
        status = Status.fromStatusCode(statusCode);
        this.optionalEntity = optionalEntity;
    }

    /**
     * Returns any HTTP status from an exception.
     */
    public static Optional<Status> getHttpStatus(Throwable e) {
        Optional<HttpStatusAndEntity> optionalHttpStatusAndEntity = getHttpStatusAndEntity(e);
        return optionalHttpStatusAndEntity.isPresent() ?
            Optional.of(optionalHttpStatusAndEntity.get().getStatus())
            :
            Optional.<Status>absent();
    }

    /**
     * Returns any HTTP status and entity from an exception.
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static Optional<HttpStatusAndEntity> getHttpStatusAndEntity(Throwable e) {
        Preconditions.checkNotNull(e);

        e = Throwables.getUseful(e);
        if (e instanceof WebApplicationException) {
            Response response = ((WebApplicationException)e).getResponse();
            return Optional.of(new HttpStatusAndEntity(response.getStatus(), Optional.fromNullable(response.getEntity())));
        }
        return Optional.absent();
    }

    public Optional<Object> getOptionalEntity() {
        return optionalEntity;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return ReflectiveRepresentation.toString(this);
    }
}
