//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommand.Setter as HCSetter
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandProperties
import com.netflix.hystrix.HystrixCommandProperties.Setter as HCPSetter
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j

/**
 * A Hystrix command to execute against a Hazelcast cache. This sets up common configuration parameters.
 */
@Slf4j
@PackageScope
abstract class AbstractHazelcastHystrixCommand<V> extends HystrixCommand<V> {
    /**
     * Constructor.
     */
    protected AbstractHazelcastHystrixCommand() {
        super(createProperties())
    }

    private static HCSetter createProperties() {
        // Groovy issue with class loading that Setter can't be used directly:
        // http://neidetcher.com/programming/2013/10/17/getting-around-groovy-linkage-error.html
        return HCSetter.withGroupKey(HystrixCommandGroupKey.Factory.asKey('HazelcastCommand')).andCommandPropertiesDefaults(
            (HystrixCommandProperties.invokeMethod('Setter', null) as HCPSetter).
                withCircuitBreakerEnabled(true).
                withCircuitBreakerSleepWindowInMilliseconds(60000).
                withCircuitBreakerErrorThresholdPercentage(75)
        )
    }

    @Override
    protected final V run() throws Exception {
        log.trace 'Running {}', getClass().simpleName
        def result = doRun()
        result as V
    }

    // This returns a def because Hazelcast return 2.6.7 either the object or an exception object.
    protected abstract def doRun() throws Exception
}
