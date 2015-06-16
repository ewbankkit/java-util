/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.httpclient;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

import com.netsol.adagent.util.IOUtil;
import com.netsol.adagent.util.zip.Zipper;

/**
 * HTTP client utilities.
 */
public final class HttpClientUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:59 HttpClientUtil.java NSI";

    public static final String DEFAULT_CHARSET = "UTF-8";

    private HttpClientUtil() {}

    public static String getHttpStatusText (int httpStatus) {
        return HttpStatus.getStatusText(httpStatus);
    }

    public static String getResponseBodyAsString(HttpMethod method) {
        InputStream responseBodyStream = null;
        try {
            responseBodyStream = method.getResponseBodyAsStream();
            Header contentEncodingHeader = method.getResponseHeader("Content-Encoding");
            String charsetName = (method instanceof HttpMethodBase) ? ((HttpMethodBase)method).getResponseCharSet() : DEFAULT_CHARSET;
            return isGzipEncoded(contentEncodingHeader, responseBodyStream) ?
                    Zipper.gunzip(responseBodyStream, charsetName) : new String(method.getResponseBody(), charsetName);
        }
        catch (IOException ex) {
            return null;
        }
        finally {
            IOUtil.close(responseBodyStream);
        }
    }

    /**
     * Is the content-encoding gzip?
     * Look at the header, and possibly the content.
     */
    public static boolean isGzipEncoded(Header contentEncodingHeader, InputStream responseBodyStream) throws IOException {
        String contentEncodingValue = null;
        if (contentEncodingHeader != null) {
            contentEncodingValue = contentEncodingHeader.getValue();
            if ((contentEncodingValue != null) && contentEncodingValue.toLowerCase().startsWith("gzip")) {
                return true;
            }
        }

        // Couldn't determine through the headers.
        // Try peeking at the first 2 bytes, looking for gzip magic numbers. (See RFC 1952)
        if (contentEncodingValue == null) {
            if (responseBodyStream.markSupported()) {
                responseBodyStream.mark(2);
                int id1 = responseBodyStream.read();
                int id2 = responseBodyStream.read();
                responseBodyStream.reset();
                if ((id1 == 0x1F) && (id2 == 0x8B)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Does the specified HTTP status code indicate an error?
     */
    public static boolean isHttpError(int httpStatus) {
        return (httpStatus >= HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Does the specified HTTP status code indicate an redirect?
     */
    public static boolean isHttpRedirect(int httpStatus) {
        return ((httpStatus == HttpStatus.SC_MOVED_PERMANENTLY)  ||
                 (httpStatus == HttpStatus.SC_MOVED_TEMPORARILY) ||
                 (httpStatus == HttpStatus.SC_SEE_OTHER)         ||
                 (httpStatus == HttpStatus.SC_TEMPORARY_REDIRECT));
    }

    public static void setBasicAuthenticationCredentials(HttpClient httpClient, String userName, String password) {
        setBasicAuthenticationCredentials(httpClient, userName, password, AuthScope.ANY);
    }

    public static void setBasicAuthenticationCredentials(HttpClient httpClient, String userName, String password, String hostName) {
        setBasicAuthenticationCredentials(httpClient, userName, password, new AuthScope(hostName, AuthScope.ANY_PORT));
    }

    private static void setBasicAuthenticationCredentials(HttpClient httpClient, String userName, String password, AuthScope authScope) {
        httpClient.getState().setCredentials(authScope, new UsernamePasswordCredentials(userName, password));
        httpClient.getParams().setAuthenticationPreemptive(true);
    }
}
