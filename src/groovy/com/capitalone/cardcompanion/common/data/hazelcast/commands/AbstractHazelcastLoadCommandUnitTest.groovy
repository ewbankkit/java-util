//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.hazelcast.IMapMock
import com.google.common.base.Optional
import com.hazelcast.core.IMap
import org.junit.Test

class AbstractHazelcastLoadCommandUnitTest {


    @Test
    void testLoadKeySuccess() {
        LoadCommandTestImpl cmd = new LoadCommandTestImpl("aKey",false)
        Optional<String> str = cmd.execute()
        assert str.isPresent()
        assert str.get() == "aValue"
        assert !cmd.fallbackCalled
    }

    @Test
    void testLoadKeyJavaExceptionFallback() {
        LoadCommandTestImpl cmd = new LoadCommandTestImpl("aKey",true)
        Optional<String> str = cmd.execute()
        assert !str.isPresent()
        assert cmd.fallbackCalled
    }

    private static class LoadCommandTestImpl extends AbstractHazelcastLoadCommand<String,String> {

        private boolean fallbackCalled = false
        private final boolean throwRuntimeException

        def LoadCommandTestImpl(String key, boolean throwRuntimeException) {
            super(createMap(), key)
            this.throwRuntimeException = throwRuntimeException
        }

        private static IMap<String,String> createMap() {
            IMap<String,String> map = new IMapMock<String,String>()
            map.put("aKey","aValue")
            return map
        }

        @Override
        protected def doRun() throws Exception {
            if ( throwRuntimeException) {
                throw new RuntimeException("Exception to force fallback for testing")
            }
            super.doRun()
        }

        @Override
        protected Optional<String> getFallback() {
            fallbackCalled = true
            return Optional.absent()
        }
    }
}
