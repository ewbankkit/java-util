/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.BusinessAddress;
import com.netsol.adagent.util.dbhelpers.BusinessAddressHelper;

public class BusinessAddressHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:45 BusinessAddressHelperUnitTest.java NSI";

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
    public void getBusinessAddressTest1() throws SQLException {
        BusinessAddressHelper businessAddressHelper = new BusinessAddressHelper("");
        assertNull(businessAddressHelper.getBusinessAddress(logTag, connection, null));
    }

    @Test
    public void getBusinessAddressTest2() throws SQLException {
        BusinessAddressHelper businessAddressHelper = new BusinessAddressHelper("");
        BusinessAddress businessAddress = new BusinessAddress();
        businessAddress.setBusinessName("Subway");
        businessAddress.setCity("Herndon");
        businessAddress.setProdInstId(prodInstId);
        businessAddress.setState("VA");
        businessAddress.setStreetAddress1("Coppermine Road");
        businessAddress.setZip("12345");
        businessAddressHelper.insertOrUpdateBusinessAddress(logTag, connection, businessAddress);
        businessAddress = businessAddressHelper.getBusinessAddress(logTag, connection, prodInstId);
        assertEquals("Subway", businessAddress.getBusinessName());
        assertEquals(prodInstId, businessAddress.getProdInstId());
        assertEquals("Coppermine Road", businessAddress.getStreetAddress1());
        assertEquals("12345", businessAddressHelper.getZipCode(logTag, connection, prodInstId));
    }
}
