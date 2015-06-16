/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.httpclient;

import java.lang.reflect.Constructor;

import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import com.github.ewbankkit.util.config.Config;
import com.github.ewbankkit.util.ssl.AcceptAllTrustManager;
import com.github.ewbankkit.util.ssl.SelfSignedTrustManager;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com.github.ewbankkit.util.ssl.StandardTrustManager;

/**
 * HTTP client factory.
 */
public final class HttpClientFactory {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:59 HttpClientFactory.java NSI";

    private static final String HTTPS_SCHEME = new String(HttpsURL.DEFAULT_SCHEME);

    /**
     * Constructor.
     */
    private HttpClientFactory() {}

    /**
     * Close the specified HTTP client.
     */
    public static void closeHttpClient(HttpClient httpClient) {
        if (httpClient != null) {
            ((MultiThreadedHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
        }
    }

    /**
     * Return a new HTTP client.
     */
    public static HttpClient newHttpClient(Config config) {
        return newHttpClient(config, null);
    }

    /**
     * Return a new HTTP client.
     */
    public static HttpClient newHttpClient(Config config, String prefix) {
        HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams httpConnectionManagerParams = httpConnectionManager.getParams();
        httpConnectionManagerParams.setConnectionTimeout(config.getInt(getConfigKeyName("adagent.httpClient.connectionTimeoutMillis", prefix)));
        httpConnectionManagerParams.setDefaultMaxConnectionsPerHost(config.getInt(getConfigKeyName("adagent.httpClient.maxConnectionsPerHost", prefix)));
        httpConnectionManagerParams.setMaxTotalConnections(config.getInt(getConfigKeyName("adagent.httpClient.maxTotalConnections", prefix)));
        httpConnectionManagerParams.setSoTimeout(config.getInt(getConfigKeyName("adagent.httpClient.socketTimeoutMillis", prefix)));
        httpConnectionManagerParams.setTcpNoDelay(true);

        HttpClient httpClient = new HttpClient();
        httpClient.setHttpConnectionManager(httpConnectionManager);
        HttpClientParams httpClientParams = httpClient.getParams();
        httpClientParams.setConnectionManagerTimeout(config.getLong(getConfigKeyName("adagent.httpClient.connectionPoolTimeoutMillis", prefix)));
        // Pass all cookie headers without attempting to comply to any RFCs.
        // Any browser/server idiosyncrasies that exist are maintained.
        httpClientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        // Set the maximum buffered response size (in bytes) that triggers no warning.
        // Buffered responses exceeding this size will trigger a warning in the log.
        httpClientParams.setIntParameter(
                HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT,
                config.getInt(getConfigKeyName("adagent.httpClient.bufferWarningTriggerLimit", prefix)));
        httpClientParams.setParameter(
                HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(config.getInt(getConfigKeyName("adagent.httpClient.methodRetryCount", prefix)), false));
        int maxRedirects = config.getInt(getConfigKeyName("adagent.httpClient.maxRedirects", prefix));
        if (maxRedirects > 0) {
            httpClientParams.setIntParameter(HttpClientParams.MAX_REDIRECTS, maxRedirects);
            httpClientParams.setBooleanParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, false);
        }

        // Set any configured HTTP proxy.
        String proxyHost = config.get("adagent.httpClient.proxyHost");
        if (proxyHost != null) {
            httpClient.getHostConfiguration().setProxy(proxyHost, config.getInt("adagent.httpClient.proxyPort"));
        }

        // Setup any SSL certificate policy for HTTPS.
        String sslCertificatePolicy = config.get(getConfigKeyName("adagent.httpClient.sslCertificatePolicy", prefix));
        if (sslCertificatePolicy != null) {
            X509KeyManager x509KeyManager = new SslKeyStore(config, prefix).getX509KeyManager();
            X509TrustManager innerX509TrustManager = new SslTrustStore(config, prefix).getX509TrustManager();
            X509TrustManager outerX509TrustManager = null;
            if ("ACCEPT_ALL".equals(sslCertificatePolicy)) {
                outerX509TrustManager = new AcceptAllTrustManager(innerX509TrustManager);
            }
            else if ("SELF_SIGNED_NO_CHECK".equals(sslCertificatePolicy)) {
                outerX509TrustManager = new SelfSignedTrustManager(innerX509TrustManager, false);
            }
            else if ("SELF_SIGNED_WITH_CHECK".equals(sslCertificatePolicy)) {
                outerX509TrustManager = new SelfSignedTrustManager(innerX509TrustManager, true);
            }
            else if ("STANDARD".equals(sslCertificatePolicy)) {
                outerX509TrustManager = new StandardTrustManager(innerX509TrustManager);
            }
            else {
                // Assume the policy is a class name.
                try {
                    @SuppressWarnings("unchecked")
                    Class<X509TrustManager> clazz = (Class<X509TrustManager>)Class.forName(sslCertificatePolicy);
                    Constructor<X509TrustManager> constructor = clazz.getConstructor(X509TrustManager.class, Config.class);
                    outerX509TrustManager = constructor.newInstance(innerX509TrustManager, config);
                }
                catch (Throwable t) {}
            }
            if (outerX509TrustManager == null) {
                throw new HttpClientError("Unknown SSL certificate policy: " + sslCertificatePolicy);
            }

            ProtocolSocketFactory protocolSocketFactory = new SslProtocolSocketFactory(x509KeyManager, outerX509TrustManager);
            Protocol.registerProtocol(HTTPS_SCHEME, new Protocol(HTTPS_SCHEME, protocolSocketFactory, HttpsURL.DEFAULT_PORT));
        }

        return httpClient;
    }

    /**
     * Return the configuration key name.
     */
    /* package-private */ static String getConfigKeyName(String keyName, String prefix) {
        return (prefix == null) ? keyName : new StringBuilder(prefix).append('.').append(keyName).toString();
    }
}
