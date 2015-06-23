//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import groovy.mock.interceptor.MockFor
import org.junit.Test

import java.lang.reflect.Modifier
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.*

class LazyMapTest {

    @Test
    void testAllMethodsDelegatedToHazelcastMap() {

        def methods = IMap.declaredMethods.findAll { method -> !method.isSynthetic()  && (Modifier.isPublic(method.modifiers) || Modifier.isProtected(method.modifiers))  }
        methods.each { method ->
            def args = []
            method.parameterTypes.each  { type ->
                args << getArgument(type)
            }
            IMap map = mock(IMap)

            HazelcastInstance instance = [ getMap : { String mapName -> map} ] as HazelcastInstance
            LazyMap lazyMap = new LazyMap(instance,"testMap")

            // verify that the call to the lazy map delegates to the mock
            method.invoke(lazyMap,args as Object[])
            method.invoke(verify(map),args as Object[])
        }

    }

    /**
     * Gets a valid argument for a parameter of the specified type
     * @param type
     */
    private static def getArgument(Class<?> type) {
        switch(type) {
            case long.class:
                return 0L
            case int.class:
                return 0
            case String.class:
                return ""
            case boolean.class:
                return true
            case TimeUnit:
                return TimeUnit.SECONDS
            case Set.class:
                return new HashSet<>()
            default:
                return new MockFor(type).proxyInstance()
        }
    }
}
