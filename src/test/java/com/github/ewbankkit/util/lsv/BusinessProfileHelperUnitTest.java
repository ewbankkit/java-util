/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.lsv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.ewbankkit.util.dbhelpers.BaseHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.lsv.BusinessProfile;
import com.netsol.adagent.util.lsv.BusinessProfileHelper;

public class BusinessProfileHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:10 BusinessProfileHelperUnitTest.java NSI";

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
    public void getBusinessProfileTest1() throws SQLException {
        BusinessProfileHelper helper = new BusinessProfileHelper("");
        assertNull(helper.getBusinessProfile(logTag, connection, null));
    }

    @Test
    public void getBusinessProfileTest2() throws SQLException {
        BusinessProfileHelper helper = new BusinessProfileHelper("");
        helper.deleteBusinessProfile(logTag, connection, prodInstId);
        BusinessProfile businessProfile = new BusinessProfile();
        businessProfile.setBrands("web.com");
        businessProfile.setProdInstId(prodInstId);
        helper.insertBusinessProfile(logTag, connection, businessProfile);
        businessProfile = helper.getBusinessProfile(logTag, connection, prodInstId);
        assertEquals("web.com", businessProfile.getBrands());
    }

    @Test
    public void getBusinessProfileTest3() throws SQLException {
        BusinessProfileHelper helper = new BusinessProfileHelper("");
        helper.deleteBusinessProfile(logTag, connection, prodInstId);
        BusinessProfile businessProfile = new BusinessProfile();
        businessProfile.setBrands("web.com");
        businessProfile.setProdInstId(prodInstId);
        helper.insertBusinessProfile(logTag, connection, businessProfile);
        businessProfile = helper.getBusinessProfile(logTag, connection, prodInstId);
        assertEquals("web.com", businessProfile.getBrands());
        assertNull(helper.getLastPublishDate(connection, prodInstId));
        businessProfile.setBrands("networksolutions.com");
        businessProfile.setPublish(true);
        helper.updateBusinessProfile(logTag, connection, businessProfile);
        businessProfile = helper.getBusinessProfile(logTag, connection, prodInstId);
        assertEquals("networksolutions.com", businessProfile.getBrands());
        assertNotNull(helper.getLastPublishDate(connection, prodInstId));
    }
}
