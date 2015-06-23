//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common

import com.google.common.base.Optional
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class GroovyBootstrapUnitTest {
    GroovyBootstrap groovyBootstrap

    @Before
    void init() {
        groovyBootstrap = new GroovyBootstrap()
        groovyBootstrap.init()
    }

    @After
    void term() {
        if (groovyBootstrap) {
            groovyBootstrap.destroy()
        }
    }

    @Test
    void testOptionalAsBoolean1() {
        Optional<String> optional = null;

        assertFalse !!optional
    }

    @Test
    void testOptionalAsBoolean2() {
        Optional<String> optional = Optional.absent();

        assertFalse !!optional
    }

    @Test
    void testOptionalAsBoolean3() {
        Optional<String> optional = Optional.of("PRESENT");

        assertTrue !!optional
    }
}
