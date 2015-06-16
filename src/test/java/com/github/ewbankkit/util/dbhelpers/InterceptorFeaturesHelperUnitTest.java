/**
 * Copyright (C) Network Solutions, LLC.
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

import com.netsol.adagent.util.beans.InterceptorFeatures;
import com.netsol.adagent.util.dbhelpers.InterceptorFeaturesHelper;

public class InterceptorFeaturesHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:46 InterceptorFeaturesHelperUnitTest.java NSI";

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
    public void getFeaturesTest1() throws SQLException {
        InterceptorFeaturesHelper interceptorFeaturesHelper = new InterceptorFeaturesHelper("");
        assertNull(interceptorFeaturesHelper.getFeatures(logTag, connection, null));
    }

    @Test
    public void getFeaturesTest2() throws SQLException {
        InterceptorFeaturesHelper interceptorFeaturesHelper = new InterceptorFeaturesHelper("");
        interceptorFeaturesHelper.deleteFeatures(logTag, connection, prodInstId);
        InterceptorFeatures interceptorFeatures = new InterceptorFeatures();
        interceptorFeatures.setAllowEmptyReferrer(true);
        interceptorFeatures.setPerformReplacements(true);
        interceptorFeatures.setProdInstId(prodInstId);
        interceptorFeatures.setPropagateAdParams(true);
        interceptorFeatures.setRewriteReferrer(true);
        interceptorFeatures.setTrackEmail(true);
        interceptorFeatures.setTrackForm(true);
        interceptorFeatures.setTrackHighValuePage(true);
        interceptorFeatures.setTrackShoppingCart(true);
        interceptorFeaturesHelper.insertFeatures(logTag, connection, interceptorFeatures);
        interceptorFeatures = interceptorFeaturesHelper.getFeatures(logTag, connection, prodInstId);
        assertTrue(interceptorFeatures.isPerformReplacements());
        assertEquals(prodInstId, interceptorFeatures.getProdInstId());
        assertTrue(interceptorFeatures.isAllowEmptyReferrer());
        assertTrue(interceptorFeatures.isPropagateAdParams());
        assertTrue(interceptorFeatures.isRewriteReferrer());
        assertTrue(interceptorFeatures.isTrackEmail());
        assertTrue(interceptorFeatures.isTrackForm());
        assertTrue(interceptorFeatures.isTrackHighValuePage());
        assertTrue(interceptorFeatures.isTrackShoppingCart());
    }

    @Test
    public void updateFeaturesTest1() throws SQLException {
        InterceptorFeaturesHelper interceptorFeaturesHelper = new InterceptorFeaturesHelper("");
        interceptorFeaturesHelper.deleteFeatures(logTag, connection, prodInstId);
        InterceptorFeatures interceptorFeatures = new InterceptorFeatures();
        interceptorFeatures.setAllowEmptyReferrer(true);
        interceptorFeatures.setPerformReplacements(true);
        interceptorFeatures.setProdInstId(prodInstId);
        interceptorFeatures.setPropagateAdParams(true);
        interceptorFeatures.setRewriteReferrer(true);
        interceptorFeatures.setTrackEmail(true);
        interceptorFeatures.setTrackForm(true);
        interceptorFeatures.setTrackHighValuePage(true);
        interceptorFeatures.setTrackShoppingCart(true);
        interceptorFeaturesHelper.insertFeatures(logTag, connection, interceptorFeatures);
        interceptorFeatures.setAllowEmptyReferrer(false);
        interceptorFeatures.setPerformReplacements(false);
        interceptorFeatures.setPropagateAdParams(false);
        interceptorFeatures.setTrackEmail(false);
        interceptorFeatures.setTrackHighValuePage(false);
        interceptorFeaturesHelper.updateFeatures(logTag, connection, interceptorFeatures);
        interceptorFeatures = interceptorFeaturesHelper.getFeatures(logTag, connection, prodInstId);
        assertFalse(interceptorFeatures.isAllowEmptyReferrer());
        assertFalse(interceptorFeatures.isPerformReplacements());
        assertEquals(prodInstId, interceptorFeatures.getProdInstId());
        assertFalse(interceptorFeatures.isPropagateAdParams());
        assertTrue(interceptorFeatures.isRewriteReferrer());
        assertFalse(interceptorFeatures.isTrackEmail());
        assertTrue(interceptorFeatures.isTrackForm());
        assertFalse(interceptorFeatures.isTrackHighValuePage());
        assertTrue(interceptorFeatures.isTrackShoppingCart());
    }
}
