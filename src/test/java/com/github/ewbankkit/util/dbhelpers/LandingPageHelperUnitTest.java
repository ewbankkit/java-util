/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.dbhelpers.LandingPageHelper;

public class LandingPageHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:47 LandingPageHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.DEV.BING.0003";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getLandingPagesTest1() throws SQLException {
        LandingPageHelper helper = new LandingPageHelper("");
        assertTrue(helper.getLandingPages(logTag, connection, null).isEmpty());
    }

    @Test
    public void getLandingPagesTest2() throws SQLException {
        LandingPageHelper helper = new LandingPageHelper("");
        assertFalse(helper.getLandingPages(logTag, connection, prodInstId).isEmpty());
    }
}
