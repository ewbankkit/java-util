//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test SSL contexts functionality.
 */
public final class SslContextsUnitTest {
    @Before
    public void before() throws Exception {
        System.setProperty("common.env", "test");
    }

    @Test
    public void testSslContexts1() throws Exception {
        SSLContext sslContext = SslContexts.acceptAll(Optional.<KeyStoreProperties>absent(), Optional.<KeyStoreProperties>absent());
        assertNotNull(sslContext);
    }

    @Test
    public void testSslContexts2() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("capitalOneCA.keystore");
        assertTrue(optionalProperties.isPresent());
        SSLContext sslContext = SslContexts.standard(Optional.<KeyStoreProperties>absent(), optionalProperties);
        assertNotNull(sslContext);
    }

    @Test
    public void testSslContexts3() throws Exception {
        Optional<KeyStoreProperties> optionalProperties = KeyStoreProperties.fromConfig("capitalOneCA.keystore");
        assertTrue(optionalProperties.isPresent());
        SSLContext sslContext = SslContexts.selfSigned(optionalProperties, optionalProperties);
        assertNotNull(sslContext);
    }
}
