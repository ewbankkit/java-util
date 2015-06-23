//
// Kit's Java Utils.
//

package com.github.ewbankkit.io;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ByteSink utilities.
 */
public final class ByteSinks {
    private ByteSinks() {}

    /**
     * Returns a ByteSink for the specified ByteArrayOutputStream.
     */
    public static ByteSink fromByteArrayOutputStream(final ByteArrayOutputStream baos) {
        Preconditions.checkNotNull(baos);

        return new ByteSink() {
            /**
             * Opens a new OutputStream for writing to this sink.
             */
            @Override
            public OutputStream openStream() throws IOException {
                baos.reset();
                return baos;
            }
        };
    }
}
