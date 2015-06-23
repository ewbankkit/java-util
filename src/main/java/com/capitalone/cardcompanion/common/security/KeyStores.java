//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;

/**
 * Key store utilities.
 */
public final class KeyStores {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStores.class);

    /**
     * Creates a key store from a byte source.
     */
    public static KeyStore fromByteSource(ByteSource byteSource, Optional<String> optionalPassword, Optional<String> optionalType) throws GeneralSecurityException {
        Preconditions.checkNotNull(byteSource);
        Preconditions.checkNotNull(optionalPassword);
        Preconditions.checkNotNull(optionalType);

        char[] password = toCharArray(optionalPassword);
        String type = optionalType.or(KeyStore.getDefaultType());
        KeyStore keystore = KeyStore.getInstance(type);
        try (InputStream stream = byteSource.openStream()) {
            keystore.load(stream, password);
        }
        catch (IOException ex) {
            throw new GeneralSecurityException(ex);
        }

        return keystore;
    }

    /**
     * Creates a key store from properties.
     */
    public static KeyStore fromProperties(KeyStoreProperties properties) throws GeneralSecurityException {
        Preconditions.checkNotNull(properties);

        KeyStore keystore = fromByteSource(properties.getByteSource(), properties.getOptionalPassword(), properties.getOptionalType());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Key store ({}):{}", properties.getFileOrResource(), toString(keystore));
        }

        return keystore;
    }

    /**
     * Returns any X509 certificate and private key in the specified key store.
     */
    public static Optional<X509CertificateAndPrivateKey> getX509CertificateAndPrivateKey(KeyStore keystore, Optional<String> optionalKeyPassword) throws GeneralSecurityException {
        Preconditions.checkNotNull(keystore);
        Preconditions.checkNotNull(optionalKeyPassword);

        for (String alias : Collections.list(keystore.aliases())) {
            if (keystore.isKeyEntry(alias)) {
                Key key = keystore.getKey(alias, toCharArray(optionalKeyPassword));
                if (key instanceof PrivateKey) {
                    Certificate certificate = keystore.getCertificate(alias);
                    if (certificate instanceof X509Certificate) {
                        return Optional.of(new X509CertificateAndPrivateKey((X509Certificate)certificate, (PrivateKey)key));
                    }
                }
            }
        }

        return Optional.absent();
    }

    /**
     * Returns any key pair in the specified key store.
     */
    public static Optional<KeyPair> getKeyPair(KeyStore keystore, Optional<String> optionalKeyPassword) throws GeneralSecurityException {
        Preconditions.checkNotNull(keystore);
        Preconditions.checkNotNull(optionalKeyPassword);

        Optional<X509CertificateAndPrivateKey> optionalX509CertificateAndPrivateKey = getX509CertificateAndPrivateKey(keystore, optionalKeyPassword);
        if (optionalX509CertificateAndPrivateKey.isPresent()) {
            X509CertificateAndPrivateKey x509CertificateAndPrivateKey = optionalX509CertificateAndPrivateKey.get();
            return Optional.of(new KeyPair(x509CertificateAndPrivateKey.getX509Certificate().getPublicKey(), x509CertificateAndPrivateKey.getPrivateKey()));
        }

        return Optional.absent();
    }

    /**
     * Returns any X509 key manager for the specified key store.
     */
    public static Optional<X509KeyManager> getX509KeyManager(@Nullable KeyStore keystore, Optional<String> optionalKeyPassword) throws GeneralSecurityException {
        Preconditions.checkNotNull(optionalKeyPassword);

        char[] password = toCharArray(optionalKeyPassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, password);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        for (KeyManager KeyManager : keyManagers) {
            if (KeyManager instanceof X509KeyManager) {
                return Optional.of((X509KeyManager)KeyManager);
            }
        }
        return Optional.absent();
    }

    /**
     * Returns any X509 trust manager for the specified key store.
     */
    public static Optional<X509TrustManager> getX509TrustManager(@Nullable KeyStore keystore) throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return Optional.of((X509TrustManager)trustManager);
            }
        }
        return Optional.absent();
    }

    private static char[] toCharArray(Optional<String> optionalString) {
        return optionalString.isPresent() ? optionalString.get().toCharArray() : null;
    }

    /**
     * Returns a string representation of a key store.
     */
    private static CharSequence toString(KeyStore keystore) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String alias : Collections.list(keystore.aliases())) {
                if (keystore.isCertificateEntry(alias)) {
                    Certificate certificate = keystore.getCertificate(alias);
                    if (certificate instanceof X509Certificate) {
                        sb.append("\n[").append(alias).append(']').append(X509Certificates.toString((X509Certificate)certificate));
                    }
                }
                else if (keystore.isKeyEntry(alias)) {
                    sb.append("\n[").append(alias).append(']').append(" Private key");
                    Certificate certificate = keystore.getCertificate(alias);
                    if (certificate instanceof X509Certificate) {
                        sb.append(X509Certificates.toString((X509Certificate)certificate));
                    }
                }
            }

            return sb;
        }
        catch (KeyStoreException ignored) {}

        return "";
    }
}
