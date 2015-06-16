/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.httpclient;

import static com.github.ewbankkit.util.beans.BaseData.coalesce;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.X509TrustManager;

import com.github.ewbankkit.util.config.Config;
import com.github.ewbankkit.util.ssl.TrustManagers;
import org.apache.commons.httpclient.HttpClientError;

import com.github.ewbankkit.util.IOUtil;
import com.github.ewbankkit.util.ssl.BaseTrustManager;

/**
 * SSL trust store.
 */
/* package-private */ class SslTrustStore {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:00 SslTrustStore.java NSI";

    private final X509TrustManager x509TrustManager;

    /**
     * Constructor.
     */
    public SslTrustStore(Config config, String suffix) {
        KeyStore keystore = null;

        try {
            String trustStoreFileName = config.get(HttpClientFactory.getConfigKeyName("adagent.httpClient.sslTrustStore.fileName", suffix));
            if (trustStoreFileName != null) {
                char[] trustStorePassword =
                    config.get(HttpClientFactory.getConfigKeyName("adagent.httpClient.sslTrustStore.password", suffix)).toCharArray();
                String keyStoreType = coalesce(config.get(HttpClientFactory.getConfigKeyName("adagent.httpClient.sslTrustStore.type", suffix)), KeyStore.getDefaultType());
                // Load the key store.
                keystore = KeyStore.getInstance(keyStoreType);
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(trustStoreFileName);
                    keystore.load(inputStream, trustStorePassword);

                    // Display the trust store's content.
                    if (BaseTrustManager.logger.isDebugEnabled()) {
                        StringBuilder message = new StringBuilder("Trust store (").append(trustStoreFileName).append("):");
                        for (String alias : Collections.list(keystore.aliases())) {
                            if (keystore.isCertificateEntry(alias)) {
                                Certificate certificate = keystore.getCertificate(alias);
                                if (certificate instanceof X509Certificate) {
                                    message.append("\n[").append(alias).append(']').append(BaseTrustManager.loggableCertificate((X509Certificate)certificate));
                                }
                            }
                        }
                        BaseTrustManager.logger.debug(message.toString());
                    }
                }
                finally {
                    IOUtil.close(inputStream);
                }
            }

            // Get the X509 trust manager.
            this.x509TrustManager = TrustManagers.getX509TrustManager(keystore);
        }
        catch (Exception ex) {
            throw new HttpClientError(ex.getMessage());
        }
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }
}
