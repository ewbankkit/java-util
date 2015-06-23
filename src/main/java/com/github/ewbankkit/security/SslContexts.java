//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * SSL context utilities.
 */
public final class SslContexts {
    private static final TrustManagerFactory ACCEPT_ALL  = new TrustManagerFactory() {
        @Override
        public X509TrustManager create(X509TrustManager x509TrustManager) {
            return new AcceptAllTrustManager(x509TrustManager);
        }
    };
    private static final String              PROTOCOL    = "TLS";
    private static final TrustManagerFactory SELF_SIGNED = new TrustManagerFactory() {
        @Override
        public X509TrustManager create(X509TrustManager x509TrustManager) {
            return new SelfSignedTrustManager(x509TrustManager, true);
        }
    };
    private static final TrustManagerFactory STANDARD    = new TrustManagerFactory() {
        @Override
        public X509TrustManager create(X509TrustManager x509TrustManager) {
            return new StandardTrustManager(x509TrustManager);
        }
    };

    /**
     * Creates an SSL context that accepts all certificates.
     */
    public static SSLContext acceptAll(Optional<KeyStoreProperties> optionalKeyProperties, Optional<KeyStoreProperties> optionalTrustProperties) throws GeneralSecurityException {
        Preconditions.checkNotNull(optionalKeyProperties);
        Preconditions.checkNotNull(optionalTrustProperties);

        return sslContext(keyManager(optionalKeyProperties), trustManager(optionalTrustProperties, ACCEPT_ALL));
    }

    /**
     * Creates an SSL context that accepts self-signed certificates.
     */
    public static SSLContext selfSigned(Optional<KeyStoreProperties> optionalKeyProperties, Optional<KeyStoreProperties> optionalTrustProperties) throws GeneralSecurityException {
        Preconditions.checkNotNull(optionalKeyProperties);
        Preconditions.checkNotNull(optionalTrustProperties);

        return sslContext(keyManager(optionalKeyProperties), trustManager(optionalTrustProperties, SELF_SIGNED));
    }

    /**
     * Creates a standard SSL context.
     */
    public static SSLContext standard(Optional<KeyStoreProperties> optionalKeyProperties, Optional<KeyStoreProperties> optionalTrustProperties) throws GeneralSecurityException {
        Preconditions.checkNotNull(optionalKeyProperties);
        Preconditions.checkNotNull(optionalTrustProperties);

        return sslContext(keyManager(optionalKeyProperties), trustManager(optionalTrustProperties, STANDARD));
    }

    private static SSLContext sslContext(X509KeyManager keyManager, X509TrustManager trustManager) throws GeneralSecurityException {
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        KeyManager[] km = (keyManager == null) ? null : new KeyManager[] {keyManager};
        TrustManager[] tm = (trustManager == null) ? null : new TrustManager[] {trustManager};
        sslContext.init(km, tm, new SecureRandom());

        return sslContext;
    }

    private static X509KeyManager keyManager(Optional<KeyStoreProperties> optionalKeyProperties) throws GeneralSecurityException {
        KeyStore keystore = null;
        Optional<String> optionalKeyPassword = Optional.absent();
        if (optionalKeyProperties.isPresent()) {
            KeyStoreProperties properties = optionalKeyProperties.get();
            keystore = KeyStores.fromProperties(properties);
            optionalKeyPassword = properties.getOptionalKeyPassword();
        }
        Optional<X509KeyManager> optionalKeyManager = KeyStores.getX509KeyManager(keystore, optionalKeyPassword);
        Preconditions.checkState(optionalKeyManager.isPresent());
        return optionalKeyManager.get();
    }

    private static X509TrustManager trustManager(Optional<KeyStoreProperties> optionalTrustProperties, TrustManagerFactory factory) throws GeneralSecurityException {
        KeyStore keystore = null;
        if (optionalTrustProperties.isPresent()) {
            keystore = KeyStores.fromProperties(optionalTrustProperties.get());
        }
        Optional<X509TrustManager> optionalTrustManager = KeyStores.getX509TrustManager(keystore);
        Preconditions.checkState(optionalTrustManager.isPresent());
        return factory.create(optionalTrustManager.get());
    }

    private static interface TrustManagerFactory {
        abstract X509TrustManager create(X509TrustManager x509TrustManager);
    }
}
