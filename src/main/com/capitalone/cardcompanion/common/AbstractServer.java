//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common;

import com.capitalone.cardcompanion.common.eventing.EventBus;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class to startup and shutdown shared services and register health checks.
 */
public abstract class AbstractServer implements Server {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private EventBus           eventBus;
    private GroovyBootstrap    groovyBootstrap;
    private rx.Scheduler       rxScheduler;
    private Boolean            successfullyStarted;
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * Constructor.
     */
    protected AbstractServer() {}

    /**
     * Start the server.
     */
    @Override
    public void start() {
        Preconditions.checkState(successfullyStarted == null);

        LOGGER.info("Server starting...");
        final MutableObject<HealthCheck.Result> resultReference = new MutableObject<>();
        try {
            groovyInit();
            createThreadPoolExecutor();
            createRxScheduler();
            createEventBus();
            doStart();
            successfullyStarted = Boolean.TRUE;
            resultReference.setValue(HealthCheck.Result.healthy());
        }
        catch (InitializationException ex) {
            LOGGER.error("Server initialization error", ex);

            successfullyStarted = Boolean.FALSE;
            resultReference.setValue(HealthCheck.Result.unhealthy(ex.getMessage()));
        }

        // Register the health check.
        HealthChecks.getInstance().getHealthCheckRegistry().register(HEALTH_CHECK_NAME, new HealthCheck() {
            /**
             * Perform a check of the application component.
             */
            @Override
            protected Result check() throws Exception {
                return resultReference.getValue();
            }
        });

        LOGGER.info("Server started");
    }

    /**
     * Stop the server.
     */
    @Override
    public void stop() {
        Preconditions.checkState(successfullyStarted != null);

        LOGGER.info("Server stopping...");
        doStop();
        destroyEventBus();
        destroyThreadPoolExecutor();
        groovyDestroy();
        LOGGER.info("Server stopped");
    }

    public EventBus getEventBus() {
        checkSuccessfullyStarted();

        return eventBus;
    }

    public rx.Scheduler getRxScheduler() {
        checkSuccessfullyStarted();

        return rxScheduler;
    }

    /**
     * Checks that the server was successfully started.
     */
    protected void checkSuccessfullyStarted() {
        Preconditions.checkState(Boolean.TRUE.equals(successfullyStarted));
    }


    protected abstract void doStart() throws InitializationException;

    protected abstract void doStop();

    protected ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    /**
     * Groovy initialization.
     */
    private void groovyInit() throws InitializationException {
        groovyBootstrap = new GroovyBootstrap();
        groovyBootstrap.getInit().call();
    }

    /**
     * Groovy destruction.
     */
    private void groovyDestroy() {
        if (groovyBootstrap != null) {
            groovyBootstrap.getDestroy().call();
        }
    }

    /**
     * Creates the event bus.
     */
    private void createEventBus() {
        eventBus = new EventBus(threadPoolExecutor);
    }

    /**
     * Creates the Rx scheduler.
     */
    private void createRxScheduler() throws InitializationException {
        rxScheduler = Schedulers.from(threadPoolExecutor);
    }

    /**
     * Creates the thread pool executor.
     */
    private void createThreadPoolExecutor() throws InitializationException {
        Config config = Config.getInstance();

        LOGGER.debug("creating thread pool with {} threads", config.getInteger("rxScheduler.threadPool.maxSize", Integer.MAX_VALUE));

        // By default the executor works like a cached thread pool.
        final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(config.getInteger("rxScheduler.threadPool.queue.maxSize", 1));
        threadPoolExecutor = ThreadPools.newThreadPoolExecutor(
            Config.getName(),
            config.getInteger("rxScheduler.threadPool.coreSize", 0),
            config.getInteger("rxScheduler.threadPool.maxSize", Integer.MAX_VALUE),
            config.getLong("rxScheduler.threadPool.keepAliveSecs", 60L),
            TimeUnit.SECONDS,
            workQueue
        );

        // Add metrics.
        MetricRegistry metricsRegistry = Metrics.getInstance().getMetricRegistry();
        metricsRegistry.register(MetricRegistry.name(getClass(), "userCache", "activeThreadCount"), new Gauge<Integer>() {
            @Override
            @SuppressWarnings("UnnecessaryBoxing")
            public Integer getValue() {
                return Integer.valueOf(threadPoolExecutor.getActiveCount());
            }
        });
        metricsRegistry.register(MetricRegistry.name(getClass(), "threadPoolExecutor", "completedTaskCount"), new Gauge<Long>() {
            @Override
            @SuppressWarnings("UnnecessaryBoxing")
            public Long getValue() {
                return Long.valueOf(threadPoolExecutor.getCompletedTaskCount());
            }
        });
        metricsRegistry.register(MetricRegistry.name(getClass(), "threadPoolExecutor", "currentPoolSize"), new Gauge<Integer>() {
            @Override
            @SuppressWarnings("UnnecessaryBoxing")
            public Integer getValue() {
                return Integer.valueOf(threadPoolExecutor.getPoolSize());
            }
        });
        metricsRegistry.register(MetricRegistry.name(getClass(), "threadPoolExecutor", "largestPoolSize"), new Gauge<Integer>() {
            @Override
            @SuppressWarnings("UnnecessaryBoxing")
            public Integer getValue() {
                return Integer.valueOf(threadPoolExecutor.getLargestPoolSize());
            }
        });
        metricsRegistry.register(MetricRegistry.name(getClass(), "threadPoolExecutor", "queueRemainingCapacity"), new Gauge<Integer>() {
            @Override
            @SuppressWarnings("UnnecessaryBoxing")
            public Integer getValue() {
                return Integer.valueOf(workQueue.remainingCapacity());
            }
        });
        metricsRegistry.register(MetricRegistry.name(getClass(), "threadPoolExecutor", "queueSize"), new Gauge<Integer>() {
            @Override
            @SuppressWarnings("UnnecessaryBoxing")
            public Integer getValue() {
                return Integer.valueOf(workQueue.size());
            }
        });
        metricsRegistry.register(MetricRegistry.name(getClass(), "threadPoolExecutor", "taskCount"), new Gauge<Long>() {
            @Override
            @SuppressWarnings("UnnecessaryBoxing")
            public Long getValue() {
                return Long.valueOf(threadPoolExecutor.getTaskCount());
            }
        });
    }

    /**
     * Destroys the event bus.
     */
    private void destroyEventBus() {
        eventBus.shutdown();
    }

    /**
     * Destroys the thread pool executor.
     */
    private void destroyThreadPoolExecutor() {
        if (threadPoolExecutor != null) {
            List<Runnable> awaitingTasks = threadPoolExecutor.shutdownNow();
            if (!awaitingTasks.isEmpty()) {
                LOGGER.info("{} tasks were awaiting execution", awaitingTasks.size());
            }
        }
    }
}
