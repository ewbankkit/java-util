//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common;

import com.codahale.metrics.MetricRegistry;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Represents the application's metric registry.
 */
@ThreadSafe
public final class Metrics {
    private final MetricRegistry metricRegistry;

    /*
     * Constructor.
     */
    private Metrics() {
        metricRegistry = new MetricRegistry();
    }

    /**
     * Returns the single instance.
     */
    public static Metrics getInstance() {
        return LazyHolder.INSTANCE;
    }

    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public final static Metrics INSTANCE = new Metrics();
    }
}
