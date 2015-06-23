//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.google.common.base.Optional
import com.hazelcast.core.IMap
import groovy.transform.PackageScope

/**
 * Abstract Hystrix command for loading something from a Hazelcast cache. The subclass should provide the fallback
 * implementation.
 */
@PackageScope
abstract class AbstractHazelcastLoadCommand<K, V> extends AbstractHazelcastHystrixCommand<Optional<V>> {
    private final K          key
    private final IMap<K, V> map

    /**
     * Constructor.
     */
    AbstractHazelcastLoadCommand(IMap<K, V> map, K key) {
        this.map = map
        this.key = key
    }

    @Override
    protected def doRun() throws Exception {
        Optional.fromNullable(map.get(key))
    }

    protected K getKey() {
        key
    }
}
