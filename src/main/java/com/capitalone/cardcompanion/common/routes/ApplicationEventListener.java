//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import com.capitalone.cardcompanion.common.ApplicationLocale;
import com.capitalone.cardcompanion.common.ExecutionContext;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.ws.rs.ext.Provider;

/**
 * Listens to application events.
 */
@Provider
public final class ApplicationEventListener implements org.glassfish.jersey.server.monitoring.ApplicationEventListener {
    /**
     * Process the application event.
     */
    @Override
    public void onEvent(ApplicationEvent applicationEvent) {}

    /**
     * Process a new request.
     */
    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        // Clear any lingering execution context.
        ExecutionContext.removeContextFromCurrentThread();
        // Clear any lingering application locale
        ApplicationLocale.removeApplicationLocaleFromCurrentThread();

        return null;
    }
}
