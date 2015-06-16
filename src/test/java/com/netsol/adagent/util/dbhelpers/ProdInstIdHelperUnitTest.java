/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.ProdInstIdHelper;

public class ProdInstIdHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:48 ProdInstIdHelperUnitTest.java NSI";

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
    public void generateProdInstIdTest1() throws SQLException {
        ProdInstIdHelper helper = new ProdInstIdHelper("");
        assertNotNull(helper.generateProdInstId(logTag, connection, "UNIT.TEST."));
    }
}
