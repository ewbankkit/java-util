//
// Kit's Java Utils.
//

package com.github.ewbankkit.io;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSink;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Test directory utilities.
 */
public final class DirectoriesUnitTest {

    @Test
    public void testTempDirectory1() throws Exception {
        Path temp = Directories.createTempDirectory();
        assertNotNull(temp);
        String s = temp.toString();
        assertNotNull(s);
        Files.delete(temp);
    }

    @Test
    public void testTempDirectory2() throws Exception {
        Path temp = Directories.createTempDirectory();
        assertNotNull(temp);
        Directories.recursiveDelete(temp);
    }

    @Test
    public void testRecursiveCopy1() throws Exception {
        Path temp = Directories.createTempDirectory();
        assertNotNull(temp);

        Path test = Paths.get(Resources.getResource("test/").toURI());
        Directories.recursiveCopy(test, temp);

        Map<String, String> hashes = Directories.recursiveHashes(temp, Hashing.sha1());
        assertNotNull(hashes);
        assertFalse(hashes.isEmpty());

        Directories.recursiveDelete(temp);
    }

    @Test
    public void testRecursiveZip1() throws Exception {
        Path temp1 = Directories.createTempDirectory();
        assertNotNull(temp1);

        Path temp2 = Directories.createTempDirectory();
        assertNotNull(temp2);

        Path test = Paths.get(Resources.getResource("test/").toURI());
        Directories.recursiveCopy(test, temp1);

        File zipFile = new File(temp2.toFile(), "temp.zip");
        ByteSink byteSink = com.google.common.io.Files.asByteSink(zipFile);

        try (OutputStream outputStream = byteSink.openStream()) {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
                Directories.recursiveZip(temp1, zipOutputStream);
            }
        }

        Directories.recursiveDelete(temp2);
        Directories.recursiveDelete(temp1);
    }
}
