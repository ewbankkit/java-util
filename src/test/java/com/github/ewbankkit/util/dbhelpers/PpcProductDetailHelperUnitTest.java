/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.PpcProductDetail;
import com.netsol.adagent.util.dbhelpers.PpcProductDetailHelper;

public class PpcProductDetailHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:48 PpcProductDetailHelperUnitTest.java NSI";

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
        PpcProductDetailHelper helper = new PpcProductDetailHelper("");
        assertNull(helper.getPpcProductDetail(logTag, connection, null));
    }

    @Test
    public void getDetailTest2() throws SQLException {
        PpcProductDetailHelper helper = new PpcProductDetailHelper("");
        PpcProductDetail ppcProductDetail = new PpcProductDetail();
        ppcProductDetail.setClickThreshold(Long.valueOf(2L));
        ppcProductDetail.setDebitCpcMarkup(true);
        ppcProductDetail.setDebitPhoneLeadCost(true);
        ppcProductDetail.setOptimizeAdGroups(true);
        ppcProductDetail.setOptimizeBudget(true);
        ppcProductDetail.setProdInstId(prodInstId);
        ppcProductDetail.setSubscriptionFee(Double.valueOf(42.24D));
        ppcProductDetail.setUpdatedBySystem(updatedBySystem);
        ppcProductDetail.setUpdatedByUser(updatedByUser);
        helper.insertOrUpdatePpcProductDetail(logTag, connection, ppcProductDetail);
        ppcProductDetail = helper.getPpcProductDetail(logTag, connection, prodInstId);
        assertEquals(Long.valueOf(2L), ppcProductDetail.getClickThreshold());
        assertTrue(ppcProductDetail.isDebitCpcMarkup());
        assertTrue(ppcProductDetail.isOptimizeAdGroups());
        assertFalse(ppcProductDetail.isOptimizeAds());
        assertTrue(ppcProductDetail.isOptimizeBudget());
        assertEquals(prodInstId, ppcProductDetail.getProdInstId());
        assertEquals(42.24D, ppcProductDetail.getSubscriptionFee().doubleValue(), 0D);
        ppcProductDetail.setClickThreshold(null);
        ppcProductDetail.setOptimizeAdGroups(false);
        ppcProductDetail.setSubscriptionFee(Double.valueOf(51.15D));
        ppcProductDetail.setUpdatedBySystem(updatedBySystem);
        ppcProductDetail.setUpdatedByUser(updatedByUser);
        helper.insertOrUpdatePpcProductDetail(logTag, connection, ppcProductDetail);
        ppcProductDetail = helper.getPpcProductDetail(logTag, connection, prodInstId);
        assertNull(ppcProductDetail.getClickThreshold());
        assertTrue(ppcProductDetail.isDebitCpcMarkup());
        assertFalse(ppcProductDetail.isOptimizeAdGroups());
        assertFalse(ppcProductDetail.isOptimizeAds());
        assertTrue(ppcProductDetail.isOptimizeBudget());
        assertEquals(prodInstId, ppcProductDetail.getProdInstId());
        assertEquals(51.15D, ppcProductDetail.getSubscriptionFee().doubleValue(), 0D);
    }


}
