/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.SeoKeywordGroup.Status;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.SeoKeywordsHelper;


public class SeoKeywordsHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:49 SeoKeywordsHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.PP.33344444";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getAllProductsAndSeoKeywordsTest1() throws SQLException {
        SeoKeywordsHelper helper = new SeoKeywordsHelper("");
        assertFalse(helper.getAllProductsAndSeoKeywords(logTag, connection).isEmpty());
    }

    @Test
    public void getgetSeoKeywordsTest1() throws SQLException {
        SeoKeywordsHelper helper = new SeoKeywordsHelper("");
        assertTrue(helper.getSeoKeywords(logTag, connection, prodInstId).isEmpty());
    }

    @Test
    public void getSeoKeywordGroupIdTest1() throws SQLException {
        SeoKeywordsHelper helper = new SeoKeywordsHelper("");
        assertNull(helper.getSeoKeywordGroupId(logTag, connection, prodInstId, null));
    }

    @Test
    public void getSEOKeywordGroupsTest1() throws SQLException {
        SeoKeywordsHelper helper = new SeoKeywordsHelper("");
        assertFalse(helper.getSEOKeywordGroups(connection, prodInstId, Collections.singleton(Status.ACTIVE)).isEmpty());
    }
}
