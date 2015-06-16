/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.netsol.adagent.util.IOUtil;

public class IOUtilUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:04 IOUtilUnitTest.java NSI";

    @Test
    public void readTextFileTest1() throws IOException {
        String string = IOUtil.readTextFile("C:\\TEMP\\test.txt");
        assertTrue(string.length() > 0);
    }

    @Test
    public void readBinaryFileTest1() throws IOException {
        byte[] bytes = IOUtil.readBinaryFile("C:\\TEMP\\test.txt");;
        assertTrue(bytes.length > 0);
        IOUtil.copyToStream(bytes, System.out);
    }
}
