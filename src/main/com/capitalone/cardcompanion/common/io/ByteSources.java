//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.io;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * ByteSource utilities.
 */
public final class ByteSources {
    private ByteSources() {}

    /**
     * Returns a ByteSource for the specified URL.
     */
    public static ByteSource fromURL(final String url) throws IOException {
        Preconditions.checkNotNull(url);

        return fromURL(new URL(url));
    }

    /**
     * Returns a ByteSource for the specified URL.
     */
    public static ByteSource fromURL(final URL url) throws IOException {
        Preconditions.checkNotNull(url);

        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return url.openStream();
            }
        };
    }
}
