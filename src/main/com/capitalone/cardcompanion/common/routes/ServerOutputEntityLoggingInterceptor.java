//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import com.capitalone.cardcompanion.common.ApplicationLocale;
import com.capitalone.cardcompanion.common.ExecutionContext;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

import static com.capitalone.cardcompanion.common.routes.ServerLoggingFilter.EXECUTION_CONTEXT_PROPERTY;
import static com.capitalone.cardcompanion.common.routes.ServerLoggingFilter.APPLICATION_LOCALE_PROPERTY;
/**
 * Output entity logging interceptor.
 */
@Provider
@LogOutputEntity
@Priority(Integer.MIN_VALUE) // Always invoked first.
public final class ServerOutputEntityLoggingInterceptor extends AbstractServerEntityLoggingInterceptor implements WriterInterceptor {
    /**
     * Interceptor method wrapping calls to MessageBodyWriter.writeTo().
     */
    @Override
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
        context.proceed();

        logEntity(context, "response");

        Object value = context.getProperty(EXECUTION_CONTEXT_PROPERTY);
        if (value != null) {
            ((ExecutionContext)value).shutdown();
        }

        Object applicationLocale = context.getProperty(APPLICATION_LOCALE_PROPERTY);
        if(applicationLocale != null)
        {
            ((ApplicationLocale)applicationLocale).shutdown();
        }
    }
}
