//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.hazelcast.core.IMap
import groovy.transform.PackageScope

/**
 * Abstract Hystrix command for deleting something from a Hazelcast cache. The subclass should provide the fallback
 * implementation.
 */
@PackageScope
abstract class AbstractHazelcastDeleteCommand<K, V> extends AbstractHazelcastHystrixCommand<Integer> {
    private final K          key
    private final IMap<K, V> map

    AbstractHazelcastDeleteCommand(IMap<K, V> map, K key) {
        this.map = map
        this.key = key
    }

    @Override
    protected def doRun() throws Exception {
        V val = map.remove(key)
        val ? 1 : 0
    }

    protected K getKey() {
        key
    }
}
