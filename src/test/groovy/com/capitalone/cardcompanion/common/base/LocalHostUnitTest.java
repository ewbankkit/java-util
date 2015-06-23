//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.base;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test localhost utilities.
 */
public final class LocalHostUnitTest {
    @Test
    public void testName() {
        String name = LocalHost.NAME;
        assertNotNull(name);
    }

    @Test
    public void testAddress() {
        String address = LocalHost.ADDRESS;
        assertNotNull(address);
    }
}
