/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.restclient;

import java.io.InputStream;

import com.github.ewbankkit.util.F1;
import com.github.ewbankkit.util.beans.BaseData;
import com.github.ewbankkit.util.beans.Pair;
import com.github.ewbankkit.util.httpclient.HttpClientUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * REST processor.
 */
public final class RestProcessor {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:28 RestProcessor.java NSI";

    private static final Log         logger      = LogFactory.getLog(RestProcessor.class);
    private static final Class<Void> NO_RESPONSE = void.class;

    private static final F1<HttpMethod, InputStream> getResponseBodyAsStream = new F1<HttpMethod, InputStream>() {
        @Override
        public InputStream apply(HttpMethod method) throws Exception {
            return method.getResponseBodyAsStream();
        }
    };

    private static final F1<HttpMethod, String> getResponseBodyAsString = new F1<HttpMethod, String>() {
        @Override
        public String apply(HttpMethod method) throws Exception {
            return HttpClientUtil.getResponseBodyAsString(method);
        }
    };

    private HttpClient           httpClient;
    private MessageConverter     messageConverter;
    private ResponseErrorHandler responseErrorHandler;

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void setResponseErrorHandler(ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;
    }

    public void delete(String uri) throws Exception {
        delete(uri, NO_RESPONSE);
    }

    public <T> T delete(String uri, Class<T> classOfT) throws Exception {
        return executeMethodWithResponse(new DeleteMethod(uri), classOfT);
    }

    public void get(String uri) throws Exception {
        get(uri, NO_RESPONSE);
    }

    public <T> T get(String uri, Class<T> classOfT) throws Exception {
        return executeMethodWithResponse(new GetMethod(uri), classOfT);
    }

    public InputStream getForInputStream(String uri) throws Exception {
        Pair<Integer, InputStream> httpStatusAndResponse = executeMethod(new GetMethod(uri), getResponseBodyAsStream);
        int httpStatus = httpStatusAndResponse.getFirst().intValue();
        if (HttpClientUtil.isHttpError(httpStatus)) {
            throw new RestClientException(HttpClientUtil.getHttpStatusText(httpStatus));
        }
        return httpStatusAndResponse.getSecond();
    }

    public void post(String uri, Object request) throws Exception {
        post(uri, request, NO_RESPONSE);
    }

    public <T> T post(String uri, Object request, Class<T> classOfT) throws Exception {
        return executeMethodWithResponse(new PostMethod(uri), request, classOfT);
    }

    public void put(String uri, Object request) throws Exception {
        put(uri, request, NO_RESPONSE);
    }

    public <T> T put(String uri, Object request, Class<T> classOfT) throws Exception {
        return executeMethodWithResponse(new PutMethod(uri), request, classOfT);
    }

    private <T> Pair<Integer, T> executeMethod(HttpMethod method, F1<HttpMethod, T> getResponseBody) throws Exception {
        if (httpClient == null) {
            throw new IllegalStateException();
        }

        int httpStatus = httpClient.executeMethod(method);
        logger.info(method.getName() + " " + method.getURI().getURI() + " " + Integer.toString(httpStatus));
        return Pair.from(Integer.valueOf(httpStatus), getResponseBody.apply(method));
    }

    private <T> T executeMethodWithResponse(HttpMethod method, Class<T> classOfT) throws Exception {
        if (messageConverter == null) {
            throw new IllegalStateException();
        }

        Pair<Integer, String> httpStatusAndResponse = executeMethod(method, getResponseBodyAsString);
        int httpStatus = httpStatusAndResponse.getFirst().intValue();
        String response = httpStatusAndResponse.getSecond();
        logger.debug(response);
        if (responseErrorHandler == null) {
            if (HttpClientUtil.isHttpError(httpStatus)) {
                throw new RestClientException(HttpClientUtil.getHttpStatusText(httpStatus));
            }
        }
        else {
            responseErrorHandler.handleAnyError(httpStatus, response);
        }

        if ((httpStatus == HttpStatus.SC_NO_CONTENT) || NO_RESPONSE.equals(classOfT) || Void.class.equals(classOfT)) {
            // No response.
            return null;
        }

        if (BaseData.stringIsBlank(response)) {
            throw new RestClientException("No response");
        }
        return messageConverter.fromString(response, classOfT);
    }

    private <T> T executeMethodWithResponse(EntityEnclosingMethod method, Object request, Class<T> classOfT) throws Exception {
        if (messageConverter == null) {
            throw new IllegalStateException();
        }

        if (request != null) {
            String content = messageConverter.toString(request);
            logger.debug(content);
            String mediaType = messageConverter.getMediaType();
            String characterSet = "UTF-8";
            method.setRequestEntity(new StringRequestEntity(content, mediaType, characterSet));
            method.setRequestHeader("Accept", mediaType);
            method.setRequestHeader("Accept-Charset", characterSet);
            method.setRequestHeader("Accept-Encoding", "gzip");
            method.setRequestHeader("Content-Type", mediaType + "; charset=" + characterSet);
        }
        return executeMethodWithResponse(method, classOfT);
    }
}
