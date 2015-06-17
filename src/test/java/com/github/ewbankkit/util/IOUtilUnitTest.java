/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class IOUtilUnitTest {
    private Path   tempFile;
    private String tempFileName;

    @Before
    public void before() {
        tempFile = Paths.get(System.getProperty("java.io.tmpdir"), "test.txt");
        tempFileName = tempFile.toString();
    }

    @Test
    public void readTextFileTest1() throws IOException {
        Files.write(tempFile, "Some stuff".getBytes());
        String string = IOUtil.readTextFile(tempFileName);
        assertTrue(string.length() > 0);
    }

    @Test
    public void readBinaryFileTest1() throws IOException {
        Files.write(tempFile, "Some stuff".getBytes());
        byte[] bytes = IOUtil.readBinaryFile(tempFileName);;
        assertTrue(bytes.length > 0);
        IOUtil.copyToStream(bytes, System.out);
    }
}
