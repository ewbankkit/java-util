//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test AES encryption.
 */
public final class AESCBCUnitTest {
    @Before
    public void before() throws Exception {
        System.setProperty("common.env", "test");
    }

    @Test
    public void testEncryptDecrypt1() throws Exception {
        String string = "A test string";
        String encrypted = AESCBC.encrypt(string);
        assertNotEquals(string, encrypted);
        assertEquals(string, AESCBC.decrypt(encrypted));
    }
}
