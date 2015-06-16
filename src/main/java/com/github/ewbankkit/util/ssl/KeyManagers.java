/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.ssl;

import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

/**
 * Key manager utilities.
 */
public final class KeyManagers {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:29 KeyManagers.java NSI";

    public static X509KeyManager getX509KeyManager(KeyStore keystore, char[] password) throws GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, password);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        for (KeyManager KeyManager : keyManagers) {
            if (KeyManager instanceof X509KeyManager) {
                return (X509KeyManager)KeyManager;
            }
        }
        return null;
    }
}
