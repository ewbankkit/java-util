//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.eventing

import junitx.util.PrivateAccessor
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EventBusUnitTest {

    private ExecutorService executor

    private EventBus eventBus

    private MockSubscriber subscriber

    @Before
    void before() {
        executor =  Executors.newSingleThreadExecutor()
        eventBus = new EventBus(executor)
        List<Object> eventSubscribers = PrivateAccessor.getField(eventBus,"eventSubscribers")
        subscriber = eventSubscribers[0]
    }

    @After
    void after() {
        executor.shutdownNow()
    }

    @Test
    void testEventPublished() {
        Object msg = new Object()
        eventBus.post(msg)
        assertMessageReceived(msg,10000)
    }

    @Test
    void testSubscribersRemovedOnShutdown() {
        eventBus.shutdown()

        // these fields are not publicly exposed
        List<Object> eventSubscribers = PrivateAccessor.getField(eventBus,"eventSubscribers")
        assert eventSubscribers.isEmpty()

        def internalSubscribers = PrivateAccessor.getField(eventBus.eventBus,"subscribersByType")
        assert internalSubscribers.isEmpty()
    }

    private void assertMessageReceived(Object message,long maxMillis) {
        long waitTime = 0;
        while ( subscriber.lastEvent != message ) {
            if ( waitTime > maxMillis ) {
                throw new AssertionError("Expected ${message} to be received")
            }
            long start = System.currentTimeMillis()
            Thread.sleep(100);
            long end = System.currentTimeMillis()
            waitTime += (end-start)
        }
    }


}
