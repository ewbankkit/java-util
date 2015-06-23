//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import com.capitalone.cardcompanion.common.Config;
import com.capitalone.cardcompanion.common.base.Either;
import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation;
import com.capitalone.cardcompanion.common.io.FileProperties;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import java.io.File;
import java.net.URL;

/**
 * Represents key store properties.
 */
public final class KeyStoreProperties {
    private final Optional<String> optionalKeyPassword;
    private final Optional<String> optionalPassword; // Store password.
    private final Optional<String> optionalType;
    private final FileProperties   storeProperties;

    /**
     * Constructor.
     */
    private KeyStoreProperties(FileProperties storeProperties, Optional<String> optionalKeyPassword, Optional<String> optionalPassword, Optional<String> optionalType) {
        Preconditions.checkNotNull(storeProperties);
        Preconditions.checkNotNull(optionalKeyPassword);
        Preconditions.checkNotNull(optionalPassword);
        Preconditions.checkNotNull(optionalType);

        this.optionalKeyPassword = optionalKeyPassword;
        this.optionalPassword = optionalPassword;
        this.optionalType = optionalType;
        this.storeProperties = storeProperties;
    }

    /**
     * Returns key store properties from the application configuration.
     */
    public static Optional<KeyStoreProperties> fromConfig(String prefix) {
        Preconditions.checkNotNull(prefix);

        Optional<FileProperties> optionalStoreProperties = FileProperties.fromConfig(prefix);
        if (!optionalStoreProperties.isPresent()) {
            return Optional.absent();
        }

        Config config = Config.getInstance();
        Optional<String> optionalPassword = config.getString(String.format("%s.password", prefix));
        Optional<String> optionalType = config.getString(String.format("%s.type", prefix));
        // If no key password was specified use the store password.
        Optional<String> optionalKeyPassword = config.getString(String.format("%s.keyPassword", prefix)).or(optionalPassword);

        return Optional.of(new KeyStoreProperties(optionalStoreProperties.get(), optionalKeyPassword, optionalPassword, optionalType));
    }

    public Either<File, URL> getFileOrResource() {
        return storeProperties.getFileOrResource();
    }

    @ReflectiveRepresentation.Ignore
    public ByteSource getByteSource() {
        return storeProperties.getByteSource();
    }

    public Optional<String> getOptionalKeyPassword() {
        return optionalKeyPassword;
    }

    public Optional<String> getOptionalPassword() {
        return optionalPassword;
    }

    public Optional<String> getOptionalType() {
        return optionalType;
    }

    @Override
    public String toString() {
        return ReflectiveRepresentation.toString(this);
    }
}
