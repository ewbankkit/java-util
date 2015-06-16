/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.ssl;

import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Trust manager utilities.
 */
public final class TrustManagers {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:30 TrustManagers.java NSI";

    public static X509TrustManager getX509TrustManager(KeyStore keystore) throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager)trustManager;
            }
        }
        return null;
    }
}
