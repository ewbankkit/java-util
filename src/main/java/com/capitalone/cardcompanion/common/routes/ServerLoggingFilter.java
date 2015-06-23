//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import com.capitalone.cardcompanion.common.jaxrs.AbstractLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Server logging filter.
 */
@Provider
@PreMatching
@Priority(FilterPriorities.SERVER_LOGGING)
@SuppressWarnings("unused")
public final class ServerLoggingFilter extends AbstractLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final String     CLASS_NAME = ServerLoggingFilter.class.getName();
    private static final Logger     LOGGER     = LoggerFactory.getLogger(CLASS_NAME);
    private static final AtomicLong REQUEST_ID = new AtomicLong(System.currentTimeMillis()); // Unique starting value.

    public static final String EXECUTION_CONTEXT_PROPERTY = CLASS_NAME + ".executionContext";
    public static final String REQUEST_ID_PROPERTY        = CLASS_NAME + ".requestId";
    public static final String STRING_BUILDER_PROPERTY    = CLASS_NAME + ".stringBuilder";
    public static final String APPLICATION_LOCALE_PROPERTY = CLASS_NAME + ".applicationLocale";
    /**
     * Filter method called before a request has been dispatched to a resource.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final long requestId = REQUEST_ID.getAndIncrement();
        requestContext.setProperty(REQUEST_ID_PROPERTY, Long.valueOf(requestId));

        final String prefix = "+> ";
        // This string is parsed by Splunk.
        // Don't change without consultation.
        final StringBuilder sbHeader = new StringBuilder().
            append("Server received request ").
            append(requestId).
            append(":\n").
            append(prefix).
            append(requestContext.getMethod()).
            append(' ').
            append(requestContext.getUriInfo().getRequestUri().toASCIIString()).
            append('\n');
        appendHeaders(sbHeader, prefix, requestContext.getHeaders());

        if (requestContext.hasEntity()) {
            StringBuilder sbEntity = new StringBuilder();

            InputStream inputStream = entityInputStream(requestContext.getEntityStream(), sbEntity, requestContext.getMediaType());
            requestContext.setEntityStream(inputStream);

            requestContext.setProperty(STRING_BUILDER_PROPERTY, sbEntity);
            // Entity logging occurs in aroundReadFrom.
        }

        log(sbHeader);
    }

    /**
     * Filter method called after a response has been provided for a request.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // changing the output stream of the streaming responses (for metrics gathering) will break the stream, and we also
        // don't want to log every metrics update
        if (isStreamingResponse(responseContext)) {
            return;
        }

        final long requestId = ((Long)requestContext.getProperty(REQUEST_ID_PROPERTY)).longValue();

        Response.StatusType statusType = responseContext.getStatusInfo();
        final String prefix = "<+ ";
        // This string is parsed by Splunk.
        // Don't change without consultation.
        StringBuilder sbHeader = new StringBuilder().
            append("Server sending response ").
            append(requestId).
            append(":\n").
            append(prefix).
            append(statusType.getStatusCode()).
            append(' ').
            append(statusType.getReasonPhrase()).
            append('\n');
        appendHeaders(sbHeader, prefix, responseContext.getStringHeaders());

        if (responseContext.hasEntity()) {
            StringBuilder sbEntity = new StringBuilder();
            OutputStream outputStream = entityOutputStream(responseContext.getEntityStream(), sbEntity, responseContext.getMediaType());
            responseContext.setEntityStream(outputStream);

            requestContext.setProperty(STRING_BUILDER_PROPERTY, sbEntity);
            // Entity logging occurs in aroundWriteTo.
        }

        log(sbHeader);
    }

    private static boolean isStreamingResponse(ContainerResponseContext responseContext) {
        MediaType responseMediaType = responseContext.getMediaType();
        return (responseMediaType != null) &&
               "text".equalsIgnoreCase(responseMediaType.getType()) &&
               "event-stream".equalsIgnoreCase(responseMediaType.getSubtype());
    }

    public static void log(StringBuilder sb) {
        log(LOGGER, sb);
    }
}
