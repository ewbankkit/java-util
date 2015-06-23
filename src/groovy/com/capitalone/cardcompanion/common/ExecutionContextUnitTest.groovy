//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

final class ExecutionContextUnitTest {
    @Test
    void testInitialize1() {
        ExecutionContext executionContext = ExecutionContext.initializeContext()

        assertEquals executionContext, ExecutionContext.contextForCurrentThread
    }

    @Test
    void testInitialize2() {
        ExecutionContext executionContext = ExecutionContext.initializeContext null, null, UUID.randomUUID(), null,null,null

        assertEquals executionContext, ExecutionContext.contextForCurrentThread
    }

    @Test
    void testInitialize3() {
        ExecutionContext executionContext = ExecutionContext.initializeContext null, 'ANDROID', null, UUID.randomUUID(),null,null

        assertEquals executionContext, ExecutionContext.contextForCurrentThread
    }

    @Test
    void testInitialize4() {
        ExecutionContext executionContext = ExecutionContext.initializeContext null, null, UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(),null

        assertEquals executionContext, ExecutionContext.contextForCurrentThread
    }

    @Test
    void testReinitialize1() {
        ExecutionContext executionContext1 = ExecutionContext.initializeContext()
        ExecutionContext executionContext2 = ExecutionContext.reinitializeContext null, null,null,null

        assertEquals executionContext1, executionContext2
        assertEquals executionContext2, ExecutionContext.contextForCurrentThread
    }

    @Test
    void testReinitialize2() {
        ExecutionContext executionContext1 = ExecutionContext.initializeContext AppVersion.fromVersionString('1.2'), 'IOS', UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(),'SSOID'
        ExecutionContext executionContext2 = ExecutionContext.reinitializeContext UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(),null

        assertNotEquals executionContext1, executionContext2
    }

    @Test
    void testReinitialize3() {
        ExecutionContext executionContext1 = ExecutionContext.initializeContext null, null, UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(),null
        ExecutionContext executionContext2 = ExecutionContext.reinitializeContext null, null,null,null

        assertEquals executionContext1, executionContext2
    }

    @Test
    void testReinitialize4() {
        ExecutionContext executionContext1 = ExecutionContext.initializeContext()
        ExecutionContext executionContext2 = ExecutionContext.reinitializeContext UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(),null

        assertNotEquals executionContext1, executionContext2
    }

    @Test
    void testReinitialize5() {
        ExecutionContext executionContext1 = ExecutionContext.initializeContext()
        ExecutionContext executionContext2 = ExecutionContext.reinitializeContext UUID.randomUUID(), null,null,null

        assertNotEquals executionContext1, executionContext2
    }

    @Test
    void testReinitialize6() {
        ExecutionContext executionContext1 = ExecutionContext.initializeContext()
        ExecutionContext executionContext2 = ExecutionContext.reinitializeContext null, UUID.randomUUID(),UUID.randomUUID(),null

        assertNotEquals executionContext1, executionContext2
    }

    @Test
    void testReinitialize7() {
        ExecutionContext executionContext1 = ExecutionContext.initializeContext  null, null, UUID.randomUUID(), null,null,null
        ExecutionContext executionContext2 = ExecutionContext.reinitializeContext null, UUID.randomUUID(),UUID.randomUUID(),null

        assertNotEquals executionContext1, executionContext2
    }
}
