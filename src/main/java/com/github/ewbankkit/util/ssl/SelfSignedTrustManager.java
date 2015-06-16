/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * A trust manager that accepts self-signed certificates.
 */
public class SelfSignedTrustManager extends BaseTrustManager {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:29 SelfSignedTrustManager.java NSI";

    private final boolean checkSelfSignedCertificate;

    /**
     * Constructor.
     */
    public SelfSignedTrustManager(X509TrustManager x509TrustManager, boolean checkSelfSignedCertificate) {
        super(x509TrustManager);
        this.checkSelfSignedCertificate = checkSelfSignedCertificate;
    }

    /**
     * Build a certificate path to a trusted root and return if it can be
     * validated and is trusted for server SSL authentication based on the
     * authentication type.
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        logCertificateChain(chain);

        if ((chain != null) && (chain.length == 1)) {
            // Self-signed certificate.
            if (this.checkSelfSignedCertificate) {
                chain[0].checkValidity();
            }
        }
        else {
            x509TrustManager.checkServerTrusted(chain, authType);
        }
    }
}
