/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.VendorAccountConfig;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.VendorAccountConfigHelper;

public class VendorAccountConfigHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:50 VendorAccountConfigHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getVendorAccountConfigByVendorAccountIdTest1() throws SQLException {
        VendorAccountConfigHelper vendorAccountConfigHelper = new VendorAccountConfigHelper("");
        assertNull(vendorAccountConfigHelper.getVendorAccountConfigByVendorAccountId(logTag, connection, 0));
    }

    @Test
    public void getVendorAccountConfigByVendorAccountIdTest2() throws SQLException {
        VendorAccountConfigHelper vendorAccountConfigHelper = new VendorAccountConfigHelper("");
        VendorAccountConfig vendorAccountConfig = vendorAccountConfigHelper.getVendorAccountConfigByVendorAccountId(logTag, connection, 1);
        assertNotNull(vendorAccountConfig);
        assertEquals(VendorId.GOOGLE, vendorAccountConfig.getVendorId());
        assertNotNull(vendorAccountConfig.getProperties());
        assertNotNull(vendorAccountConfig.getProperties().get("password"));
    }

    @Test
    public void getAllVendorAccountConfigsTest1() throws SQLException {
        VendorAccountConfigHelper vendorAccountConfigHelper = new VendorAccountConfigHelper("");
        assertTrue(vendorAccountConfigHelper.getAllVendorAccountConfigs(logTag, connection, null).isEmpty());
    }

    @Test
    public void getAllVendorAccountConfigsTest2() throws SQLException {
        VendorAccountConfigHelper vendorAccountConfigHelper = new VendorAccountConfigHelper("");
        Map<Integer, VendorAccountConfig> allVendorAccountConfigs = vendorAccountConfigHelper.getAllVendorAccountConfigs(logTag, connection, prodInstId);
        assertFalse(allVendorAccountConfigs.isEmpty());
    }

    @Test
    public void getVendorAccountConfigByProdInstIdAndVendorIdTest1() throws SQLException {
        VendorAccountConfigHelper vendorAccountConfigHelper = new VendorAccountConfigHelper("");
        assertNull(vendorAccountConfigHelper.getVendorAccountConfigByProdInstIdAndVendorId(logTag, connection, null, VendorId.GOOGLE));
    }

    @Test
    public void getVendorAccountConfigByProdInstIdAndVendorIdTest2() throws SQLException {
        VendorAccountConfigHelper vendorAccountConfigHelper = new VendorAccountConfigHelper("");
        VendorAccountConfig vendorAccountConfig = vendorAccountConfigHelper.getVendorAccountConfigByProdInstIdAndVendorId(logTag, connection, prodInstId, VendorId.GOOGLE);
        assertNotNull(vendorAccountConfig);
        assertEquals(1, vendorAccountConfig.getVendorAccountId());
        assertNotNull(vendorAccountConfig.getProperties());
        assertNotNull(vendorAccountConfig.getProperties().get("password"));
    }
}
