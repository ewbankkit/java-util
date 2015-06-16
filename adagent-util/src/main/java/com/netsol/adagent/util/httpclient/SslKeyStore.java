/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.httpclient;

import static com.netsol.adagent.util.beans.BaseData.coalesce;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.X509KeyManager;

import org.apache.commons.httpclient.HttpClientError;

import com.netsol.adagent.util.IOUtil;
import com.netsol.adagent.util.config.Config;
import com.netsol.adagent.util.ssl.KeyManagers;

/**
 * SSL key store.
 */
/* package-private */ class SslKeyStore {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:00 SslKeyStore.java NSI";

    private final X509KeyManager x509KeyManager;

    /**
     * Constructor.
     */
    public SslKeyStore(Config config, String suffix) {
        KeyStore keystore = null;

        try {
            String keyStoreFileName = config.get(HttpClientFactory.getConfigKeyName("adagent.httpClient.sslKeyStore.fileName", suffix));
            String password = config.get(HttpClientFactory.getConfigKeyName("adagent.httpClient.sslKeyStore.password", suffix));
            char[] keyStorePassword = (password == null) ? null : password.toCharArray();
            if (keyStoreFileName != null) {
                String keyStoreType = coalesce(config.get(HttpClientFactory.getConfigKeyName("adagent.httpClient.sslKeyStore.type", suffix)), KeyStore.getDefaultType());
                // Load the key store.
                keystore = KeyStore.getInstance(keyStoreType);
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(keyStoreFileName);
                    keystore.load(inputStream, keyStorePassword);
                }
                finally {
                    IOUtil.close(inputStream);
                }
            }

            // Get the X509 key manager.
            this.x509KeyManager = KeyManagers.getX509KeyManager(keystore, keyStorePassword);
        }
        catch (Exception ex) {
            throw new HttpClientError(ex.getMessage());
        }
    }

    public X509KeyManager getX509KeyManager() {
        return x509KeyManager;
    }
}
