//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.routes;

import com.capitalone.cardcompanion.common.Descriptor;
import com.capitalone.cardcompanion.common.HealthChecks;
import com.capitalone.cardcompanion.common.Server;
import com.capitalone.cardcompanion.common.jaxrs.Responses;
import com.codahale.metrics.health.HealthCheck;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Request filter ensuring that the server started successfully.
 */
@SuppressWarnings("unused")
@Priority(FilterPriorities.SERVER_HEALTH_CHECK)
@Provider
public final class ServerHealthCheckFilter implements ContainerRequestFilter {
    /**
     * Called before a request has been dispatched to a resource.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        HealthCheck.Result serverHealth = HealthChecks.getInstance().getHealthCheckRegistry().runHealthCheck(Server.HEALTH_CHECK_NAME);
        if (!serverHealth.isHealthy()) {
            requestContext.abortWith(Responses.serviceUnavailable(Descriptor.getInstance().getName() + " service unavailable"));
        }
    }
}
