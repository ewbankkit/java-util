//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Represents an X509 certificate and associated private key.
 */
public final class X509CertificateAndPrivateKey {
    @SuppressWarnings("NullableProblems")
    @Nullable
    private final PrivateKey      privateKey;
    private final X509Certificate x509Certificate;

    public X509CertificateAndPrivateKey(X509Certificate x509Certificate, @Nullable PrivateKey privateKey) {
        Preconditions.checkNotNull(x509Certificate);

        this.x509Certificate = x509Certificate;
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }
}
