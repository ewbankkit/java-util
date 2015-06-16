/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.VendorReport;
import com.github.ewbankkit.util.codes.VendorReportType;
import com.netsol.adagent.util.dbhelpers.VendorReportHelper;

public class VendorReportHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:51 VendorReportHelperUnitTest.java NSI";

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
    public void getVendorReportTest1() throws SQLException {
        VendorReportHelper vendorReportHelper = new VendorReportHelper("");
        assertNull(vendorReportHelper.getVendorReport(logTag, connection, 0L));
    }

    @Test
    public void getVendorReportTest2() throws SQLException {
        VendorReportHelper vendorReportHelper = new VendorReportHelper("");
        VendorReport vendorReport = new VendorReport();
        vendorReport.setProdInstIds(new String[] {});
        vendorReport.setReportEndDate(new Date());
        vendorReport.setReportStartDate(new Date());
        vendorReport.setReportTypeId(VendorReportType.AD_GROUP_SUMMARY);
        vendorReport.setVendorAccountId(1);
        vendorReport.setVendorId(1);
        Long nsReportId = vendorReportHelper.insertVendorReport(logTag, connection, vendorReport);
        assertNotNull(nsReportId);
        vendorReport = vendorReportHelper.getVendorReport(logTag, connection, nsReportId.longValue());
        assertNotNull(vendorReport);
        assertNull(vendorReport.getProdInstIds());
        assertEquals(1, vendorReport.getVendorId());
    }

    @Test
    public void getVendorReportTest3() throws SQLException {
        VendorReportHelper vendorReportHelper = new VendorReportHelper("");
        VendorReport vendorReport = new VendorReport();
        vendorReport.setProdInstIds(new String[] {"PROD.INST.ID.1", "PROD.INST.ID.2", "PROD.INST.ID.3"});
        vendorReport.setReportEndDate(new Date());
        vendorReport.setReportStartDate(new Date());
        vendorReport.setReportTypeId(VendorReportType.AD_GROUP_SUMMARY);
        vendorReport.setVendorAccountId(1);
        vendorReport.setVendorId(1);
        Long nsReportId = vendorReportHelper.insertVendorReport(logTag, connection, vendorReport);
        assertNotNull(nsReportId);

        vendorReport.setNsReportId(nsReportId.longValue());
        vendorReport.setVendorReportId(99L);
        vendorReportHelper.updateVendorReport(logTag, connection, vendorReport);
        vendorReport.clearTrackedUpdates();

        vendorReport = vendorReportHelper.getVendorReport(logTag, connection, nsReportId.longValue());
        assertEquals(99L, vendorReport.getVendorReportId());
        assertEquals(3, vendorReport.getProdInstIds().length);
    }

    @Test
    public void getVendorReportTest4() throws SQLException {
        VendorReportHelper vendorReportHelper = new VendorReportHelper("");
        VendorReport vendorReport = new VendorReport();
        vendorReport.setReportEndDate(new Date());
        vendorReport.setReportStartDate(new Date());
        vendorReport.setReportTypeId(VendorReportType.AD_GROUP_SUMMARY);
        vendorReport.setVendorAccountId(1);
        vendorReport.setVendorId(1);
        Long nsReportId = vendorReportHelper.insertVendorReport(logTag, connection, vendorReport);
        assertNotNull(nsReportId);

        vendorReport.setNsReportId(nsReportId.longValue());
        vendorReport.setDownloadEndDate(new Date());
        vendorReport.setFileSize(12345L);
        vendorReportHelper.updateVendorReport(logTag, connection, vendorReport);
        vendorReport.clearTrackedUpdates();

        vendorReport.setProcessingEndDate(new Date());
        vendorReportHelper.updateVendorReport(logTag, connection, vendorReport);
        vendorReport.clearTrackedUpdates();

        vendorReport = vendorReportHelper.getVendorReport(logTag, connection, nsReportId.longValue());
        assertEquals(12345L, vendorReport.getFileSize());
    }
}
