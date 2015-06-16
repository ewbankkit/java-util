/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.httpclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import com.github.ewbankkit.util.ssl.SslSocketFactory;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

/**
 * A factory for creating SSL sockets.
 */
/* package-private */ class SslProtocolSocketFactory implements SecureProtocolSocketFactory {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:00 SslProtocolSocketFactory.java NSI";

    private final SslSocketFactory sslSocketFactory;

    /**
     * Constructor.
     */
    public SslProtocolSocketFactory(X509KeyManager x509KeyManager, X509TrustManager x509TrustManager) {
        sslSocketFactory = new SslSocketFactory(x509KeyManager, x509TrustManager) {};
    }

    /**
     * Get a new socket connection to the specified host.
     */
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(host, port);
    }

    /**
     * Get a new socket connection to the specified host.
     */
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(host, port, localAddress, localPort);
    }

    /**
     * Get a new socket connection to the specified host.
     */
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams httpConnectionParams) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (httpConnectionParams == null) {
            throw new IllegalArgumentException("httpConnectionParams may not be null");
        }

        int connectionTimeout = httpConnectionParams.getConnectionTimeout();
        if (connectionTimeout == 0) {
            return sslSocketFactory.createSocket(host, port, localAddress, localPort);
        }

        Socket socket = sslSocketFactory.createSocket();
        SocketAddress localSocketAddress = new InetSocketAddress(localAddress, localPort);
        SocketAddress remoteSocketAddress = new InetSocketAddress(host, port);
        socket.bind(localSocketAddress);
        socket.connect(remoteSocketAddress, connectionTimeout);
        return socket;
    }

    /**
     * Get a new socket connection to the specified host that is layered over an existing socket.
     */
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(socket, host, port, autoClose);
    }
}
