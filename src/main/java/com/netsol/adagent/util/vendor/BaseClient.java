/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import org.apache.commons.httpclient.HttpClient;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.httpclient.HttpClientUtil;
import com.netsol.adagent.util.restclient.JsonMessageConverter;
import com.netsol.adagent.util.restclient.MessageConverter;
import com.netsol.adagent.util.restclient.RestProcessor;
import com.netsol.vendor.beans.Credentials;

/* package-private */ abstract class BaseClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:36 BaseClient.java NSI";

    protected static final String characterEncoding = HttpClientUtil.DEFAULT_CHARSET;

    // For some reason the report service doesn't return JSON, just plain strings.
    private static final MessageConverter messageConverter = new MessageConverter() {
        private final MessageConverter jsonMessageConverter = new JsonMessageConverter();

        public <T> T fromString(String string, Class<T> classOfT) throws Exception {
            if (String.class.equals(classOfT)) {
                return classOfT.cast(string);
            }
            return jsonMessageConverter.fromString(string, classOfT);
        }

        public String getMediaType() {
            return jsonMessageConverter.getMediaType();
        }

        public String toString(Object object) throws Exception {
            return jsonMessageConverter.toString(object);
        }};

    protected final RestProcessor restProcessor;
    protected final String rootUrl;

    /**
     * Constructor.
     */
    protected BaseClient(Credentials credentials, HttpClient httpClient) {
        rootUrl = credentials.getServiceUrl();
        HttpClientUtil.setBasicAuthenticationCredentials(
                httpClient,
                credentials.getUsername(),
                credentials.getPassword(),
                TrackingUtil.extractHostName(rootUrl));
        restProcessor = new RestProcessor();
        restProcessor.setHttpClient(httpClient);
        restProcessor.setMessageConverter(messageConverter);
    }
}
