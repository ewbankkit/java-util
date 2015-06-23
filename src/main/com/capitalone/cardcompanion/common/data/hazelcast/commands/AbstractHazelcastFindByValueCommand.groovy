//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.google.common.base.Optional
import com.google.common.collect.FluentIterable
import com.hazelcast.core.IMap
import com.hazelcast.query.PredicateBuilder
import groovy.transform.PackageScope

/**
 * Abstract Hystrix command for loading something from a Hazelcast cache using a secondary field. The subclass should
 * provide the fallback implementation.
 */
@PackageScope
abstract class AbstractHazelcastFindByValueCommand<K, V, T /*extends Comparable<T>*/> extends AbstractHazelcastHystrixCommand<Optional<V>> {
    private final String     field
    private final IMap<K, V> map
    private final T          value

    /**
     * Constructor.
     */
    AbstractHazelcastFindByValueCommand(IMap<K, V> map, String field, T value) {
        this.map = map
        this.field = field
        this.value = value
    }

    @Override
    protected def doRun() throws Exception {
        // For our cases, there is only one matching value.
        FluentIterable.from(map.values(new PredicateBuilder().entryObject.get(field).equal(value))).first()
    }

    protected T getValue() {
        value
    }
}
