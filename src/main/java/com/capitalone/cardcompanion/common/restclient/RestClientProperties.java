//
// Copyright (C) Capital One Labs.
//
//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.restclient;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.util.Collection;
import java.util.Collections;

/**
 * REST client properties.
 */
public final class RestClientProperties {
    private Collection<Class<?>>       extraProviderClasses     = Collections.emptySet();
    private Optional<HostnameVerifier> optionalHostnameVerifier = Optional.absent();
    private Optional<String>           optionalPassword         = Optional.absent();
    private Optional<SSLContext>       optionalSslContext       = Optional.absent();
    private Optional<String>           optionalUsername         = Optional.absent();
    private String                     serviceUrl;

    Collection<Class<?>> getExtraProviderClasses() {
        return extraProviderClasses;
    }

    Optional<HostnameVerifier> getOptionalHostnameVerifier() {
        return optionalHostnameVerifier;
    }

    Optional<String> getOptionalPassword() {
        return optionalPassword;
    }

    Optional<SSLContext> getOptionalSslContext() {
        return optionalSslContext;
    }

    Optional<String> getOptionalUsername() {
        return optionalUsername;
    }

    String getServiceUrl() {
        return serviceUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements org.apache.commons.lang3.builder.Builder<RestClientProperties> {
        private final RestClientProperties restClientProperties = new RestClientProperties();

        private Builder() {}

        @Override
        public RestClientProperties build() {
            return restClientProperties;
        }

        public Builder extraProviderClasses(Collection<Class<?>> extraProviderClasses) {
            Preconditions.checkNotNull(extraProviderClasses);
            restClientProperties.extraProviderClasses = extraProviderClasses;
            return this;
        }

        public Builder optionalHostnameVerifier(Optional<HostnameVerifier> optionalHostnameVerifier) {
            Preconditions.checkNotNull(optionalHostnameVerifier);
            restClientProperties.optionalHostnameVerifier = optionalHostnameVerifier;
            return this;
        }

        public Builder optionalSslContext(Optional<SSLContext> optionalSslContext) {
            Preconditions.checkNotNull(optionalSslContext);
            restClientProperties.optionalSslContext = optionalSslContext;
            return this;
        }

        public Builder password(String password) {
            Preconditions.checkNotNull(password);
            restClientProperties.optionalPassword = Optional.of(password);
            return this;
        }

        public Builder serviceUrl(String serviceUrl) {
            Preconditions.checkNotNull(serviceUrl);
            restClientProperties.serviceUrl = serviceUrl;
            return this;
        }

        public Builder username(String username) {
            Preconditions.checkNotNull(username);
            restClientProperties.optionalUsername = Optional.of(username);
            return this;
        }
    }
}
