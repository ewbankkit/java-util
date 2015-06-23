//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Standard trust manager.
 */
final class StandardTrustManager extends AbstractTrustManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardTrustManager.class);

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
        if (LOGGER.isDebugEnabled() && LOG_CERTIFICATE_CHAIN) {
            LOGGER.debug(X509Certificates.toString(chain));
        }

        x509TrustManager.checkServerTrusted(chain, authType);
    }
}
