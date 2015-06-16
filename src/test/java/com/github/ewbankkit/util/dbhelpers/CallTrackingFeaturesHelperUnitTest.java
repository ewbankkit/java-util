/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.CallTrackingFeatures;
import com.netsol.adagent.util.dbhelpers.CallTrackingFeaturesHelper;

public class CallTrackingFeaturesHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:45 CallTrackingFeaturesHelperUnitTest.java NSI";

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
    public void getFeaturesTest1() throws SQLException {
        CallTrackingFeaturesHelper callTrackingFeaturesHelper = new CallTrackingFeaturesHelper("");
        assertNull(callTrackingFeaturesHelper.getCallTrackingFeatures(logTag, connection, null));
    }

    @Test
    public void getFeaturesTest2() throws SQLException {
        CallTrackingFeaturesHelper callTrackingFeaturesHelper = new CallTrackingFeaturesHelper("");
        CallTrackingFeatures callTrackingFeatures = new CallTrackingFeatures();
        callTrackingFeatures.setCallLeadMinDuration(10);
        callTrackingFeatures.setProdInstId(prodInstId);
        callTrackingFeatures.setTrackUnansweredCalls(false);
        callTrackingFeaturesHelper.insertOrUpdateCallTrackingFeatures(logTag, connection, callTrackingFeatures);
        callTrackingFeatures = callTrackingFeaturesHelper.getCallTrackingFeatures(logTag, connection, prodInstId);
        assertEquals(10, callTrackingFeatures.getCallLeadMinDuration());
        assertEquals(prodInstId, callTrackingFeatures.getProdInstId());
        assertFalse(callTrackingFeatures.isTrackUnansweredCalls());
    }
}
