/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.InterceptorLeadPage;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.InterceptorLeadPageHelper;

public class InterceptorLeadPageHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:46 InterceptorLeadPageHelperUnitTest.java NSI";

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
    public void getPagesTest1() throws SQLException {
        InterceptorLeadPageHelper interceptorLeadPageHelper = new InterceptorLeadPageHelper("");
        assertTrue(interceptorLeadPageHelper.getFormPages(logTag, connection, null).isEmpty());
        assertTrue(interceptorLeadPageHelper.getHighValuePages(logTag, connection, null).isEmpty());
        assertTrue(interceptorLeadPageHelper.getShoppingCartPages(logTag, connection, null).isEmpty());
    }

    @Test
    public void getPagesTest2() throws SQLException {
        InterceptorLeadPageHelper interceptorLeadPageHelper = new InterceptorLeadPageHelper("");
        interceptorLeadPageHelper.deleteLeadPages(logTag, connection, prodInstId);
        assertTrue(interceptorLeadPageHelper.getFormPages(logTag, connection, prodInstId).isEmpty());
        assertTrue(interceptorLeadPageHelper.getHighValuePages(logTag, connection, prodInstId).isEmpty());
        assertTrue(interceptorLeadPageHelper.getShoppingCartPages(logTag, connection, prodInstId).isEmpty());
    }

    @Test
    public void getPagesTest3() throws SQLException {
        InterceptorLeadPageHelper interceptorLeadPageHelper = new InterceptorLeadPageHelper("");
        interceptorLeadPageHelper.deleteLeadPages(logTag, connection, prodInstId);
        InterceptorLeadPage interceptorLeadPage = new InterceptorLeadPage();
        interceptorLeadPage.setFormPage(true);
        interceptorLeadPage.setProdInstId(prodInstId);
        interceptorLeadPageHelper.insertLeadPage(logTag, connection, interceptorLeadPage);
        assertFalse(interceptorLeadPageHelper.getFormPages(logTag, connection, prodInstId).isEmpty());
        assertTrue(interceptorLeadPageHelper.getHighValuePages(logTag, connection, prodInstId).isEmpty());
        assertTrue(interceptorLeadPageHelper.getShoppingCartPages(logTag, connection, prodInstId).isEmpty());
    }

    @Test
    public void getPagesTest4() throws SQLException {
        InterceptorLeadPageHelper interceptorLeadPageHelper = new InterceptorLeadPageHelper("");
        interceptorLeadPageHelper.deleteLeadPages(logTag, connection, prodInstId);
        InterceptorLeadPage interceptorLeadPage = new InterceptorLeadPage();
        interceptorLeadPage.setHighValuePage(true);
        interceptorLeadPage.setProdInstId(prodInstId);
        interceptorLeadPageHelper.insertLeadPage(logTag, connection, interceptorLeadPage);
        assertTrue(interceptorLeadPageHelper.getFormPages(logTag, connection, prodInstId).isEmpty());
        assertFalse(interceptorLeadPageHelper.getHighValuePages(logTag, connection, prodInstId).isEmpty());
        assertTrue(interceptorLeadPageHelper.getShoppingCartPages(logTag, connection, prodInstId).isEmpty());
    }

    @Test
    public void getPagesTest5() throws SQLException {
        InterceptorLeadPageHelper interceptorLeadPageHelper = new InterceptorLeadPageHelper("");
        interceptorLeadPageHelper.deleteLeadPages(logTag, connection, prodInstId);
        InterceptorLeadPage interceptorLeadPage = new InterceptorLeadPage();
        interceptorLeadPage.setShoppingCartPage(true);
        interceptorLeadPage.setProdInstId(prodInstId);
        interceptorLeadPageHelper.insertLeadPage(logTag, connection, interceptorLeadPage);
        assertTrue(interceptorLeadPageHelper.getFormPages(logTag, connection, prodInstId).isEmpty());
        assertTrue(interceptorLeadPageHelper.getHighValuePages(logTag, connection, prodInstId).isEmpty());
        assertFalse(interceptorLeadPageHelper.getShoppingCartPages(logTag, connection, prodInstId).isEmpty());
    }
}
