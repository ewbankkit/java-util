//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.eventing

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.SubscriberExceptionContext
import com.google.common.eventbus.SubscriberExceptionHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Executor

/**
 * Wraps an event bus to add additional functionality to unregister events on shutdown
 */
class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus)

    private final List<Object> eventSubscribers = new ArrayList<Object>()

    @Delegate
    com.google.common.eventbus.EventBus eventBus

    EventBus(Executor threadPoolExecutor) {
        this.eventBus = createEventBus(threadPoolExecutor)
    }

    public void shutdown() {
        eventSubscribers.each { eventBus.unregister(it) }
        eventSubscribers.clear()
    }

    /**
     * Creates the event bus.
     * @param threadPoolExecutor
     */
    private com.google.common.eventbus.EventBus createEventBus(Executor threadPoolExecutor) {
        eventBus = new AsyncEventBus(threadPoolExecutor, new SubscriberExceptionHandler() {
            /**
             * Handles exceptions thrown by subscribers.
             */
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context) {
                LOGGER.warn(String.format("Unable to dispatch event to %s", context.subscriberMethod), exception)
            }
        })

        ServiceLoader<EventSubscriber> subscribers = ServiceLoader.load(EventSubscriber)
        subscribers.each { eventSubscriber ->
            LOGGER.debug("Registering subscriber {}", eventSubscriber.getClass());
            eventBus.register(eventSubscriber)
            eventSubscribers.add(eventSubscriber)
        }
        return eventBus
    }
}
