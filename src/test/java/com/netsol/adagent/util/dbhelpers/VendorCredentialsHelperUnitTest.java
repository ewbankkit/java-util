/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.DateUtil;
import com.netsol.adagent.util.beans.VendorCredentials;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.VendorCredentialsHelper;

public class VendorCredentialsHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:50 VendorCredentialsHelperUnitTest.java NSI";

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
    public void getAdVendorIdsTest1() throws SQLException {
        VendorCredentialsHelper vendorCredentialsHelper = new VendorCredentialsHelper("");
        assertTrue(vendorCredentialsHelper.getAdVendorIds(logTag, connection, null).isEmpty());
    }

    @Test
    public void getAdVendorIdsTest2() throws SQLException {
        VendorCredentialsHelper vendorCredentialsHelper = new VendorCredentialsHelper("");
        assertEquals(3, vendorCredentialsHelper.getAdVendorIds(logTag, connection, "WN.DEV.BING.0003").size());
    }

    @Test
    public void getVendorCredentialsTest1() throws Exception {
        String prodInstId = "RYAN.12345";
        VendorCredentialsHelper vendorCredentialsHelper = new VendorCredentialsHelper("");
        for (Integer vendorId : vendorCredentialsHelper.getAdVendorIds(logTag, connection, prodInstId)) {
            vendorCredentialsHelper.deleteVendorCredentials(prodInstId, connection, prodInstId, vendorId.intValue());
        }
        VendorCredentials vendorCredentials = new VendorCredentials();
        vendorCredentials.setEndDate(DateUtil.stringToDate("2012-07-26"));
        vendorCredentials.setProdInstId(prodInstId);
        vendorCredentials.setVendorAccountId1("TEST ID1");
        vendorCredentials.setVendorAccountId2("TEST ID2");
        vendorCredentials.setVendorAccountName("TEST NAME");
        vendorCredentials.setVendorAccountPassword("TEST PASSWORD");
        vendorCredentials.setVendorAccountUserName("TEST USERNAME");
        vendorCredentials.setVendorId(VendorId.GOOGLE);
        vendorCredentials.setVendorTimeZoneId(Long.valueOf(1L));
        vendorCredentialsHelper.insertVendorCredentials(prodInstId, connection, vendorCredentials);
        vendorCredentials = vendorCredentialsHelper.getVendorCredentials(prodInstId, connection, prodInstId, VendorId.GOOGLE);
        assertThat(DateUtil.dateToString(vendorCredentials.getEndDate()), is("2012-07-26"));
        assertEquals("TEST ID1", vendorCredentials.getVendorAccountId1());
        assertEquals("TEST ID2", vendorCredentials.getVendorAccountId2());
        assertEquals("TEST NAME", vendorCredentials.getVendorAccountName());
        assertEquals("TEST PASSWORD", vendorCredentials.getVendorAccountPassword());
        assertEquals("TEST USERNAME", vendorCredentials.getVendorAccountUserName());
        assertEquals(1L, vendorCredentials.getVendorTimeZoneId().longValue());
    }

    @Test
    public void getVendorCredentialsTest2() throws SQLException {
        String prodInstId = "RYAN.12345";
        VendorCredentialsHelper vendorCredentialsHelper = new VendorCredentialsHelper("");
        for (Integer vendorId : vendorCredentialsHelper.getAdVendorIds(logTag, connection, prodInstId)) {
            vendorCredentialsHelper.deleteVendorCredentials(prodInstId, connection, prodInstId, vendorId.intValue());
        }
        VendorCredentials vendorCredentials = new VendorCredentials();
        vendorCredentials.setProdInstId(prodInstId);
        vendorCredentials.setVendorAccountId1("TEST USER NAME");
        vendorCredentials.setVendorAccountId2("TEST USER NAME 2");
        vendorCredentials.setVendorId(VendorId.GOOGLE);
        vendorCredentialsHelper.insertVendorCredentials(prodInstId, connection, vendorCredentials);
        assertFalse(vendorCredentialsHelper.getAllVendorCredentials(prodInstId, connection, prodInstId).isEmpty());
    }
}
