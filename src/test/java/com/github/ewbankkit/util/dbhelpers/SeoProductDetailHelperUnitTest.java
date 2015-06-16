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

import com.netsol.adagent.util.beans.SeoProductDetail;
import com.netsol.adagent.util.dbhelpers.SeoProductDetailHelper;

public class SeoProductDetailHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:49 SeoProductDetailHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "TEST.000001";
    private static final String updatedBySystem = "UNIT_TEST";
    private static final String updatedByUser = "UNIT_TEST";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getDetailTest1() throws SQLException {
        SeoProductDetailHelper helper = new SeoProductDetailHelper("");
        assertNull(helper.getSeoProductDetail(logTag, connection, null));
    }

    @Test
    public void getDetailTest2() throws SQLException {
        SeoProductDetailHelper helper = new SeoProductDetailHelper("");
        SeoProductDetail seoProductDetail = new SeoProductDetail();
        seoProductDetail.setAccountManagerUserName("seoAccountMgr");
        seoProductDetail.setCopywriterUserName("seoCopywriter");
        seoProductDetail.setQaCopywriterUserName("seoCopywriter2");
        seoProductDetail.setProdInstId(prodInstId);
        seoProductDetail.setUpdatedBySystem(updatedBySystem);
        seoProductDetail.setUpdatedByUser(updatedByUser);
        helper.insertOrUpdateSeoProductDetail(logTag, connection, seoProductDetail);
        seoProductDetail = helper.getSeoProductDetail(logTag, connection, prodInstId);
        assertEquals("seoAccountMgr", seoProductDetail.getAccountManagerUserName());
        assertEquals("seoCopywriter", seoProductDetail.getCopywriterUserName());
        assertNull(seoProductDetail.getJsCheckDate());
        assertNull(seoProductDetail.getJsCheckResult());
        assertEquals("seoCopywriter2", seoProductDetail.getQaCopywriterUserName());
        assertEquals(prodInstId, seoProductDetail.getProdInstId());

        seoProductDetail.setAccountManagerUserName(null);
        seoProductDetail.setCopywriterUserName("seoCopywriter2");
        seoProductDetail.setQaCopywriterUserName("seoCopywriter");
        seoProductDetail.setJsCheckDate(new Date());
        seoProductDetail.setJsCheckResult(Boolean.FALSE);
        seoProductDetail.setProdInstId(prodInstId);
        seoProductDetail.setUpdatedBySystem(updatedBySystem);
        seoProductDetail.setUpdatedByUser(updatedByUser);
        helper.insertOrUpdateSeoProductDetail(logTag, connection, seoProductDetail);
        seoProductDetail = helper.getSeoProductDetail(logTag, connection, prodInstId);
        assertNull(seoProductDetail.getAccountManagerUserName());
        assertEquals("seoCopywriter2", seoProductDetail.getCopywriterUserName());
        assertNotNull(seoProductDetail.getJsCheckDate());
        assertNotNull(seoProductDetail.getJsCheckResult());
        assertEquals("seoCopywriter", seoProductDetail.getQaCopywriterUserName());
        assertEquals(prodInstId, seoProductDetail.getProdInstId());
    }
}
