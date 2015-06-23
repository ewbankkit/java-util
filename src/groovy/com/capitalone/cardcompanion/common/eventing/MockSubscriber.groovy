//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.eventing

import com.google.common.eventbus.Subscribe

class MockSubscriber implements EventSubscriber {

    volatile Object lastEvent

    @Subscribe
    void onEventReceived(Object event) {
        lastEvent = event
    }
}
