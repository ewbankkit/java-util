//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.apiclients;

import com.capitalone.cardcompanion.common.Config;
import com.capitalone.cardcompanion.common.ExecutionContext;
import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation;
import com.capitalone.cardcompanion.common.jaxrs.UnwrappedObjectMapperProvider;
import com.capitalone.cardcompanion.common.restclient.GetRequest;
import com.capitalone.cardcompanion.common.restclient.RestClient;
import com.capitalone.cardcompanion.common.restclient.RestClientProperties;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;

/**
 * Abstract base class for Innovation API REST clients.
 */
@ThreadSafe
public abstract class InnovationApiClient extends RestClient {
    protected static final String BASE_URL = baseUrl();

    private static final Optional<Map<String, String>> ADDITIONAL_HEADERS = Config.getInstance().getMap("innovationApiClient.additionalHeaders");
    private static final String                        API_KEY            = apiKey();
    private static final String                        SECRET             = secret();

    /**
     * Constructor.
     */
    protected InnovationApiClient(String serviceUrl) {
        super(RestClientProperties.builder().
            extraProviderClasses(Collections.<Class<?>>singleton(UnwrappedObjectMapperProvider.class)).
            optionalHostnameVerifier(hostnameVerifierFromConfig("innovationApiClient.hostnameVerifier.name")).
            optionalSslContext(sslContextFromConfig("innovationApiClient.sslContext")).
            serviceUrl(serviceUrl).
            build()
        );
    }

    /**
     * Returns API health status.
     */
    public Future<HealthStatus> getHealthStatus() {
        GetRequest<HealthStatus> request = new GetRequest<>();
        request.setClassOfResponse(HealthStatus.class);
        request.setHttpHeaders(httpHeaders());
        request.setPath("/health");

        return requiredFuture(invokeForOptionalResponseEntityFuture(request));
    }

    /**
     * Returns HTTP headers for the API key and secret.
     * Don't pre-compute as the signature contains the current timestamp.
     */
    protected Map<String, String> httpHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Api-Key", API_KEY);
        headers.put("Signature", signatureHeaderValue(API_KEY, SECRET));

        // Set the User-Id and Echo.
        ExecutionContext context = ExecutionContext.getContextForCurrentThread();
        if (context != null) {
            if (context.getExecutionContextId() != null) {
                headers.put("echo", context.getExecutionContextId().toString());
            }
            if (context.getUserId() != null) {
                headers.put("User-Id", context.getUserId().toString());
            }
        }

        if (ADDITIONAL_HEADERS.isPresent()) {
            headers.putAll(ADDITIONAL_HEADERS.get());
        }
        return headers;
    }

    /**
     * Returns the API key.
     */
    private static String apiKey() {
        Optional<String> optionalApiKey = Config.getInstance().getString("innovationApiClient.apiKey");
        Preconditions.checkState(optionalApiKey.isPresent());
        return optionalApiKey.get();
    }

    /**
     * Returns the base URL.
     */
    private static String baseUrl() {
        Optional<String> optionalEnterpriseApiBaseUrl = Config.getInstance().getString("innovationApiClient.baseUrl");
        Preconditions.checkState(optionalEnterpriseApiBaseUrl.isPresent());
        return optionalEnterpriseApiBaseUrl.get();
    }

    /**
     * Returns a new random alphanumeric string of the specified length.
     */
    private static String randomAlphanumericString(int length) {
        final char[] characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    /**
     * Returns the secret.
     */
    private static String secret() {
        Optional<String> optionalSecret = Config.getInstance().getString("innovationApiClient.secret");
        Preconditions.checkState(optionalSecret.isPresent());
        return optionalSecret.get();
    }

    /**
     * Returns a Signature HTTP header value for the specified API key and secret.
     */
    private static String signatureHeaderValue(String apiKey, String secret) {
        long timestamp = System.currentTimeMillis() / 1000L;
        String nonce = randomAlphanumericString(30);
        String signature = signatureString(apiKey, secret, nonce, timestamp);
        return String.format("nonce=\"%s\", timestamp=\"%d\", method=\"HMAC-SHA256\", signature=\"%s\"", nonce, timestamp, signature);
    }

    /**
     * Returns a signature string for the specified API key and secret.
     */
    private static String signatureString(String apiKey, String secret, String nonce, long timestamp) {
        String text = String.format("%s%s%s%d", apiKey, secret, nonce, timestamp);
        return BaseEncoding.base16().encode(Hashing.sha256().hashString(text, StandardCharsets.UTF_8).asBytes());
    }

    /**
     * API health status.
     */
    public static final class HealthStatus {
        private String message;
        private String name;
        private String version;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return ReflectiveRepresentation.toString(this);
        }
    }
}
