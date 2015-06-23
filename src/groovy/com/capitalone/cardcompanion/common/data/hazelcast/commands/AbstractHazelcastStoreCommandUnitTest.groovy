//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.hazelcast.IMapMock
import com.hazelcast.core.IMap
import org.junit.Test

import java.lang.reflect.Field

class AbstractHazelcastStoreCommandUnitTest {

    @Test
    void testStoreKeySuccess() {
        StoreCommandTestImpl cmd = new StoreCommandTestImpl("aKey","aValue",false)
        cmd.execute()
        Field mapField = AbstractHazelcastStoreCommand.getDeclaredField("map")
        mapField.setAccessible(true)
        IMap<String,String> map = mapField.get(cmd)
        assert map.get("aKey") == "aValue"
        assert !cmd.useFallback
    }

    @Test
    void testStoreKeyFallback() {
        StoreCommandTestImpl cmd = new StoreCommandTestImpl("aKey","aValue",true)
        cmd.execute()

        assert cmd.fallbackCalled
    }


    private static class StoreCommandTestImpl extends AbstractHazelcastStoreCommand<String,String> {

        private boolean fallbackCalled = false
        private final boolean useFallback

        def StoreCommandTestImpl(String key, String value, boolean useFallback) {
            super(createMap(), key, value)
            this.useFallback = useFallback
        }

        private static IMap<String,String> createMap() {
            IMap<String,String> map = new IMapMock<String,String>()
            map.put("aKey","aValue")
            return map
        }

        @Override
        protected def doRun() throws Exception {
            if ( useFallback) {
                throw new RuntimeException("Exception to force fallback for testing")
            }
            super.doRun()
        }

        @Override
        protected Void getFallback() {
            fallbackCalled = true
            return null
        }
    }
}
