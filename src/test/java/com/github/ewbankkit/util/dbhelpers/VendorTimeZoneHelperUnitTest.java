/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.ewbankkit.util.codes.VendorId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.VendorTimeZone;
import com.netsol.adagent.util.dbhelpers.VendorTimeZoneHelper;

public class VendorTimeZoneHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:51 VendorTimeZoneHelperUnitTest.java NSI";

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
    public void getVendorTimeZoneTest1() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        assertNull(helper.getVendorTimeZoneByVendorTimeZoneId(logTag, connection, 0L));
    }

    @Test
    public void getVendorTimeZoneTest2() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        assertNull(helper.getVendorTimeZoneByVendorIdAndName(logTag, connection, 0, null));
    }

    @Test
    public void getVendorTimeZoneTest3() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        assertNull(helper.getVendorTimeZoneByVendorIdAndProdInstId(logTag, connection, 0, null));
    }

    @Test
    public void getVendorTimeZoneTest4() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        assertNotNull(helper.getVendorTimeZoneByVendorTimeZoneId(logTag, connection, 1L));
    }

    @Test
    public void getVendorTimeZoneTest5() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        VendorTimeZone vendorTimeZone = helper.getVendorTimeZoneByVendorIdAndName(logTag, connection, VendorId.GOOGLE, "America/New_York");
        assertNotNull(vendorTimeZone);
        assertEquals(VendorId.GOOGLE, vendorTimeZone.getVendorId());
        assertEquals("America/New_York", vendorTimeZone.getName());
    }

    @Test
    public void getVendorTimeZoneTest6() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        assertNotNull(helper.getVendorTimeZoneByVendorIdAndProdInstId(logTag, connection, VendorId.GOOGLE, "WN.PP.33344444"));
    }

    @Test
    public void getVendorTimeZoneTest7() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        assertNotNull(helper.getSingleVendorTimeZoneByVendorId(logTag, connection, VendorId.TELMETRICS));
    }

    @Test(expected = SQLException.class)
    public void getVendorTimeZoneTest8() throws SQLException {
        VendorTimeZoneHelper helper = new VendorTimeZoneHelper("");
        assertNotNull(helper.getSingleVendorTimeZoneByVendorId(logTag, connection, VendorId.MICROSOFT));
    }
}
