/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.httpclient;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ewbankkit.util.config.Config;

public class HttpClientFactoryUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:59 HttpClientFactoryUnitTest.java NSI";

    private static HttpClient httpClient;

    @BeforeClass
    public static void setup() {
        Properties properties = new Properties();
        properties.put("adagent.httpClient.connectionTimeoutMillis", "10000");
        properties.put("adagent.httpClient.maxConnectionsPerHost", "16");
        properties.put("adagent.httpClient.maxTotalConnections", "100");
        properties.put("adagent.httpClient.socketTimeoutMillis", "60000");
        properties.put("adagent.httpClient.connectionPoolTimeoutMillis", "0");
        properties.put("adagent.httpClient.bufferWarningTriggerLimit", "5000000");
        properties.put("adagent.httpClient.methodRetryCount", "1");
        properties.put("adagent.httpClient.sslCertificatePolicy", "STANDARD");
        httpClient = HttpClientFactory.newHttpClient(new Config(properties) {});
    }

    @AfterClass
    public static void teardown() {
        HttpClientFactory.closeHttpClient(httpClient);
    }

    @Test
    public void httpClientTest1() throws Exception {
        HttpMethod method = new GetMethod("http://www.google.com/");
        int httpStatus = httpClient.executeMethod(method);
        assertEquals(200, httpStatus);
    }

    @Test
    public void httpClientTest2() throws Exception {
        HttpMethod method = new GetMethod("https://www.bankofamerica.com/");
        int httpStatus = httpClient.executeMethod(method);
        assertEquals(200, httpStatus);
    }
}
