//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

import com.capitalone.cardcompanion.common.Config;
import com.capitalone.cardcompanion.common.jaxrs.AbstractLoggingFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.InterceptorContext;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Client logging filter.
 */
@Priority(Integer.MIN_VALUE) // Always invoked first.
final class RestClientLoggingFilter extends AbstractLoggingFilter implements ClientRequestFilter, ClientResponseFilter, ReaderInterceptor, WriterInterceptor {
    private static final String     CLASS_NAME              = RestClientLoggingFilter.class.getName();
    private static final boolean    GLOBAL_LOG_ENTITY       = Config.getInstance().getBoolean("restClient.logEntity", false);
    private static final Logger     LOGGER                  = LoggerFactory.getLogger(CLASS_NAME);
    private static final AtomicLong REQUEST_ID              = new AtomicLong(System.currentTimeMillis()); // Unique starting value.
    private static final String     REQUEST_ID_PROPERTY     = CLASS_NAME + ".requestId";
    private static final String     STRING_BUILDER_PROPERTY = CLASS_NAME + ".stringBuilder";

    public static final String LOG_REQUEST_ENTITY_PROPERTY  = CLASS_NAME + ".logRequestEntity";
    public static final String LOG_RESPONSE_ENTITY_PROPERTY = CLASS_NAME + ".logResponseEntity";

    /**
     * Interceptor method wrapping calls to MessageBodyReader.readFrom().
     */
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        logEntity(context, "response");

        return context.proceed();
    }

    /**
     * Interceptor method wrapping calls to MessageBodyWriter.writeTo().
     */
    @Override
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
        context.proceed();

        logEntity(context, "request");
    }

    /**
     * Filter method called before a request has been dispatched to a client transport layer.
     */
    @SuppressWarnings({"UnnecessaryBoxing", "UnnecessaryUnboxing"})
    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        final long requestId = REQUEST_ID.getAndIncrement();
        requestContext.setProperty(REQUEST_ID_PROPERTY, Long.valueOf(requestId));

        final String prefix = "-> ";
        // This string is parsed by Splunk.
        // Don't change without consultation.
        final StringBuilder sbHeader = new StringBuilder().
            append("Sending REST client request ").
            append(requestId).
            append(":\n").
            append(prefix).
            append(requestContext.getMethod()).
            append(' ').
            append(requestContext.getUri().toASCIIString()).
            append('\n');
        appendHeaders(sbHeader, prefix, requestContext.getStringHeaders());

        if (requestContext.hasEntity()) {
            StringBuilder sbEntity = new StringBuilder();
            OutputStream outputStream = isLogEntity(requestContext, LOG_REQUEST_ENTITY_PROPERTY) ?
                entityOutputStream(requestContext.getEntityStream(), sbEntity, requestContext.getMediaType()) :
                redactedOutputStream(requestContext.getEntityStream(), sbEntity);
            requestContext.setEntityStream(outputStream);

            // Logging occurs in aroundWriteTo.
            requestContext.setProperty(STRING_BUILDER_PROPERTY, sbEntity);
        }

        log(sbHeader);
    }

    /**
     * Filter method called after a response has been provided for a request (either by a request filter or when the HTTP invocation returns.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) throws IOException {
        final long requestId = ((Long)requestContext.getProperty(REQUEST_ID_PROPERTY)).longValue();

        Response.StatusType statusType = responseContext.getStatusInfo();
        final String prefix = "<- ";
        // This string is parsed by Splunk.
        // Don't change without consultation.
        StringBuilder sbHeader = new StringBuilder().
            append("Received REST client response ").
            append(requestId).
            append(":\n").
            append(prefix).
            append(statusType.getStatusCode()).
            append(' ').
            append(statusType.getReasonPhrase()).
            append('\n');
        appendHeaders(sbHeader, prefix, responseContext.getHeaders());

        if (responseContext.hasEntity()) {
            StringBuilder sbEntity = new StringBuilder();
            InputStream inputStream = entityInputStream(responseContext.getEntityStream(), sbEntity, responseContext.getMediaType());
            responseContext.setEntityStream(inputStream);

            if (isLogEntity(requestContext, LOG_RESPONSE_ENTITY_PROPERTY)) {
                requestContext.setProperty(STRING_BUILDER_PROPERTY, sbEntity);
                // Logging occurs in aroundReadFrom.
            }
        }

        log(sbHeader);
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    private boolean isLogEntity(ClientRequestContext requestContext, String name) {
        // Request-level default is to log entities.
        return BooleanUtils.toBooleanDefaultIfNull((Boolean)requestContext.getProperty(name), true);
    }

    private static void log(StringBuilder sb) {
        log(LOGGER, sb);
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    private static void logEntity(InterceptorContext context, String requestOrResponse) {
        Object property = context.getProperty(STRING_BUILDER_PROPERTY);
        if ((property != null) && GLOBAL_LOG_ENTITY) {
            final long requestId = ((Long)context.getProperty(REQUEST_ID_PROPERTY)).longValue();

            // This string is parsed by Splunk.
            // Don't change without consultation.
            StringBuilder sb = new StringBuilder().
                append("REST client ").
                append(requestOrResponse).
                append(' ').
                append(requestId).
                append(" entity:\n").
                append((CharSequence)property);
            log(sb);
        }
    }
}
