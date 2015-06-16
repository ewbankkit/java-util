/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.ProductVendorAccountHelper;

public class ProductVendorAccountHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:49 ProductVendorAccountHelperUnitTest.java NSI";

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
    public void getSingleProdInstIdTest1() throws SQLException {
        ProductVendorAccountHelper productVendorAccountHelper = new ProductVendorAccountHelper("");
        assertNull(productVendorAccountHelper.getSingleProdInstId(logTag, connection, 0));
    }

    @Test(expected = SQLException.class)
    public void getSingleProdInstIdTest2() throws SQLException {
        ProductVendorAccountHelper productVendorAccountHelper = new ProductVendorAccountHelper("");
        productVendorAccountHelper.getSingleProdInstId(logTag, connection, 1);
    }
}
