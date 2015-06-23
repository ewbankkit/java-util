//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import com.capitalone.cardcompanion.common.Config;

import javax.ws.rs.ext.InterceptorContext;

import static com.capitalone.cardcompanion.common.routes.ServerLoggingFilter.REQUEST_ID_PROPERTY;
import static com.capitalone.cardcompanion.common.routes.ServerLoggingFilter.STRING_BUILDER_PROPERTY;

/**
 * Abstract base class for server entity logging interceptors.
 */
abstract class AbstractServerEntityLoggingInterceptor {
    private static final boolean GLOBAL_LOG_ENTITY = Config.getInstance().getBoolean("server.logEntity", false);

    @SuppressWarnings("UnnecessaryUnboxing")
    protected static void logEntity(InterceptorContext context, String requestOrResponse) {
        Object property = context.getProperty(STRING_BUILDER_PROPERTY);
        if ((property != null) && GLOBAL_LOG_ENTITY) {
            final long requestId = ((Long)context.getProperty(REQUEST_ID_PROPERTY)).longValue();

            // This string is parsed by Splunk.
            // Don't change without consultation.
            StringBuilder sb = new StringBuilder().
                append("Server ").
                append(requestOrResponse).
                append(' ').
                append(requestId).
                append(" entity:\n").
                append((CharSequence)property);
            ServerLoggingFilter.log(sb);
        }
    }
}
