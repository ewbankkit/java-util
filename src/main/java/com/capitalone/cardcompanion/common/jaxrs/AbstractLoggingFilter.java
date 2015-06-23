//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.jaxrs;

import com.google.common.io.ByteStreams;
import org.slf4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for Jersey logging filters.
 */
public abstract class AbstractLoggingFilter {
    /**
     * Appends HTTP headers.
     */
    protected static void appendHeaders(StringBuilder sb, String prefix, MultivaluedMap<String, String> headers) {
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            sb.append(prefix).
                append(entry.getKey()).
                append(": ");
            boolean appendComma = false;
            for (String value : entry.getValue()) {
                if (appendComma) {
                    sb.append(',');
                }
                else {
                    appendComma = true;
                }
                sb.append(value);
            }
            sb.append('\n');
        }
    }

    /**
     * Returns an entity input stream.
     */
    protected static InputStream entityInputStream(final InputStream entityStream, final StringBuilder sb, final MediaType mediaType) throws IOException {
        InputStream inputStream = entityStream;
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(Integer.MAX_VALUE);
        byte[] entity = ByteStreams.toByteArray(inputStream);
        if (isPrintable(mediaType)) {
            sb.append(new String(entity, StandardCharsets.UTF_8));
        }
        else {
            sb.append("** ").append(entity.length).append(" byte(s) of data **");
        }
        inputStream.reset();
        return inputStream;
    }

    /**
     * Returns an entity output stream which writes summary info to a StringBuilder.
     */
    protected static OutputStream redactedOutputStream(final OutputStream entityStream, final StringBuilder sb) throws IOException {
        return new OutputStream() {
            private int bytesAppended;

            /**
             * Writes the specified byte to this output stream.
             */
            @Override
            public void write(int b) throws IOException {
                throw new UnsupportedOperationException();
            }

            /**
             * Writes len bytes from the specified byte array starting at offset off to this output stream.
             */
            @Override
            public void write(@SuppressWarnings("NullableProblems") byte b[], int off, int len) throws IOException {
                CharSequence message;
                final String format = "** %d byte(s) of data, redacted for security **";
                if (bytesAppended > 0) {
                    // We've already written a message to the StringBuffer; remove it.
                    message = String.format(format, bytesAppended);
                    int length = sb.length();
                    sb.setLength(length - message.length());
                }
                bytesAppended += len;
                message = String.format(format, bytesAppended);
                sb.append(message);
                entityStream.write(b, off, len);
            }
        };
    }

    /**
     * Returns an entity output stream.
     */
    protected static OutputStream entityOutputStream(final OutputStream entityStream, final StringBuilder sb, final MediaType mediaType) throws IOException {
        return new OutputStream() {
            private int           bytesAppended;
            private final boolean isPrintableMediaType = isPrintable(mediaType);

            /**
             * Writes the specified byte to this output stream.
             */
            @Override
            public void write(int b) throws IOException {
                throw new UnsupportedOperationException();
            }

            /**
             * Writes len bytes from the specified byte array starting at offset off to this output stream.
             */
            @Override
            public void write(@SuppressWarnings("NullableProblems") byte b[], int off, int len) throws IOException {
                if (isPrintableMediaType) {
                    sb.append(new String(b, off, len, StandardCharsets.UTF_8));
                    bytesAppended += len;
                }
                else {
                    CharSequence message;
                    final String format = "** %d byte(s) of data **";
                    if (bytesAppended > 0) {
                        message = String.format(format, bytesAppended);
                        int length = sb.length();
                        sb.setLength(length - message.length());
                    }
                    bytesAppended += len;
                    message = String.format(format, bytesAppended);
                    sb.append(message);
                }
                entityStream.write(b, off, len);
            }
        };
    }

    /**
     * Logs to the specified logger.
     */
    protected static void log(Logger logger, StringBuilder sb) {
        int length = sb.length();
        if (sb.charAt(length - 1) == '\n') {
            sb.setLength(length - 1);
        }
        logger.info(LoggingReplacer.getInstance().replaceAll(sb.toString()));
    }

    /**
     * Is the specified media type printable.
     */
    private static boolean isPrintable(MediaType mediaType) {
        return (mediaType != null) && (MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType) || "text".equalsIgnoreCase(mediaType.getType()));
    }
}
