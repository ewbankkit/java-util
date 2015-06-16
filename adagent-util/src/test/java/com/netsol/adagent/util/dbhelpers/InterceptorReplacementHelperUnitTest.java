/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.InterceptorReplacement;
import com.netsol.adagent.util.codes.LimitType;
import com.netsol.adagent.util.codes.ReplacementType;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorReplacementHelper;

public class InterceptorReplacementHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:47 InterceptorReplacementHelperUnitTest.java NSI";

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
    public void getLimitIdTest1() throws SQLException {
        InterceptorReplacementHelper interceptorReplacementHelper = new InterceptorReplacementHelper("");
        InterceptorReplacement interceptorReplacement = new InterceptorReplacement();
        assertNull(interceptorReplacementHelper.getLimitId(logTag, connection, interceptorReplacement));
    }

    @Test
    public void getLimitIdTest2() throws SQLException {
        InterceptorReplacementHelper interceptorReplacementHelper = new InterceptorReplacementHelper("");
        interceptorReplacementHelper.deleteReplacementsByProdInstId(logTag, connection, prodInstId);
        InterceptorReplacement interceptorReplacement = new InterceptorReplacement();
        interceptorReplacement.setLimitId(42L);
        interceptorReplacement.setLimitType(LimitType.CAMPAIGN);
        interceptorReplacement.setOriginalRegex("O");
        interceptorReplacement.setOriginalText("O");
        interceptorReplacement.setProdInstId(prodInstId);
        interceptorReplacement.setReplacementText("R");
        interceptorReplacement.setReplacementType(ReplacementType.CUSTOM);
        interceptorReplacement.setVendorEntityId(Long.valueOf(0L));
        interceptorReplacement.setVendorId(VendorId.GOOGLE);
        interceptorReplacementHelper.insertReplacement(logTag, connection, interceptorReplacement);
        interceptorReplacement = new InterceptorReplacement();
        interceptorReplacement.setLimitType(LimitType.CAMPAIGN);
        interceptorReplacement.setOriginalRegex("O");
        interceptorReplacement.setOriginalText("O");
        interceptorReplacement.setProdInstId(prodInstId);
        interceptorReplacement.setReplacementText("R");
        interceptorReplacement.setReplacementType(ReplacementType.CUSTOM);
        interceptorReplacement.setVendorId(VendorId.GOOGLE);
        assertEquals(42L, interceptorReplacementHelper.getLimitId(logTag, connection, interceptorReplacement).longValue());
    }
}
