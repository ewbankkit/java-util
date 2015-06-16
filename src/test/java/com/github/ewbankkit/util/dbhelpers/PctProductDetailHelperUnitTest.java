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

import com.netsol.adagent.util.beans.PctProductDetail;
import com.netsol.adagent.util.dbhelpers.PctProductDetailHelper;

public class PctProductDetailHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:48 PctProductDetailHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";
    private static final String updatedBySystem = "UNIT_TEST";
    private static final String updatedByUser = "UNIT_TEST";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getDetailTest1() throws SQLException {
        PctProductDetailHelper helper = new PctProductDetailHelper("");
        assertNull(helper.getPctProductDetail(logTag, connection, null));
    }

    @Test
    public void getDetailTest2() throws SQLException {
        PctProductDetailHelper helper = new PctProductDetailHelper("");
        PctProductDetail pctProductDetail = new PctProductDetail();
        pctProductDetail.setProdInstId(prodInstId);
        pctProductDetail.setUpdatedBySystem(updatedBySystem);
        pctProductDetail.setUpdatedByUser(updatedByUser);
        helper.insertOrUpdatePctProductDetail(logTag, connection, pctProductDetail);
        pctProductDetail = helper.getPctProductDetail(logTag, connection, prodInstId);
        assertEquals(prodInstId, pctProductDetail.getProdInstId());
    }
}
