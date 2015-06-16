/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

/**
 * A factory for creating SSL sockets.
 */
public abstract class SslSocketFactory extends SSLSocketFactory {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:29 SslSocketFactory.java NSI";

    private final KeyManager[] keyManagers;
    private final TrustManager[] trustManagers;
    private SSLContext sslContext;

    /**
     * Constructor.
     */
    protected SslSocketFactory(X509KeyManager x509KeyManager, X509TrustManager x509TrustManager) {
        this(new KeyManager[] {x509KeyManager}, new TrustManager[] {x509TrustManager});
    }

    /**
     * Constructor.
     */
    protected SslSocketFactory(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        this.keyManagers = keyManagers;
        this.trustManagers = trustManagers;
    }

    /**
     * Creates an unconnected socket.
     */
    @Override
    public Socket createSocket() throws IOException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    /**
     * Returns a socket layered over an existing socket connected to the named host, at the given port.
     */
    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(s, host, port, autoClose);
    }

    /**
     * Returns the list of cipher suites which are enabled by default.
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return getSSLContext().getSocketFactory().getDefaultCipherSuites();
    }

    /**
     * Returns the names of the cipher suites which could be enabled for use on an SSL connection.
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return getSSLContext().getSocketFactory().getSupportedCipherSuites();
    }

    /**
     * Creates a socket and connects it to the specified remote host at the specified remote port.
     */
    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    /**
     * Creates a socket and connects it to the specified port number at the specified address.
     */
    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    /**
     * Creates a socket and connect it to the specified remote address on the specified remote port.
     */
    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port, localHost, localPort);
    }

    /**
     * Creates a socket and connects it to the specified remote host on the specified remote port.
     */
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(address, port, localAddress, localPort);
    }

    private synchronized SSLContext getSSLContext() {
        if (sslContext == null) {
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(keyManagers, trustManagers, new SecureRandom());
            }
            catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        }
        return sslContext;
    }
}
