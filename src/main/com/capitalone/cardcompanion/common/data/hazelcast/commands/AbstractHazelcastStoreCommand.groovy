//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.hazelcast.core.IMap
import groovy.transform.PackageScope

/**
 * Abstract Hystrix command for storing something in a Hazelcast cache. The subclass should provide the fallback
 * implementation.
 */
@PackageScope
abstract class AbstractHazelcastStoreCommand<K, V> extends AbstractHazelcastHystrixCommand<Void> {
    private final K          key
    private final IMap<K, V> map
    private final V          value

    /**
     * Constructor.
     */
    AbstractHazelcastStoreCommand(IMap<K,V> map, K key, V value) {
        this.map = map
        this.key = key
        this.value = value
    }

    @Override
    protected def doRun() throws Exception {
        map.put(key, value)
    }

    protected K getKey() {
        key
    }

    protected V getValue() {
        value
    }
}
