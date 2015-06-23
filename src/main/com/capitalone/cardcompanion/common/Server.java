//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common;

/**
 * Starts/stops services required by the application
 */
public interface Server {

    /** a name to use to register the server health checks with the health check registry */
    public final static String HEALTH_CHECK_NAME = "server";

    void start() throws InitializationException;
    void stop();

}
