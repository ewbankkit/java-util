//
// Copyright (C) Capital One Labs.
//
package com.capitalone.cardcompanion.common.data.hazelcast.commands

import com.capitalone.cardcompanion.common.data.hazelcast.IMapMock
import com.google.common.base.Optional
import com.hazelcast.core.IMap
import org.junit.Test

class AbstractHazelcastFindByValueCommandUnitTest {


    @Test
    void testFindByValueSuccess() {
        FindByValueCommandTestImpl cmd = new FindByValueCommandTestImpl("aField","aValue",false)
        Optional<AnObject> obj = cmd.execute()
        assert obj.isPresent()
        assert obj.get().aField == "aValue"
        assert !cmd.fallbackCalled
    }

    @Test
    void testFindByValueFallback() {
        FindByValueCommandTestImpl cmd = new FindByValueCommandTestImpl("aField","aValue",true)
        Optional<AnObject> obj = cmd.execute()
        assert !obj.isPresent()
        assert cmd.fallbackCalled
    }


    private static class FindByValueCommandTestImpl extends AbstractHazelcastFindByValueCommand<String, AnObject, String> {

        private boolean fallbackCalled = false
        private final boolean useFallback

        def FindByValueCommandTestImpl(String field, String value, boolean useFallback) {
            super(createMap(), field, value)
            this.useFallback = useFallback
        }

        private static IMap<String, AnObject> createMap() {
            IMap<String, AnObject> map = new IMapMock<String,AnObject>()
            map.put("aKey",new AnObject(aField: "aValue"))
            map.put("anotherKey",new AnObject(aField: "anotherValue"))
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
        protected Optional<AnObject> getFallback() {
            fallbackCalled = true
            return Optional.absent()
        }
    }

    public static class AnObject {
        String aField
    }
}
