//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Hostname verifier utilities.
 */
public final class HostnameVerifiers {
    private static final HostnameVerifier ACCEPT_ALL = new HostnameVerifier() {
        /**
         * Verify that the host name is an acceptable match with the server's authentication scheme.
         */
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    /**
     * Returns a hostname verifier that accepts all hosts.
     */
    @SuppressWarnings("unused")
    public static HostnameVerifier acceptAll() {
        return ACCEPT_ALL;
    }
}
