//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

import com.capitalone.cardcompanion.common.*;
import com.capitalone.cardcompanion.common.base.Either;
import com.capitalone.cardcompanion.common.base.Futures;
import com.capitalone.cardcompanion.common.jaxrs.HttpStatusAndEntity;
import com.capitalone.cardcompanion.common.jaxrs.Jackson2NoJaxbFeature;
import com.capitalone.cardcompanion.common.security.HostnameVerifiers;
import com.capitalone.cardcompanion.common.security.KeyStoreProperties;
import com.capitalone.cardcompanion.common.security.SslContexts;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer.Context;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.message.GZipEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.ACCEPT_CHARSET;
import static com.google.common.net.HttpHeaders.ACCEPT_ENCODING;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.MediaType.WILDCARD_TYPE;

/**
 * Abstract base class for simple REST clients.
 */
@ThreadSafe
public abstract class RestClient {
    private static final Class<RestClient> CLASS          = RestClient.class;
    private static final Logger            LOGGER         = LoggerFactory.getLogger(CLASS);
    private static final boolean           TIMING_ENABLED = Config.getInstance().getBoolean("outboundCall.metrics.enabled", false);
    private static final String            UA             = userAgent();

    protected final URI serviceUrl;

    public final Client client;

    /**
     * Constructor.
     */
    protected RestClient(RestClientProperties properties) {
        Preconditions.checkNotNull(properties);
        Preconditions.checkNotNull(properties.getServiceUrl());

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(Jackson2NoJaxbFeature.class);
        clientConfig.register(GZipEncoder.class);
        clientConfig.register(RestClientLoggingFilter.class);
        if (properties.getOptionalUsername().isPresent() && properties.getOptionalPassword().isPresent()) {
            clientConfig.register(HttpAuthenticationFeature.basic(properties.getOptionalUsername().get(), properties.getOptionalPassword().get()));
        }
        for (Class<?> providerClass : properties.getExtraProviderClasses()) {
            clientConfig.register(providerClass);
        }

        // Hierarchy of possible timeout settings.
        Config config = Config.getInstance();
        for (String configKeyPrefix : configKeyPrefixes()) {
            Optional<Integer> optionalConnectTimeout = config.getInteger(String.format("%s.connectTimeoutMillis", configKeyPrefix));
            if (optionalConnectTimeout.isPresent()) {
                clientConfig.property(ClientProperties.CONNECT_TIMEOUT, optionalConnectTimeout.get());
            }
            Optional<Integer> optionalReadTimeout = config.getInteger(String.format("%s.readTimeoutMillis", configKeyPrefix));
            if (optionalReadTimeout.isPresent()) {
                clientConfig.property(ClientProperties.READ_TIMEOUT, optionalReadTimeout.get());
            }
        }

        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        if (properties.getOptionalSslContext().isPresent()) {
            clientBuilder = clientBuilder.sslContext(properties.getOptionalSslContext().get());
        }
        if (properties.getOptionalHostnameVerifier().isPresent()) {
            clientBuilder = clientBuilder.hostnameVerifier(properties.getOptionalHostnameVerifier().get());
        }
        client = clientBuilder.withConfig(clientConfig).build();
        serviceUrl = UriBuilder.fromUri(properties.getServiceUrl()).build();
    }

    /**
     * Returns configuration key prefixes.
     */
    protected Iterable<String> configKeyPrefixes() {
        return configKeyPrefixes(getClass());
    }

    protected static Iterable<String> configKeyPrefixes(Class<? extends RestClient> classOfT) {
        return Config.keyPrefixes(classOfT, CLASS);
    }

    /**
     * Returns a hostname verifier from the application configuration.
     */
    protected static Optional<HostnameVerifier> hostnameVerifierFromConfig(String key) {
        Preconditions.checkNotNull(key);

        Optional<String> optionalHostnameVerifierName = Config.getInstance().getString(key);
        if (optionalHostnameVerifierName.isPresent()) {
            String hostnameVerifierName = optionalHostnameVerifierName.get();
            LOGGER.info("Using hostname verifier: {}", hostnameVerifierName);

            try {
                Method method = HostnameVerifiers.class.getMethod(hostnameVerifierName);
                return Optional.of((HostnameVerifier)method.invoke(null));
            }
            catch (Exception ex) {
                LOGGER.warn(String.format("Unable to create hostname verifier %s", hostnameVerifierName), ex);
            }
        }
        return Optional.absent();
    }

    /**
     * Invoke the specified request.
     */
    protected <T, U> Response<U> invoke(Request<T, U> request) throws RestClientException {
        Preconditions.checkNotNull(request);
        Preconditions.checkArgument(request.getUriOrPath() != null);

        Optional<OutboundCallType> optionalOutboundCallType = request.getOptionalOutboundCallType();
        Optional<String> optionalMetricName = optionalOutboundCallType.isPresent() ?
            Optional.of(MetricRegistry.name("OutboundCall", optionalOutboundCallType.get().toString()))
            :
            Optional.<String>absent();

        return response(
                invoke(invocation(request), optionalMetricName),
                request.getOptionalClassOfResponse(),
                request.getOptionalGenericTypeOfResponse()
        );
    }

    /**
     * Invoke the specified request.
     */
    protected final <T, U> Future<Response<U>> invokeForFuture(final Request<T, U> request) {
        return Futures.synchronousFuture(new Callable<Response<U>>() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             */
            @Override
            public Response<U> call() throws Exception {
                return invoke(request);
            }
        });
    }

    /**
     * Invoke the specified request.
     */
    protected final <T, U> Optional<U> invokeForOptionalResponseEntity(final Request<T, U> request) throws RestClientException {
        return invoke(request).getOptionalResponseEntity();
    }

    /**
     * Invoke the specified request.
     */
    protected final <T, U> Future<Optional<U>> invokeForOptionalResponseEntityFuture(final Request<T, U> request) {
        Preconditions.checkNotNull(request);

        return Futures.transform(invokeForFuture(request), new Function<Response<U>, Optional<U>>() {
            /**
             * Returns the result of applying this function to input.
             */
            @Nullable
            @Override
            public Optional<U> apply(@Nullable Response<U> input) {
                assert input != null;
                return input.getOptionalResponseEntity();
            }
        });
    }

    /**
     * Require that a response entity is present.
     */
    protected final <T> T required(final Optional<T> optionalOfT) throws RestClientException {
        Preconditions.checkNotNull(optionalOfT);

        if (optionalOfT.isPresent()) {
            return optionalOfT.get();
        }
        throw new MissingResponseEntityException();
    }

    /**
     * Require that a response entity is present.
     */
    protected final <T> Future<T> requiredFuture(final Future<Optional<T>> future) {
        Preconditions.checkNotNull(future);

        return Futures.transform(future, new Function<Optional<T>, T>() {
            /**
             * Returns the result of applying this function to input.
             */
            @Nullable
            @Override
            public T apply(@Nullable Optional<T> input) {
                try {
                    return required(input);
                } catch (RestClientException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    /**
     * Returns an SSL context from the application configuration.
     */
    protected static Optional<SSLContext> sslContextFromConfig(String prefix) {
        Preconditions.checkNotNull(prefix);

        Optional<Map<String, String>> optionalMap = Config.getInstance().getMap(prefix);
        if (optionalMap.isPresent()) {
            Map<String, String> map = optionalMap.get();
            String sslContextName = map.get("name");
            if (sslContextName != null) {
                LOGGER.info("Using SSL context: {}", sslContextName);

                String keyRef = map.get("keyref");
                Optional<KeyStoreProperties> optionalKeyProperties =
                    (keyRef == null) ? Optional.<KeyStoreProperties>absent() : KeyStoreProperties.fromConfig(keyRef);
                String trustRef = map.get("trustref");
                Optional<KeyStoreProperties> optionalTrustProperties =
                    (trustRef == null) ? Optional.<KeyStoreProperties>absent() : KeyStoreProperties.fromConfig(trustRef);
                try {
                    Method method = SslContexts.class.getMethod(sslContextName, Optional.class, Optional.class);
                    return Optional.of((SSLContext)method.invoke(null, optionalKeyProperties, optionalTrustProperties));
                }
                catch (Exception ex) {
                    LOGGER.warn(String.format("Unable to create SSL context %s", sslContextName), ex);
                }
            }
        }
        return Optional.absent();
    }

    /**
     * Creates an Invocation from the specified request.
     */
    private <T, U> Invocation invocation(Request<T, U> request) {
        // Resolve the path.
        Either<URI, String> uriOrPath = request.getUriOrPath();
        WebTarget webTarget =
            uriOrPath.isLeft() ? client.target(uriOrPath.getLeft()) : client.target(serviceUrl).path(uriOrPath.getRight());
        Optional<Map<String, Object>> optionalPathTemplateValues = request.getOptionalPathTemplateValues();
        if (optionalPathTemplateValues.isPresent()) {
            webTarget = webTarget.resolveTemplates(optionalPathTemplateValues.get());
        }
        Optional<Multimap<String, Object>> optionalQueryParameters = request.getOptionalQueryParameters();
        if (optionalQueryParameters.isPresent()) {
            for (String name: optionalQueryParameters.get().keySet()) {
                webTarget = webTarget.queryParam(name, optionalQueryParameters.get().get(name).toArray());
            }
        }

        // Build the invocation.
        Invocation.Builder builder = webTarget.request();
        builder.property(RestClientLoggingFilter.LOG_REQUEST_ENTITY_PROPERTY, request.getOptionalLogRequestEntity().orNull());
        builder.property(RestClientLoggingFilter.LOG_RESPONSE_ENTITY_PROPERTY, request.getOptionalLogResponseEntity().orNull());
        Optional<Integer> optionalReadTimeout = request.getOptionalReadTimeout();
        if (optionalReadTimeout.isPresent()) {
            builder.property(ClientProperties.READ_TIMEOUT, optionalReadTimeout.get());
        }
        // Response media types.
        MediaType acceptedMediaType;
        Optional<Class<U>> optionalClassOfResponse = request.getOptionalClassOfResponse();
        Optional<GenericType<U>> optionalGenericTypeOfResponse = request.getOptionalGenericTypeOfResponse();
        if (optionalClassOfResponse.isPresent()) {
            Class<U> classOfResponse = optionalClassOfResponse.get();
            if (StreamingOutput.class.equals(classOfResponse)) {
                acceptedMediaType = WILDCARD_TYPE;
            }
            else if (String.class.equals(classOfResponse)) {
                acceptedMediaType = TEXT_PLAIN_TYPE;
            }
            else {
                acceptedMediaType = APPLICATION_JSON_TYPE;
            }
        }
        else if (optionalGenericTypeOfResponse.isPresent()) {
            acceptedMediaType = APPLICATION_JSON_TYPE;
        }
        else {
            acceptedMediaType = WILDCARD_TYPE;
        }
        // HTTP headers.
        MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<>();
        StringBuilder sbAccept = new StringBuilder();
        sbAccept = sbAccept.append(acceptedMediaType);
        sbAccept = version(sbAccept, request.getOptionalAcceptVersion());
        httpHeaders.putSingle(ACCEPT, sbAccept);
        httpHeaders.putSingle(ACCEPT_CHARSET, StandardCharsets.UTF_8);
        httpHeaders.putSingle(ACCEPT_ENCODING, "gzip");
        httpHeaders.putSingle(USER_AGENT, UA);

        ExecutionContext context = ExecutionContext.getContextForCurrentThread();
        if (context != null) {
            if (context.getExecutionContextId() != null) {
                httpHeaders.putSingle("Execution-Context-Id", context.getExecutionContextId());
            }
            if (context.getSessionId() != null) {
                httpHeaders.putSingle("Session-Id", context.getSessionId());
            }
        }

        Optional<Map<String, String>> optionalHttpHeaders = request.getOptionalHttpHeaders();
        if (optionalHttpHeaders.isPresent()) {
            Optional<String> optionalCustomApiKey = request.getOptionalCustomApiKey();
            for (Map.Entry<String, String> httpHeader : optionalHttpHeaders.get().entrySet()) {
                String httpHeaderName = httpHeader.getKey();
                String httpHeaderValue = httpHeader.getValue();
                // Remove any existing header values.
                httpHeaders.remove(httpHeaderName);
                if ("Api-Key".equals(httpHeaderName) && optionalCustomApiKey.isPresent()) {
                    httpHeaders.putSingle(httpHeaderName, optionalCustomApiKey.get());
                }
                else if (httpHeaderValue != null) {
                    httpHeaders.putSingle(httpHeaderName, httpHeaderValue);
                }
            }
        }

        /*** Accept-Language ***/
        if (ApplicationLocale.getApplicationLocaleForCurrentThread() != null) {
            httpHeaders.putSingle("Accept-Language", ApplicationLocale.getApplicationLocaleForCurrentThread().getLocaleForCurrentRequest().toLanguageTag());
        }

        builder.headers(httpHeaders);
        // Request entity.
        String httpMethod = request.getHttpMethod();
        Invocation invocation;
        Optional<T> optionalRequestEntity = request.getOptionalRequestEntity();
        if (optionalRequestEntity.isPresent()) {
            T requestEntity = optionalRequestEntity.get();
            MediaType contentMediaType;
            if (requestEntity instanceof Form) {
                contentMediaType = APPLICATION_FORM_URLENCODED_TYPE;
            }
            else if (requestEntity instanceof String) {
                contentMediaType = TEXT_PLAIN_TYPE;
            }
            else {
                contentMediaType = APPLICATION_JSON_TYPE;
            }
            StringBuilder sbContent = new StringBuilder();
            sbContent = sbContent.append(contentMediaType);
            sbContent = version(sbContent, request.getOptionalContentTypeVersion());
            invocation = builder.build(httpMethod, Entity.entity(requestEntity, sbContent.toString()));
        }
        else {
            invocation = builder.build(httpMethod);
        }

        return invocation;
    }

    /**
     * Invokes an Invocation.
     */
    private static javax.ws.rs.core.Response invoke(
        Invocation       invocation,
        Optional<String> optionalMetricName
    ) {
        Context timer = (TIMING_ENABLED && optionalMetricName.isPresent()) ?
            Metrics.getInstance().getMetricRegistry().timer(optionalMetricName.get()).time()
            :
            null;
        try {
            return invocation.invoke();
        }
        finally {
            if (timer != null) {
                timer.stop();
            }
        }
    }

    /**
     * Creates a Response object.
     */
    private static <U> Response<U> response(
        final javax.ws.rs.core.Response result,
        Optional<Class<U>>              optionalClassOfResponse,
        Optional<GenericType<U>>        optionalGenericTypeOfResponse
    ) throws RestClientException {
        boolean streamingOutput = optionalClassOfResponse.isPresent() && StreamingOutput.class.equals(optionalClassOfResponse.get());

        try {
            boolean hasEntity = result.hasEntity();
            javax.ws.rs.core.Response.StatusType statusType = result.getStatusInfo();
            switch (statusType.getFamily()) {
            case SUCCESSFUL :   // 2xx
            case REDIRECTION:   // 3xx
                break;
            default         :
                if (hasEntity) {
                    throw new RestClientException(statusType, result.readEntity(String.class));
                }
                throw new RestClientException(statusType);
            }

            Response<U> response = new Response<>(statusType.getStatusCode());
            response.setHttpHeaders(result.getStringHeaders());
            if (hasEntity) {
                if (optionalClassOfResponse.isPresent()) {
                    Class<U> classOfResponse = optionalClassOfResponse.get();
                    if (streamingOutput) {
                        response.setResponseEntity(classOfResponse.cast(new StreamingOutput() {
                            /**
                             * Called to write the message body.
                             */
                            @Override
                            public void write(OutputStream output) throws IOException, WebApplicationException {
                                try (InputStream inputStream = result.readEntity(InputStream.class)) {
                                    ByteStreams.copy(inputStream, output);
                                }
                                output.flush();
                            }
                        }));
                    }
                    else {
                        response.setResponseEntity(result.readEntity(classOfResponse));
                    }
                }
                else if (optionalGenericTypeOfResponse.isPresent()) {
                    response.setResponseEntity(result.readEntity(optionalGenericTypeOfResponse.get()));
                }
            }

            return response;
        }
        finally {
            if (!streamingOutput) {
                result.close();
            }
        }
    }

    private static String userAgent() {
        StringBuilder sb = new StringBuilder("DigitalWalletOrchestrator");
        try {
            String version = Descriptor.getInstance().getVersion();
            if (version != null) {
                sb.append('/').append(version);
            }
        }
        catch (Exception ignored) {}
        return sb.toString();
    }

    private static StringBuilder version(StringBuilder sb, Optional<Integer> optionalVersion) {
        if (optionalVersion.isPresent()) {
            sb.append(";v=").append(optionalVersion.get());
        }
        return sb;
    }

    /**
     * Represents a REST client exception.
     */
    @SuppressWarnings("SerializableHasSerializationMethods")
    public static class RestClientException extends Exception {
        private static final long serialVersionUID = -4461303025402538990L;

        private final Optional<HttpStatusAndEntity> optionalHttpStatusAndEntity;

        /**
         * Constructor.
         */
        protected RestClientException(String message) {
            super(message);
            optionalHttpStatusAndEntity = Optional.absent();
        }

        /**
         * Constructor.
         */
        private RestClientException(javax.ws.rs.core.Response.StatusType statusType) {
            this(statusType, null);
        }

        /**
         * Constructor.
         */
        private RestClientException(javax.ws.rs.core.Response.StatusType statusType, @Nullable Object entity) {
            super(String.format("HTTP %d %s", statusType.getStatusCode(), statusType.getReasonPhrase()));
            optionalHttpStatusAndEntity = Optional.of(new HttpStatusAndEntity(statusType.getStatusCode(), Optional.fromNullable(entity)));
        }

        public Optional<HttpStatusAndEntity> getOptionalHttpStatusAndEntity() {
            return optionalHttpStatusAndEntity;
        }
    }

    /**
     * Indicates a missing response entity.
     */
    @SuppressWarnings("SerializableHasSerializationMethods")
    private static class MissingResponseEntityException extends RestClientException {
        private static final long serialVersionUID = 7415657515240148360L;

        /**
         * Constructor.
         */
        public MissingResponseEntityException() {
            super("Missing response entity");
        }
    }
}
