//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * A trust manager that accepts self-signed certificates.
 */
final class SelfSignedTrustManager extends AbstractTrustManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SelfSignedTrustManager.class);

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
        if (LOGGER.isDebugEnabled() && LOG_CERTIFICATE_CHAIN) {
            LOGGER.debug(X509Certificates.toString(chain));
        }

        if ((chain != null) && (chain.length == 1)) {
            // Self-signed certificate.
            if (checkSelfSignedCertificate) {
                chain[0].checkValidity();
            }
        }
        else {
            x509TrustManager.checkServerTrusted(chain, authType);
        }
    }
}
