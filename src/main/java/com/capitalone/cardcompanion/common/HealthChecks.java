//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common;

import com.codahale.metrics.health.HealthCheckRegistry;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Represents the application's health checks registry.
 */
@ThreadSafe
public final class HealthChecks {
    private final HealthCheckRegistry healthCheckRegistry;

    /*
     * Constructor.
     */
    private HealthChecks() {
        healthCheckRegistry = new HealthCheckRegistry();
    }

    /**
     * Returns the single instance.
     */
    public static HealthChecks getInstance() {
        return LazyHolder.INSTANCE;
    }

    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public final static HealthChecks INSTANCE = new HealthChecks();
    }
}
