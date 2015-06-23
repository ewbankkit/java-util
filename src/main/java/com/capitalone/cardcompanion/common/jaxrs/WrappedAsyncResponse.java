//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs;

import com.capitalone.cardcompanion.common.Config;
import com.capitalone.cardcompanion.common.InboundCallType;
import com.capitalone.cardcompanion.common.Metrics;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer.Context;
import com.google.common.base.Preconditions;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Timed AsyncResponse object.
 */
public final class WrappedAsyncResponse implements AsyncResponse {
    private static final boolean METRICS_ENABLED = Config.getInstance().getBoolean("inboundCall.metrics.enabled", false);

    private final AsyncResponse asyncResponse;
    private       boolean       finished;
    private final String        inboundCallType;
    private final Context       timer;

    /**
     * Constructor.
     */
    public WrappedAsyncResponse(AsyncResponse asyncResponse, InboundCallType inboundCallType) {
        Preconditions.checkNotNull(asyncResponse);
        Preconditions.checkNotNull(inboundCallType);

        this.asyncResponse = asyncResponse;
        this.inboundCallType = inboundCallType.toString();
        timer = METRICS_ENABLED ?
            Metrics.getInstance().getMetricRegistry().timer(MetricRegistry.name("InboundCall", this.inboundCallType)).time()
            :
            null;
    }

    public String getInboundCallType() {
        return inboundCallType;
    }

    @Override
    public boolean resume(Object response) {
        onFinished();
        return asyncResponse.resume(response);
    }

    @Override
    public boolean resume(Throwable response) {
        onFinished();
        return asyncResponse.resume(response);
    }

    @Override
    public boolean cancel() {
        onFinished();
        return asyncResponse.cancel();
    }

    @Override
    public boolean cancel(int retryAfter) {
        onFinished();
        return asyncResponse.cancel(retryAfter);
    }

    @Override
    public boolean cancel(Date retryAfter) {
        onFinished();
        return asyncResponse.cancel(retryAfter);
    }

    @Override
    public boolean isSuspended() {
        return asyncResponse.isSuspended();
    }

    @Override
    public boolean isCancelled() {
        return asyncResponse.isCancelled();
    }

    @Override
    public boolean isDone() {
        return asyncResponse.isDone();
    }

    @Override
    public boolean setTimeout(long time, TimeUnit unit) {
        return asyncResponse.setTimeout(time, unit);
    }

    @Override
    public void setTimeoutHandler(TimeoutHandler handler) {
        asyncResponse.setTimeoutHandler(handler);
    }

    @Override
    public Collection<Class<?>> register(Class<?> callback) {
        return asyncResponse.register(callback);
    }

    @Override
    public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback, Class<?>... callbacks) {
        return asyncResponse.register(callback, callbacks);
    }

    @Override
    public Collection<Class<?>> register(Object callback) {
        return asyncResponse.register(callback);
    }

    @Override
    public Map<Class<?>, Collection<Class<?>>> register(Object callback, Object... callbacks) {
        return asyncResponse.register(callback, callbacks);
    }

    private void onFinished() {
        if (!finished) {
            if (timer != null) {
                timer.stop();
            }
            finished = true;
        }
    }
}
