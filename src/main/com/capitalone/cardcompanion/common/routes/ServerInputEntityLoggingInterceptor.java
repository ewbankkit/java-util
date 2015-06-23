//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

/**
 * Input entity logging interceptor.
 */
@Provider
@LogInputEntity
@Priority(Integer.MIN_VALUE) // Always invoked first.
public final class ServerInputEntityLoggingInterceptor extends AbstractServerEntityLoggingInterceptor implements ReaderInterceptor {
    /**
     * Interceptor method wrapping calls to MessageBodyReader.readFrom().
     */
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        logEntity(context, "request");

        return context.proceed();
    }
}
