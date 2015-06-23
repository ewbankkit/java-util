//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import com.capitalone.cardcompanion.common.jaxrs.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Maps Java exceptions to Response.
 */
@Provider
public final class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMapper.class);

    /**
     * Map an exception to a Response.
     */
    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof WebApplicationException) {
            return ((WebApplicationException)throwable).getResponse();
        }
        LOGGER.error("Unhandled exception", throwable);
        return Responses.internalServerError(throwable.getMessage());
    }
}
