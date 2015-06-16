/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ewbankkit.util.codes.FeatureId;
import com.github.ewbankkit.util.codes.ProductFeatureStatus;
import com.netsol.adagent.util.dbhelpers.ProductFeatureHelper;

public class ProductFeatureHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:48 ProductFeatureHelperUnitTest.java NSI";

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
    public void getProductFeaturesTest1() throws SQLException {
        ProductFeatureHelper helper = new ProductFeatureHelper("");
        assertTrue(helper.getProductFeaturesExcludingStatuses(logTag, connection, null).isEmpty());
    }

    @Test
    public void getProductFeaturesTest2() throws SQLException {
        ProductFeatureHelper helper = new ProductFeatureHelper("");
        assertFalse(helper.getProductFeaturesExcludingStatuses(logTag, connection, prodInstId).isEmpty());
    }

    @Test
    public void getProductFeaturesTest3() throws SQLException {
        ProductFeatureHelper helper = new ProductFeatureHelper("");
        assertTrue(helper.getProductFeaturesExcludingStatuses(logTag, connection, prodInstId, ProductFeatureStatus.ACTIVE).isEmpty());
    }

    @Test
    public void getFeatureIdsTest1() throws SQLException {
        ProductFeatureHelper helper = new ProductFeatureHelper("");
        assertEquals(0, helper.getFeatureIdsExcludingStatuses(logTag, connection, null).length);
    }

    @Test
    public void getFeatureIdsTest2() throws SQLException {
        ProductFeatureHelper helper = new ProductFeatureHelper("");
        int[] featureIds = helper.getFeatureIdsExcludingStatuses(logTag, connection, prodInstId);
        assertFalse(featureIds.length == 0);
        assertTrue(FeatureId.featureIsEnabled(featureIds, FeatureId.PPC));
        assertFalse(FeatureId.featureIsEnabled(featureIds, FeatureId.PCT));
    }
}
