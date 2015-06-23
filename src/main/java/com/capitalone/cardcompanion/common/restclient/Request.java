//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

import com.capitalone.cardcompanion.common.OutboundCallType;
import com.capitalone.cardcompanion.common.base.Either;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import javax.ws.rs.core.GenericType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Represents a REST client request.
 */
public abstract class Request<T, U> {
    protected static final String DELETE = "DELETE";
    protected static final String GET    = "GET";
    protected static final String POST   = "POST";
    protected static final String PUT    = "PUT";

    private final String httpMethod;

    private Optional<Integer>                  optionalAcceptVersion         = Optional.absent();
    private Optional<Class<U>>                 optionalClassOfResponse       = Optional.absent();
    private Optional<Integer>                  optionalContentTypeVersion    = Optional.absent();
    private Optional<GenericType<U>>           optionalGenericTypeOfResponse = Optional.absent();
    private Optional<Map<String, String>>      optionalHttpHeaders           = Optional.absent();
    private Optional<Boolean>                  optionalLogRequestEntity      = Optional.absent();
    private Optional<Boolean>                  optionalLogResponseEntity     = Optional.absent();
    private Optional<OutboundCallType>         optionalOutboundCallType      = Optional.absent();
    private Optional<Map<String, Object>>      optionalPathTemplateValues    = Optional.absent();
    private Optional<Integer>                  optionalReadTimeout           = Optional.absent();
    private Optional<Multimap<String, Object>> optionalQueryParameters       = Optional.absent();
    private Optional<T>                        optionalRequestEntity         = Optional.absent();
    private Optional<String>                   optionalCustomApiKey          = Optional.absent();
    private Either<URI, String>                uriOrPath;

    /**
     * Constructor.
     */
    protected Request(String httpMethod) {
        Preconditions.checkNotNull(httpMethod);

        this.httpMethod = httpMethod;
    }

    Optional<Integer> getOptionalAcceptVersion() {
        return optionalAcceptVersion;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void setAcceptVersion(int acceptVersion) {
        Preconditions.checkArgument(acceptVersion > 0);

        optionalAcceptVersion = Optional.of(Integer.valueOf(acceptVersion));
    }

    Optional<Class<U>> getOptionalClassOfResponse() {
        return optionalClassOfResponse;
    }

    public void setClassOfResponse(Class<U> classOfResponse) {
        Preconditions.checkNotNull(classOfResponse);
        Preconditions.checkState(!optionalGenericTypeOfResponse.isPresent());

        optionalClassOfResponse = Optional.of(classOfResponse);
    }

    Optional<Integer> getOptionalContentTypeVersion() {
        return optionalContentTypeVersion;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void setContentTypeVersion(int contentTypeVersion) {
        Preconditions.checkArgument(contentTypeVersion > 0);

        optionalContentTypeVersion = Optional.of(Integer.valueOf(contentTypeVersion));
    }

    Optional<GenericType<U>> getOptionalGenericTypeOfResponse() {
        return optionalGenericTypeOfResponse;
    }

    public void setGenericTypeOfResponse(GenericType<U> genericTypeOfResponse) {
        Preconditions.checkNotNull(genericTypeOfResponse);
        Preconditions.checkState(!optionalClassOfResponse.isPresent());

        optionalGenericTypeOfResponse = Optional.of(genericTypeOfResponse);
    }

    public Optional<Map<String, String>> getOptionalHttpHeaders() {
        return optionalHttpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        Preconditions.checkNotNull(httpHeaders);

        optionalHttpHeaders = Optional.of(httpHeaders);
    }

    public void addHttpHeaders(Map<String, String> httpHeaders) {
        Preconditions.checkNotNull(httpHeaders);

        Map<String, String> existingHttpHeaders = new HashMap<>();
        if (optionalHttpHeaders.isPresent()) {
            existingHttpHeaders.putAll(optionalHttpHeaders.get());
        }
        existingHttpHeaders.putAll(httpHeaders);
        optionalHttpHeaders = Optional.of(existingHttpHeaders);
    }

    public void addHttpHeader(String name, String value) {
        Preconditions.checkNotNull(name);

        if (!optionalHttpHeaders.isPresent()) {
            optionalHttpHeaders = Optional.of((Map<String, String>)new HashMap<String, String>());
        }
        optionalHttpHeaders.get().put(name, value);
    }

    String getHttpMethod() {
        return httpMethod;
    }

    Optional<Boolean> getOptionalLogRequestEntity() {
        return optionalLogRequestEntity;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void setLogRequestEntity(boolean logEntity) {
        optionalLogRequestEntity = Optional.of(Boolean.valueOf(logEntity));
    }

    Optional<OutboundCallType> getOptionalOutboundCallType() {
        return optionalOutboundCallType;
    }

    public void setOutboundCallType(OutboundCallType outboundCallType) {
        Preconditions.checkNotNull(outboundCallType);

        optionalOutboundCallType = Optional.of(outboundCallType);
    }

    Optional<Boolean> getOptionalLogResponseEntity() {
        return optionalLogResponseEntity;
    }

    @SuppressWarnings({"UnnecessaryBoxing", "unused"})
    public void setLogResponseEntity(boolean logEntity) {
        optionalLogResponseEntity = Optional.of(Boolean.valueOf(logEntity));
    }

    Optional<Map<String, Object>> getOptionalPathTemplateValues() {
        return optionalPathTemplateValues;
    }

    public void setPathTemplateValues(Map<String, Object> pathTemplateValues) {
        Preconditions.checkNotNull(pathTemplateValues);

        optionalPathTemplateValues = Optional.of(pathTemplateValues);
    }

    Optional<Integer> getOptionalReadTimeout() {
        return optionalReadTimeout;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public void setReadTimeout(long timeout, TimeUnit unit) {
        Preconditions.checkArgument(timeout > 0L);
        Preconditions.checkNotNull(unit);

        optionalReadTimeout = Optional.of(Integer.valueOf((int)unit.convert(timeout, TimeUnit.MILLISECONDS)));
    }

    Optional<Multimap<String, Object>> getOptionalQueryParameters() {
        return optionalQueryParameters;
    }

    public void addQueryParameter(String name, @Nullable Object value) {
        Preconditions.checkNotNull(name);

        createQueryParameters();
        optionalQueryParameters.get().put(name, value);
    }

    public void addQueryParameters(String name, Iterable<?> values) {
        Preconditions.checkNotNull(name);

        createQueryParameters();
        optionalQueryParameters.get().putAll(name, values);
    }

    Optional<T> getOptionalRequestEntity() {
        return optionalRequestEntity;
    }

    public void setRequestEntity(T requestEntity) {
        Preconditions.checkNotNull(requestEntity);

        optionalRequestEntity = Optional.of(requestEntity);
    }

    Optional<String> getOptionalCustomApiKey() {
        return optionalCustomApiKey;
    }

    public void setCustomApiKey(String customApiKey) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(customApiKey));

        optionalCustomApiKey = Optional.of(customApiKey);
    }

    public void setPath(String path) {
        Preconditions.checkNotNull(path);

        uriOrPath = Either.right(path);
    }

    public void setUri(URI uri) {
        Preconditions.checkNotNull(uri);

        uriOrPath = Either.left(uri);
    }

    Either<URI, String> getUriOrPath() {
        return uriOrPath;
    }

    private void createQueryParameters() {
        if (!optionalQueryParameters.isPresent()) {
            Multimap<String, Object> queryParameters = ArrayListMultimap.create();
            optionalQueryParameters = Optional.of(queryParameters);
        }
    }
}
