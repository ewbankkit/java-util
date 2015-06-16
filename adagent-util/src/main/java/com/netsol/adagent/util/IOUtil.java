/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.BaseData.arrayIsEmpty;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * I/O utilities.
 */
public final class IOUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:00 IOUtil.java NSI";

    public static int DEFAULT_BYTE_BUFFER_SIZE = 4096;

    private static final AtomicInteger byteBufferSize = new AtomicInteger(DEFAULT_BYTE_BUFFER_SIZE);

    private static final Copier DEFAULT_COPIER = new Copier() {
        /**
         * Copy all of an input stream to an output stream.
         * Return the number of bytes written to the output stream.
         */
        public long copyToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
            byte[] buffer = new byte[byteBufferSize.get()];
            int numRead = 0;
            long numWritten = 0;
            while ((numRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, numRead);
                numWritten += numRead;
            }

            return numWritten;
        }
    };

    public static void setByteBufferSize(int byteBufferSize) {
        if (byteBufferSize <= 0) {
            throw new IllegalArgumentException();
        }
        IOUtil.byteBufferSize.set(byteBufferSize);
    }

    public static int getByteBufferSize() {
        return byteBufferSize.intValue();
    }

    /**
     * Close a closeable.
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException ex) {}
        }
    }

    /**
     * Close closeables.
     */
    public static void close(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                close(closeable);
            }
        }
    }

    /**
     * Copy all of an input stream to an output file.
     * Return the number of bytes written to the output file.
     */
    public static long copyToFile(InputStream inputStream, File outputFile) throws IOException {
        return copyToFile(inputStream, outputFile, DEFAULT_COPIER);
    }

    /**
     * Copy all of an input stream to an output file using the specified copier.
     * Return the number of bytes written to the output file.
     */
    public static long copyToFile(InputStream inputStream, File outputFile, Copier copier) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputFile);

            return copier.copyToStream(inputStream, outputStream);
        }
        finally {
            close(outputStream);
        }
    }

    /**
     * Copy all of an input stream to an output file.
     * Return the number of bytes written to the output file.
     */
    public static long copyToFile(InputStream inputStream, String outputFileName) throws IOException {
        return copyToFile(inputStream, outputFileName, DEFAULT_COPIER);
    }

    /**
     * Copy all of an input stream to an output file using the specified copier.
     * Return the number of bytes written to the output file.
     */
    public static long copyToFile(InputStream inputStream, String outputFileName, Copier copier) throws IOException {
        return copyToFile(inputStream, new File(outputFileName), copier);
    }

    /**
     * Copy all of a byte array to an output stream.
     * Return the number of bytes written to the output stream.
     */
    public static long copyToStream(byte[] bytes, OutputStream outputStream) throws IOException {
        return copyToStream(bytes, outputStream, DEFAULT_COPIER);
    }

    /**
     * Copy all of a byte array to an output stream using the specified copier.
     * Return the number of bytes written to the output stream.
     */
    public static long copyToStream(byte[] bytes, OutputStream outputStream, Copier copier) throws IOException {
        if (arrayIsEmpty(bytes)) {
            return 0L;
        }

        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);

            return copier.copyToStream(byteArrayInputStream, outputStream);
        }
        finally {
            close(byteArrayInputStream);
        }
    }

    /**
     * Copy all of an input stream to an output stream.
     * Return the number of bytes written to the output stream.
     */
    public static long copyToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        return DEFAULT_COPIER.copyToStream(inputStream, outputStream);
    }

    /**
     * Return the contents of a binary buffer using the specified copier.
     */
    public static byte[] readBinaryBuffer(byte[] buffer, Copier copier) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(buffer);

            return readBinaryStream(inputStream, copier);
        }
        finally {
            close(inputStream);
        }
    }

    /**
     * Return the contents of a binary file.
     */
    public static byte[] readBinaryFile(String fileName) throws IOException {
        return readBinaryFile(fileName, DEFAULT_COPIER);
    }

    /**
     * Return the contents of a binary file using the specified copier.
     */
    public static byte[] readBinaryFile(String fileName, Copier copier) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));

            return readBinaryStream(inputStream, copier);
        }
        finally {
            close(inputStream);
        }
    }

    /**
     * Return the contents of a binary stream.
     */
    public static byte[] readBinaryStream(InputStream inputStream) throws IOException {
        return readBinaryStream(inputStream, DEFAULT_COPIER);
    }

    /**
     * Return the contents of a binary stream using the specified copier.
     */
    public static byte[] readBinaryStream(InputStream inputStream, Copier copier) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            copier.copyToStream(inputStream, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        }
        finally {
            close(byteArrayOutputStream);
        }
    }

    /**
     * Return the contents of a string as a byte array using the specified copier.
     */
    public static byte[] readBinaryString(String string, String charsetName, Copier copier) throws IOException {
        return readBinaryBuffer(string.getBytes(charsetName), copier);
    }

    /**
     * Return the contents of a text buffer using the specified copier.
     */
    public static String readTextBuffer(byte[] buffer, String charsetName, Copier copier) throws IOException {
        return new String(readBinaryBuffer(buffer, copier), charsetName);
    }

    /**
     * Return the contents of a text file using the default character encoding.
     */
    public static String readTextFile(String fileName) throws IOException {
        return readTextFile(fileName, Charset.defaultCharset().name());
    }

    /**
     * Return the contents of a text file.
     */
    public static String readTextFile(String fileName, String charsetName) throws IOException {
        return readTextFile(fileName, charsetName, DEFAULT_COPIER);
    }

    /**
     * Return the contents of a text file using the specified copier.
     */
    public static String readTextFile(String fileName, String charsetName, Copier copier) throws IOException {
        return new String(readBinaryFile(fileName, copier), charsetName);
    }

    /**
     * Return the contents of a text stream.
     */
    public static String readTextStream(InputStream inputStream, String charsetName) throws IOException {
        return readTextStream(inputStream, charsetName, DEFAULT_COPIER);
    }

    /**
     * Return the contents of a text stream using the specified copier.
     */
    public static String readTextStream(InputStream inputStream, String charsetName, Copier copier) throws IOException {
        return new String(readBinaryStream(inputStream, copier), charsetName);
    }

    /**
     * Constructor.
     */
    private IOUtil() {}

    /**
     * Interface implemented by copiers.
     */
    public static interface Copier {
        /**
         * Copy all of an input stream to an output stream.
         * Return the number of bytes written to the output stream.
         */
        public abstract long copyToStream(InputStream inputStream, OutputStream outputStream) throws IOException;
    };

    /**
     * Input stream factory.
     */
    public static interface InputStreamFactory {
        /**
         * Return a new input stream.
         */
        public abstract InputStream newInputStream(InputStream inputStream) throws IOException;
    };

    /**
     * Output stream factory.
     */
    public static interface OutputStreamFactory {
        /**
         * Return a new output stream.
         */
        public abstract OutputStream newOutputStream(OutputStream outputStream) throws IOException;
    };

    @SuppressWarnings("unused")
    private static class FileOutputStreamFactory implements OutputStreamFactory {
        private final File outputFile;

        /**
         * Constructor.
         */
        public FileOutputStreamFactory(File outputFile) {
            this.outputFile = outputFile;
        }

        /**
         * Constructor.
         */
        public FileOutputStreamFactory(String outputFileName) {
            this(new File(outputFileName));
        }

        /**
         * Return a new output stream.
         */
        public OutputStream newOutputStream(OutputStream outputStream) throws IOException {
            return new FileOutputStream(outputFile);
        }
    }
}
