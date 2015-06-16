/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.VendorAccount;
import com.github.ewbankkit.util.codes.ChannelId;
import com.github.ewbankkit.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.VendorAccountHelper;

public class VendorAccountHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:50 VendorAccountHelperUnitTest.java NSI";

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
    public void getVendorAccountByVendorAccountIdTest1() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertNull(vendorAccountHelper.getVendorAccountByVendorAccountId(logTag, connection, 0));
    }

    @Test
    public void getVendorAccountByVendorAccountIdTest2() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        VendorAccount vendorAccount = vendorAccountHelper.getVendorAccountByVendorAccountId(logTag, connection, 1);
        assertNotNull(vendorAccount);
        assertEquals(ChannelId.NETSOL, vendorAccount.getChannelId());
        assertEquals(VendorId.GOOGLE, vendorAccount.getVendorId());
    }

    @Test
    public void getVendorAccountIdsByChannelIdTest1() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertTrue(vendorAccountHelper.getVendorAccountIdsByChannelId(logTag, connection, 0).isEmpty());
    }

    @Test
    public void getVendorAccountIdsByChannelIdTest2() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        List<Integer> vendorAccountIds = vendorAccountHelper.getVendorAccountIdsByChannelId(logTag, connection, ChannelId.NETSOL);
        assertFalse(vendorAccountIds.isEmpty());
        assertTrue(vendorAccountIds.contains(Integer.valueOf(1)));
    }

    @Test
    public void getVendorAccountsByVendorIdTest1() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertTrue(vendorAccountHelper.getVendorAccountsByVendorId(logTag, connection, 0).isEmpty());
    }

    @Test
    public void getVendorAccountsByVendorIdTest2() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertFalse(vendorAccountHelper.getVendorAccountsByVendorId(logTag, connection, VendorId.GOOGLE).isEmpty());
    }

    @Test
    public void getVendorAccountsByVendorIdAndChannelIdTest1() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertTrue(vendorAccountHelper.getVendorAccountsByVendorIdAndChannelId(logTag, connection, 0, 0).isEmpty());
    }

    @Test
    public void getVendorAccountsByVendorIdAndChannelIdTest2() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertFalse(vendorAccountHelper.getVendorAccountsByVendorIdAndChannelId(logTag, connection, VendorId.GOOGLE, ChannelId.NETSOL).isEmpty());
    }

    @Test
    public void getVendorAccountByProdInstIdAndVendorIdTest1() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertNull(vendorAccountHelper.getVendorAccountByProdInstIdAndVendorId(logTag, connection, null, 0));
    }

    @Test
    public void getVendorAccountByProdInstIdAndVendorIdTest2() throws SQLException {
        VendorAccountHelper vendorAccountHelper = new VendorAccountHelper("");
        assertNotNull(vendorAccountHelper.getVendorAccountByProdInstIdAndVendorId(logTag, connection, prodInstId, VendorId.GOOGLE));
    }
}
