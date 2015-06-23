//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;

/**
 * Represents a REST client response.
 */
public final class Response<T> {
    private final int httpStatus;

    private Optional<Map<String, List<String>>> optionalHttpHeaders    = Optional.absent();
    private Optional<T>                         optionalResponseEntity = Optional.absent();

    /**
     * Constructor.
     */
    public Response(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public Optional<String> getOptionalHttpHeader(String name) {
        Preconditions.checkNotNull(name);

        if (optionalHttpHeaders.isPresent()) {
            List<String> values = optionalHttpHeaders.get().get(name);
            if ((values != null) && !values.isEmpty()) {
                return Optional.fromNullable(values.get(0));
            }
        }

        return Optional.absent();
    }

    void setHttpHeaders(Map<String, List<String>> httpHeaders) {
        Preconditions.checkNotNull(httpHeaders);

        this.optionalHttpHeaders = Optional.of(httpHeaders);
    }

    public Optional<T> getOptionalResponseEntity() {
        return optionalResponseEntity;
    }

    void setResponseEntity(T responseEntity) {
        Preconditions.checkNotNull(responseEntity);

        this.optionalResponseEntity = Optional.of(responseEntity);
    }
}
