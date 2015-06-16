/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.github.ewbankkit.util.IOUtil;
import com.github.ewbankkit.util.IOUtil.Copier;
import com.github.ewbankkit.util.IOUtil.InputStreamFactory;
import com.github.ewbankkit.util.IOUtil.OutputStreamFactory;

/**
 * Compression and uncompression.
 */
public final class Zipper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:39 Zipper.java NSI";

    private static final Compressor DEFLATE_COMPRESSOR = new Compressor() {
        /**
         * Return a new input stream.
         */
        public InputStream newInputStream(InputStream inputStream) throws IOException {
            return new InflaterInputStream(inputStream);
        }

        /**
         * Return a new output stream.
         */
        public OutputStream newOutputStream(OutputStream outputStream) throws IOException {
            return new DeflaterOutputStream(outputStream);
        }
    };
    private static final Compressor GZIP_COMPRESSOR    = new Compressor() {
        /**
         * Return a new input stream.
         */
        public InputStream newInputStream(InputStream inputStream) throws IOException {
            return new GZIPInputStream(inputStream);
        }

        /**
         * Return a new output stream.
         */
        public OutputStream newOutputStream(OutputStream outputStream) throws IOException {
            return new GZIPOutputStream(outputStream);
        }
    };

    private static final Copier DEFLATE_COPIER = new CompressCopier(Zipper.DEFLATE_COMPRESSOR);
    private static final Copier GUNZIP_COPIER  = new UncompressCopier(Zipper.GZIP_COMPRESSOR);
    private static final Copier GZIP_COPIER    = new CompressCopier(Zipper.GZIP_COMPRESSOR);
    private static final Copier INFLATE_COPIER = new UncompressCopier(Zipper.DEFLATE_COMPRESSOR);
    private static final Copier UNZIP_COPIER   = new UnzipCopier();

    /**
     * Compress an input stream to an output stream using the deflate format.
     * Return the number of bytes written to the output stream.
     */
    public static long deflateToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        return Zipper.DEFLATE_COPIER.copyToStream(inputStream, outputStream);
    }

    /**
     * Uncompress a byte array compressed in the GZIP format to a string.
     * Return the string.
     */
    public static String gunzip(byte[] buffer, String charsetName) throws IOException {
        return IOUtil.readTextBuffer(buffer, charsetName, Zipper.GUNZIP_COPIER);
    }

    /**
     * Uncompress an input stream compressed in the GZIP format to a string.
     * Return the string.
     */
    public static String gunzip(InputStream inputStream, String charsetName) throws IOException {
        return IOUtil.readTextStream(inputStream, charsetName, Zipper.GUNZIP_COPIER);
    }

    /**
     * Uncompress an input stream compressed in the GZIP format to an output file.
     * Return the number of bytes written to the output stream.
     */
    public static long gunzipToFile(InputStream inputStream, String outputFileName) throws IOException {
        return IOUtil.copyToFile(inputStream, outputFileName, Zipper.GUNZIP_COPIER);
    }

    /**
     * Uncompress an input stream compressed in the GZIP format to an output stream.
     * Return the number of bytes written to the output stream.
     */
    public static long gunzipToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        return Zipper.GUNZIP_COPIER.copyToStream(inputStream, outputStream);
    }

    /**
     * Compress a string to a byte array using the GZIP format.
     * Return the byte array.
     */
    public static byte[] gzip(String string, String charsetName) throws IOException {
        return IOUtil.readBinaryString(string, charsetName, Zipper.GZIP_COPIER);
    }

    /**
     * Compress an input stream to an output stream using the GZIP format.
     * Return the number of bytes written to the output stream.
     */
    public static long gzipToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        return Zipper.GZIP_COPIER.copyToStream(inputStream, outputStream);
    }

    /**
     * Uncompress an input stream compressed in the deflate format to an output stream.
     * Return the number of bytes written to the output stream.
     */
    public static long inflateToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        return Zipper.INFLATE_COPIER.copyToStream(inputStream, outputStream);
    }

    /**
     * Uncompress an input stream compressed in the ZIP format to an output file.
     * Return the number of bytes written to the output file.
     */
    public static long unzipToFile(InputStream inputStream, String outputFileName) throws IOException {
        return IOUtil.copyToFile(inputStream, outputFileName, Zipper.UNZIP_COPIER);
    }

    /**
     * Uncompress an input stream compressed in the ZIP format to an output stream.
     * Return the number of bytes written to the output stream.
     */
    public static long unzipToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        return Zipper.UNZIP_COPIER.copyToStream(inputStream, outputStream);
    }

    /**
     * Compress a byte array to an output stream in the ZIP format.
     * Return the number of bytes written to the output stream.
     */
    public static long zipToStream(byte[] bytes, OutputStream outputStream, String name) throws IOException {
        return IOUtil.copyToStream(bytes, outputStream, new ZipCopier(name));
    }

    /**
     * Constructor.
     */
    private Zipper() {
        return;
    }

    /**
     * Interface implemented by compressors.
     */
    private static interface Compressor extends InputStreamFactory, OutputStreamFactory {
    };

    private static class CompressCopier implements Copier {
        private final Compressor compressor;

        /**
         * Constructor.
         */
        public CompressCopier(Compressor compressor) {
            this.compressor = compressor;

            return;
        }

        /**
         * Copy all of an input stream to an output stream.
         * Return the number of bytes written to the output stream.
         */
        public long copyToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
            OutputStream compressionOutputStream = null;
            try {
                compressionOutputStream = this.compressor.newOutputStream(outputStream);

                return IOUtil.copyToStream(inputStream, compressionOutputStream);
            }
            finally {
                IOUtil.close(compressionOutputStream);
            }
        }
    };

    private static class UncompressCopier implements Copier {
        private final Compressor compressor;

        /**
         * Constructor.
         */
        public UncompressCopier(Compressor compressor) {
            this.compressor = compressor;

            return;
        }

        /**
         * Copy all of an input stream to an output stream.
         * Return the number of bytes written to the output stream.
         */
        public long copyToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
            InputStream compressionInputStream = null;
            try {
                compressionInputStream = this.compressor.newInputStream(inputStream);

                return IOUtil.copyToStream(compressionInputStream, outputStream);
            }
            finally {
                IOUtil.close(compressionInputStream);
            }
        }
    };

    private static class UnzipCopier implements Copier {
        /**
         * Constructor.
         */
        public UnzipCopier() {
            return;
        }

        /**
         * Copy all of an input stream to an output stream.
         * Return the number of bytes written to the output stream.
         */
        public long copyToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
            // Copy to temporary file.
            File temporaryFile = null;
            try {
                temporaryFile = File.createTempFile("aa-", null);
                IOUtil.copyToFile(inputStream, temporaryFile);

                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(temporaryFile);
                    List<? extends ZipEntry> zipEntries = Collections.list(zipFile.entries());
                    if (!zipEntries.isEmpty()) {
                        InputStream zipInputStream = null;
                        try {
                            zipInputStream = zipFile.getInputStream(zipEntries.get(0));

                            return IOUtil.copyToStream(zipInputStream, outputStream);
                        }
                        finally {
                            IOUtil.close(zipInputStream);
                        }
                    }

                    return 0L;
                }
                finally {
                    if (zipFile != null) {
                        zipFile.close();
                    }
                }
            }
            finally {
                if (temporaryFile != null) {
                    temporaryFile.delete();
                }
            }
        }
    };

    private static class ZipCopier implements Copier {
        private final String name;

        /**
         * Constructor.
         */
        public ZipCopier(String name) {
            this.name = name;

            return;
        }

        /**
         * Copy all of an input stream to an output stream.
         * Return the number of bytes written to the output stream.
         */
        public long copyToStream(InputStream inputStream, OutputStream outputStream) throws IOException {
            ZipOutputStream zipOutputStream = null;
            try {
                zipOutputStream = new ZipOutputStream(outputStream);
                zipOutputStream.putNextEntry(new ZipEntry(this.name));

                try {
                    return IOUtil.copyToStream(inputStream, zipOutputStream);
                }
                finally {
                    zipOutputStream.closeEntry();
                }
            }
            finally {
                IOUtil.close(zipOutputStream);
            }
        }
    };

    // Test harness.
    public static void main(String[] args) {
        try {
            final String charsetName = "UTF-8";
            String string = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            System.out.println(string);
            byte[] buffer = Zipper.gzip(string, charsetName);
            string = Zipper.gunzip(buffer, charsetName);
            System.out.println(string);

            ByteArrayOutputStream outputStream = null;
            ByteArrayInputStream inputStream = null;
            try {
                outputStream = new ByteArrayOutputStream();
                Zipper.zipToStream(string.getBytes(), outputStream, "test");
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                Zipper.unzipToStream(inputStream, System.out);
            }
            finally {
                IOUtil.close(outputStream, inputStream);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
