//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.io;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.reflect.Reflection;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Directory utilities.
 */
public final class Directories {
    private static final String TEMP_DIRECTORY_PREFIX = String.format("%s.", Reflection.getPackageName(Directories.class));

    /**
     * Creates a new directory in the default temporary-file directory.
     */
    public static Path createTempDirectory() throws IOException {
        return java.nio.file.Files.createTempDirectory(TEMP_DIRECTORY_PREFIX);
    }

    /**
     * Recursively delete the specified directory.
     */
    public static void recursiveCopy(final Path source, final Path target) throws IOException {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(target);

        java.nio.file.Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            /**
             * Invoked for a directory before entries in the directory are visited.
             */
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                try {
                    java.nio.file.Files.copy(dir, targetDir);
                }
                catch (FileAlreadyExistsException ex) {
                    if (!java.nio.file.Files.isDirectory(targetDir)) {
                        throw ex;
                    }
                }

                return FileVisitResult.CONTINUE;
            }

            /**
             * Invoked for a file in a directory.
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                java.nio.file.Files.copy(file, target.resolve(source.relativize(file)));

                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Recursively delete the specified directory.
     */
    public static void recursiveDelete(final Path path) throws IOException {
        Preconditions.checkNotNull(path);

        java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            /**
             * Invoked for a directory after entries in the directory, and all their descendants, have been visited,
             */
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }

                java.nio.file.Files.delete(dir);

                return FileVisitResult.CONTINUE;
            }

            /**
             * Invoked for a file in a directory.
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                java.nio.file.Files.delete(file);

                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Returns a map of relative file names and associated hashes.
     */
    public static Map<String, String> recursiveHashes(final Path path, final HashFunction hashFunction) throws IOException {
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(hashFunction);

        final Map<String, String> hashes = new HashMap<>();

        java.nio.file.Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            /**
             * Invoked for a file in a directory.
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = path.relativize(file);
                HashCode hashCode = com.google.common.io.Files.hash(file.toFile(), hashFunction);
                hashes.put(relativePath.toString(), hashCode.toString());

                return FileVisitResult.CONTINUE;
            }
        });

        return hashes;
    }

    /**
     * Recursively zips the specified directory.
     */
    public static void recursiveZip(final Path path, final ZipOutputStream zipOutputStream) throws IOException {
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(zipOutputStream);

        java.nio.file.Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            /**
             * Invoked for a file in a directory.
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = path.relativize(file);
                zipOutputStream.putNextEntry(new ZipEntry(relativePath.toString()));
                com.google.common.io.Files.asByteSource(file.toFile()).copyTo(zipOutputStream);

                return FileVisitResult.CONTINUE;
            }
        });

    }

    private Directories() {}
}
