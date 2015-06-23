//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.apiclients;

import com.capitalone.cardcompanion.common.Config;
import com.capitalone.cardcompanion.common.ExecutionContext;
import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation;
import com.capitalone.cardcompanion.common.restclient.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.ext.ContextResolver;
import java.net.URI;
import java.util.*;

/**
 * Abstract base class for Enterprise API REST clients.
 */
@ThreadSafe
public abstract class EnterpriseApiClientExt extends RestClient {
    private static final Optional<Map<String, String>> OPTIONAL_ADDITIONAL_HEADERS = Config.getInstance().getMap("enterpriseApiClient.additionalHeaders");

    // An explicit API version is sometimes required.
    private final Optional<Integer> optionalApiVersion   = Config.getFirst("version", configKeyPrefixes(), Config.GET_INTEGER);
    // A custom API key is sometimes required.
    private final Optional<String>  optionalCustomApiKey = Config.getFirst("apiKey", configKeyPrefixes(), Config.GET_STRING);

    // TODO
    // TODO Add a constructor that takes the API version as a parameter.
    // TODO

    /**
     * Constructor.
     */
    protected EnterpriseApiClientExt(Class<? extends ContextResolver<ObjectMapper>> classOfObjectMapper) {
        super(RestClientProperties.builder().
            extraProviderClasses(Collections.<Class<?>>singleton(classOfObjectMapper)).
            optionalHostnameVerifier(hostnameVerifierFromConfig("enterpriseApiClient.provision.hostnameVerifier.name")).
            optionalSslContext(sslContextFromConfig("enterpriseApiClient.sslContext")).
            serviceUrl(Config.getInstance().getString("enterpriseApiClient.provision.baseUrl").get()).
            build()
        );
    }

    /**
     * Invoke the specified request.
     */
    @Override
    @SuppressWarnings("UnnecessaryUnboxing")
    protected <T, U> Response<U> invoke(final Request<T, U> request) throws RestClientException {
        Preconditions.checkNotNull(request);

        // API version is passed via the Accept HTTP header.
        if (optionalApiVersion.isPresent()) {
            request.setAcceptVersion(optionalApiVersion.get().intValue());
        }
        if (optionalCustomApiKey.isPresent()) {
            request.setCustomApiKey(optionalCustomApiKey.get());
        }

        Map<String, String> headers = new HashMap<>();
        if (OPTIONAL_ADDITIONAL_HEADERS.isPresent()) {
            headers.putAll(OPTIONAL_ADDITIONAL_HEADERS.get());
        }
        ExecutionContext context = ExecutionContext.getContextForCurrentThread();
        if (context != null) {
            if (context.getExecutionContextId() != null) {
                headers.put("Correlation-Id", context.getExecutionContextId().toString());
            }
            if(context.getClientCorelationID()!=null)
            {
                headers.put("Client-Correlation-ID",context.getClientCorelationID().toString());
            }
        }
        request.addHttpHeaders(headers);

        return super.invoke(request);
    }

    protected static <T> Iterable<T> getUnmodifiableIterable(@Nullable Iterable<T> iterable) {
        return (iterable == null) ? null : Iterables.unmodifiableIterable(iterable);
    }

    protected static String getUrl(@Nullable Hypermedia hypermedia) {
        return (hypermedia == null) ? null : hypermedia.getHref();
    }

    /**
     * eAPI canonical address (https://pulse.kdc.capitalone.com/docs/DOC-61227).
     */
    public static interface Address {
        String getAddressLine1();
        String getAddressLine2();
        String getAddressLine3();
        String getAddressLine4();
        String getCity();
        String getCountryCode();
        String getPostalCode();
        String getStateCode();
    }

    protected static final class Hypermedia {
        private final String href;
        private final String method;

        @JsonCreator
        public Hypermedia(@JsonProperty("href") String href, @JsonProperty("method") String method) {
            this.href = href;
            this.method = method;
        }

        public String getHref() {
            return href;
        }

        public String getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return ReflectiveRepresentation.toString(this);
        }
    }

    /**
     * Iterator for a paged API.
     */
    protected abstract class PagedIterator<T> implements Iterable<T> {
        /**
         * Returns an iterator.
         */
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private boolean       hasNext = true;
                private Optional<URI> pageUri = Optional.absent();

                /**
                 * Returns true if the iteration has more elements.
                 */
                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                /**
                 * Returns the next element in the iteration.
                 */
                @Override
                @SuppressWarnings("UnnecessaryUnboxing")
                public T next() {
                    if (!hasNext) {
                        throw new NoSuchElementException();
                    }

                    GetRequest<T> request = pageRequest(pageUri);
                    hasNext = false;
                    T t;
                    try {
                        t = required(invokeForOptionalResponseEntity(request));
                    }
                    catch (RestClientException ex) {
                        throw new RuntimeException(ex);
                    }

                    Optional<String> pagingUrl = getPageUrl(t);
                    if (pagingUrl.isPresent()) {
                        URI uri = null;
                        try {
                            uri = URI.create(pagingUrl.get());
                        }
                        catch (IllegalArgumentException ignore) {}
                        if (uri != null) {
                            pageUri = Optional.of(uri);
                            hasNext = true;
                        }
                    }

                    return t;
                }

                /**
                 * Removes from the underlying collection the last element returned by this iterator.
                 */
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        /**
         * Returns an optional page URL.
         */
        protected abstract Optional<String> getPageUrl(T t);

        /**
         * Returns a request to get a page.
         */
        protected abstract GetRequest<T> pageRequest(Optional<URI> pageUri);
    }
}
