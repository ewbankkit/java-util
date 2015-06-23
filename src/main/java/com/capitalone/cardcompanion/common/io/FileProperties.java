//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.io;

import com.capitalone.cardcompanion.common.Config;
import com.capitalone.cardcompanion.common.base.Either;
import com.capitalone.cardcompanion.common.base.ReflectiveRepresentation;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.net.URL;

/**
 * Represents file properties.
 */
public final class FileProperties {
    private final Either<File, URL> fileOrResource;

    /**
     * Constructor.
     */
    private FileProperties(Either<File, URL> fileOrResource) {
        Preconditions.checkNotNull(fileOrResource);

        this.fileOrResource = fileOrResource;
    }

    /**
     * Returns file properties from the application configuration.
     */
    public static Optional<FileProperties> fromConfig(String prefix) {
        Preconditions.checkNotNull(prefix);

        Config config = Config.getInstance();
        Optional<String> optionalFile = config.getString(String.format("%s.file", prefix));
        Optional<String> optionalResource = config.getString(String.format("%s.resource", prefix));
        if (!optionalFile.isPresent() && !optionalResource.isPresent()) {
            return Optional.absent();
        }

        Either<File, URL> fileOrResource = optionalFile.isPresent() ?
            Either.<File, URL>left(new File(optionalFile.get())) : Either.<File, URL>right(Resources.getResource(optionalResource.get()));

        return Optional.of(new FileProperties(fileOrResource));
    }

    public Either<File, URL> getFileOrResource() {
        return fileOrResource;
    }

    @ReflectiveRepresentation.Ignore
    public ByteSource getByteSource() {
        return fileOrResource.isLeft() ? Files.asByteSource(fileOrResource.getLeft()) : Resources.asByteSource(fileOrResource.getRight());
    }

    @Override
    public String toString() {
        return ReflectiveRepresentation.toString(this);
    }
}
