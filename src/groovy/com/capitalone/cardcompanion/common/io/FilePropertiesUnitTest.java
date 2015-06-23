//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.io;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test file properties.
 */
public final class FilePropertiesUnitTest {
    @Before
    public void before() {
        System.setProperty("config.name", "common");
        System.setProperty("common.env", "test");
    }

    @Test
    public void testFromConfig1() throws Exception {
        Optional<FileProperties> fp = FileProperties.fromConfig("goo");
        assertNotNull(fp);
        assertFalse(fp.isPresent());
    }

    @Test
    public void testFromConfig2() throws Exception {
        Optional<FileProperties> fp = FileProperties.fromConfig("foo");
        assertNotNull(fp);
        assertTrue(fp.isPresent());
        assertTrue(fp.get().getFileOrResource().isRight());
    }

    @Test
    public void testFromConfig3() throws Exception {
        Optional<FileProperties> fp = FileProperties.fromConfig("bar");
        assertNotNull(fp);
        assertTrue(fp.isPresent());
        assertTrue(fp.get().getFileOrResource().isLeft());
    }
}
