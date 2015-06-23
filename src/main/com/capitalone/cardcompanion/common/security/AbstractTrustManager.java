//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import com.capitalone.cardcompanion.common.Config;
import com.google.common.base.Preconditions;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Base trust manager.
 */
abstract class AbstractTrustManager implements X509TrustManager {
    protected static final boolean LOG_CERTIFICATE_CHAIN = Config.getInstance().getBoolean("trustManager.logCertificateChain", false);

    protected final X509TrustManager x509TrustManager;

    /**
     * Constructor.
     */
    protected AbstractTrustManager(X509TrustManager x509TrustManager) {
        Preconditions.checkNotNull(x509TrustManager);
        this.x509TrustManager = x509TrustManager;
    }

    /**
     * Build a certificate path to a trusted root and return if it can be
     * validated and is trusted for client SSL authentication based on the
     * authentication type.
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        x509TrustManager.checkClientTrusted(chain, authType);
    }

    /**
     * Build a certificate path to a trusted root and return if it can be
     * validated and is trusted for server SSL authentication based on the
     * authentication type.
     */
    @Override
    public abstract void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException;

    /**
     * Return an array of certificate authority certificates which are
     * trusted for authenticating peers.
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return x509TrustManager.getAcceptedIssuers();
    }
}
