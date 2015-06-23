//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyStore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test key store functionality.
 */
public final class KeyStoresUnitTest {
    @Before
    public void before() throws Exception {
        System.setProperty("common.env", "test");
    }

    @Test
    public void testKeyStoreProperties1() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("NoNoNo");
        assertFalse(optionalProperties.isPresent());
    }

    @Test
    public void testKeyStoreProperties2() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("capitalOneCA.keystore");
        assertTrue(optionalProperties.isPresent());
        String s = optionalProperties.get().toString();
        assertNotNull(s);
    }

    @Test
    public void testKeyStoreProperties3() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("capitalOneCA.keystore");
        assertTrue(optionalProperties.isPresent());
        KeyStore keystore = KeyStores.fromProperties(optionalProperties.get());
        assertNotNull(keystore);
    }

    @Test
    @Ignore("Ignored until implementing passbook and we know where to put the properties files")
    public void testKeyStoreProperties4() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("passbook.keystore");
        assertTrue(optionalProperties.isPresent());
        KeyStoreProperties properties = optionalProperties.get();
        KeyStore keystore = KeyStores.fromProperties(properties);
        assertNotNull(keystore);

        Optional<KeyPair> optionalKeyPair = KeyStores.getKeyPair(keystore, properties.getOptionalKeyPassword());
        assertNotNull(optionalKeyPair);
        assertTrue(optionalKeyPair.isPresent());
    }

    @Test
    public void testKeyStoreProperties5() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("rtmClientQA.keystore");
        assertTrue(optionalProperties.isPresent());
        KeyStore keystore = KeyStores.fromProperties(optionalProperties.get());
        assertNotNull(keystore);
    }

    @Test
    public void testKeyStoreProperties6() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("rtmClientPROD.keystore");
        assertTrue(optionalProperties.isPresent());
        KeyStore keystore = KeyStores.fromProperties(optionalProperties.get());
        assertNotNull(keystore);
    }
}
