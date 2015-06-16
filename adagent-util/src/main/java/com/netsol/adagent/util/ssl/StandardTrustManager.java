/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Standard trust manager.
 */
public class StandardTrustManager extends BaseTrustManager {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:29 StandardTrustManager.java NSI";

    /**
     * Constructor.
     */
    public StandardTrustManager(X509TrustManager x509TrustManager) {
        super(x509TrustManager);
    }

    /**
     * Build a certificate path to a trusted root and return if it can be
     * validated and is trusted for server SSL authentication based on the
     * authentication type.
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        logCertificateChain(chain);
        x509TrustManager.checkServerTrusted(chain, authType);
    }
}
