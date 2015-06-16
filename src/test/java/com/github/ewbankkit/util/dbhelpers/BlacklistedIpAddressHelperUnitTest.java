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

import com.netsol.adagent.util.dbhelpers.BlacklistedIpAddressHelper;

public class BlacklistedIpAddressHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:45 BlacklistedIpAddressHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void isBlacklistedIpAddressTest1() throws SQLException {
        BlacklistedIpAddressHelper blacklistedIpAddressHelper = new BlacklistedIpAddressHelper("");
        assertFalse(blacklistedIpAddressHelper.isBlacklistedIpAddress(logTag, connection, null));
    }

    @Test
    public void isBlacklistedIpAddressTest2() throws SQLException {
        BlacklistedIpAddressHelper blacklistedIpAddressHelper = new BlacklistedIpAddressHelper("");
        assertFalse(blacklistedIpAddressHelper.isBlacklistedIpAddress(logTag, connection, "127.0.0.1"));
    }

    @Test
    public void isBlacklistedIpAddressTest3() throws SQLException {
        BlacklistedIpAddressHelper blacklistedIpAddressHelper = new BlacklistedIpAddressHelper("");
        assertTrue(blacklistedIpAddressHelper.isBlacklistedIpAddress(logTag, connection, "204.236.213.154"));
    }
}
