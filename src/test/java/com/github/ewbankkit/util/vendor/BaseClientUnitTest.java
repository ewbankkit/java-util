/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.vendor;

import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;

import com.github.ewbankkit.util.config.Config;
import com.github.ewbankkit.util.httpclient.HttpClientFactory;
import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.client.ClientFactories;

abstract class BaseClientUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:37 BaseClientUnitTest.java NSI";

    private static HttpClient httpClient;

    protected static Credentials getCredentials() {
        Credentials credentials = new Credentials();
        credentials.setPassword("amppwd");
        credentials.setServiceUrl("http://aaapp1.dev.netsol.com:3260/adagent-ws/rest/");
        credentials.setUsername("ampuser");
        return credentials;
    }

    protected static void setupBuiltInClient() {
        Properties properties = new Properties();
        properties.setProperty("adagent.httpClient.connectionTimeoutMillis", "10000");
        properties.setProperty("adagent.httpClient.maxConnectionsPerHost", "8");
        properties.setProperty("adagent.httpClient.maxTotalConnections", "16");
        properties.setProperty("adagent.httpClient.socketTimeoutMillis", "60000");
        properties.setProperty("adagent.httpClient.connectionPoolTimeoutMillis", "0");
        properties.setProperty("adagent.httpClient.bufferWarningTriggerLimit", "5000000");
        properties.setProperty("adagent.httpClient.maxRedirects", "5");
        properties.setProperty("adagent.httpClient.methodRetryCount", "1");
        httpClient = HttpClientFactory.newHttpClient(new Config(properties) {});
        ClientFactories.setHttpClient(httpClient);
    }

    protected static void teardownBuiltInClient() {
        HttpClientFactory.closeHttpClient(httpClient);
    }
}
