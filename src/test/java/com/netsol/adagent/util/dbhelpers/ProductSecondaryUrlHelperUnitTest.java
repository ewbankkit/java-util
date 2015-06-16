/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.ProductSecondaryUrlHelper;

public class ProductSecondaryUrlHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:49 ProductSecondaryUrlHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getSecondaryUrlsTest1() throws SQLException {
        ProductSecondaryUrlHelper productSecondaryUrlHelper = new ProductSecondaryUrlHelper("");
        assertTrue(productSecondaryUrlHelper.getSecondaryUrls(logTag, connection, null).isEmpty());
    }

    @Test
    public void getSecondaryUrlsTest2() throws SQLException {
        ProductSecondaryUrlHelper productSecondaryUrlHelper = new ProductSecondaryUrlHelper("");
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, connection, prodInstId);
        Collection<String> list = Collections.emptyList();
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, connection, prodInstId, list);
        assertTrue(productSecondaryUrlHelper.getSecondaryUrls(logTag, connection, prodInstId).isEmpty());
    }

    @Test
    public void getSecondaryUrlsTest3() throws SQLException {
        ProductSecondaryUrlHelper productSecondaryUrlHelper = new ProductSecondaryUrlHelper("");
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, connection, prodInstId);
        assertFalse(productSecondaryUrlHelper.hasSecondaryUrls(logTag, connection, prodInstId));
        Collection<String> list = Collections.singletonList("www.example.com");
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, connection, prodInstId, list);
        assertTrue(productSecondaryUrlHelper.hasSecondaryUrls(logTag, connection, prodInstId));
        List<String> urls = productSecondaryUrlHelper.getSecondaryUrls(logTag, connection, prodInstId);
        assertEquals(1, urls.size());
        assertEquals("www.example.com", urls.get(0));
    }

    @Test
    public void getSecondaryUrlsTest4() throws SQLException {
        ProductSecondaryUrlHelper productSecondaryUrlHelper = new ProductSecondaryUrlHelper("");
        productSecondaryUrlHelper.deleteSecondaryUrls(logTag, connection, prodInstId);
        Collection<String> list = Arrays.asList("www.example.com", "domain.net");
        productSecondaryUrlHelper.insertSecondaryUrls(logTag, connection, prodInstId, list);
        List<String> urls = productSecondaryUrlHelper.getSecondaryUrls(logTag, connection, prodInstId);
        assertEquals(2, urls.size());
        assertTrue(urls.contains("domain.net"));
    }
}
