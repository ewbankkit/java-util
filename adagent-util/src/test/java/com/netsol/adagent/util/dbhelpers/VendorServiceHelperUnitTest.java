/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.codes.VendorType;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.VendorServiceHelper;

public class VendorServiceHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:51 VendorServiceHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getVendorIdTest1() throws SQLException {
        VendorServiceHelper vendorServiceHelper = new VendorServiceHelper("");
        assertNull(vendorServiceHelper.getVendorId(logTag, connection, null));
    }

    @Test
    public void getVendorIdTest2() throws SQLException {
        VendorServiceHelper vendorServiceHelper = new VendorServiceHelper("");
        assertEquals(VendorId.GOOGLE, vendorServiceHelper.getVendorId(logTag, connection, "Google").intValue());
    }

    @Test
    public void getVendorIdsTest1() throws SQLException {
        VendorServiceHelper vendorServiceHelper = new VendorServiceHelper("");
        assertTrue(vendorServiceHelper.getVendorIds(logTag, connection, -1).isEmpty());
    }

    @Test
    public void getVendorIdsTest2() throws SQLException {
        VendorServiceHelper vendorServiceHelper = new VendorServiceHelper("");
        assertFalse(vendorServiceHelper.getVendorIds(logTag, connection, VendorType.AD).isEmpty());
    }

    @Test
    public void isAdVendorTest1() throws SQLException {
        VendorServiceHelper vendorServiceHelper = new VendorServiceHelper("");
        assertTrue(vendorServiceHelper.isAdVendor(logTag, connection, VendorId.GOOGLE));
    }

    @Test
    public void isAdVendorTest2() throws SQLException {
        VendorServiceHelper vendorServiceHelper = new VendorServiceHelper("");
        assertFalse(vendorServiceHelper.isAdVendor(logTag, connection, VendorId.TELMETRICS));
    }

    @Test
    public void isAdVendorTest3() throws SQLException {
        VendorServiceHelper vendorServiceHelper = new VendorServiceHelper("");
        assertFalse(vendorServiceHelper.isAdVendor(logTag, connection, VendorId.YAHOO));
    }
}
