/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.ewbankkit.util.codes.AdStatus;
import com.github.ewbankkit.util.codes.CampaignStatus;
import com.github.ewbankkit.util.codes.KeywordMatchType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.NsAd;
import com.netsol.adagent.util.beans.NsAdGroup;
import com.netsol.adagent.util.beans.NsBusinessLocation;
import com.netsol.adagent.util.beans.NsCampaign;
import com.netsol.adagent.util.beans.NsCampaignAdCallExtension;
import com.netsol.adagent.util.beans.NsCampaignAdLocationExtension;
import com.netsol.adagent.util.beans.NsCampaignAdSchedule;
import com.netsol.adagent.util.beans.NsCampaignAdSitelinksExtension;
import com.netsol.adagent.util.beans.NsCampaignAdSitelinksExtension.Sitelink;
import com.netsol.adagent.util.beans.NsCampaignNegativeKeyword;
import com.netsol.adagent.util.beans.NsCampaignProximityTarget;
import com.netsol.adagent.util.beans.NsKeyword;
import com.netsol.adagent.util.beans.NsNegativeKeyword;
import com.github.ewbankkit.util.codes.AdGroupStatus;
import com.github.ewbankkit.util.codes.AdType;
import com.github.ewbankkit.util.codes.BusinessLocationStatus;
import com.github.ewbankkit.util.codes.CampaignAdExtensionStatus;
import com.github.ewbankkit.util.codes.NegativeKeywordStatus;
import com.github.ewbankkit.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.NsEntityHelper;

public class NsEntityHelperUnitTest {
    private static Connection connection;
    private static final String logTag = null;
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
    public void isValidAdGroupIdAndVendorIdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0001";
        assertTrue(nsEntityHelper.isValidAdGroupIdAndVendorId(logTag, connection, prodInstId, 7776L, VendorId.MICROSOFT));
    }

    @Test
    public void isValidAdGroupIdAndVendorIdTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0001";
        assertFalse(nsEntityHelper.isValidAdGroupIdAndVendorId(logTag, connection, prodInstId, 7776L, VendorId.GOOGLE));
    }

    @Test
    public void getNsAdCampaignsIncludingStatusesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsAdCampaignsIncludingStatuses(logTag, connection, null, CampaignStatus.ACTIVE).isEmpty());
    }

    @Test
    public void getNsAdCampaignsIncludingStatusesTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        assertTrue(nsEntityHelper.getNsAdCampaignsIncludingStatuses(logTag, connection, prodInstId, CampaignStatus.ACTIVE).isEmpty());
    }

    @Test
    public void getNsAdCampaignsExcludingStatusesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsAdCampaignsExcludingStatuses(logTag, connection, null, CampaignStatus.DEACTIVATED, CampaignStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsAdCampaignsExcludingStatusesTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0003";
        assertFalse(nsEntityHelper.getNsAdCampaignsExcludingStatuses(logTag, connection, prodInstId, CampaignStatus.DEACTIVATED, CampaignStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsAdCampaignsForTargetExcludingStatusesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsAdCampaignsForTargetExcludingStatuses(logTag, connection, null, null, CampaignStatus.DEACTIVATED, CampaignStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsAdCampaignsForTargetExcludingStatusesTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        assertTrue(nsEntityHelper.getNsAdCampaignsForTargetExcludingStatuses(logTag, connection, prodInstId, 0L, CampaignStatus.DEACTIVATED, CampaignStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsAdGroupIdToNsKeywordMapTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsAdGroupIdToNsKeywordMap(logTag, connection, null).isEmpty());
    }

    @Test
    public void getNsAdGroupIdToNsKeywordMapTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0003";
        assertTrue(nsEntityHelper.getNsAdGroupIdToNsKeywordMap(logTag, connection, prodInstId, "melonhaus-de.netsolads.com").isEmpty());
    }

    @Test
    public void getNsAdGroupIdToNsSuperPagesCampaignMapTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsAdGroupIdToNsSuperPagesCampaignMap(logTag, connection, null).isEmpty());
    }

    @Test
    public void getNsAdGroupIdToNsSuperPagesCampaignMapTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0003";
        assertFalse(nsEntityHelper.getNsAdGroupIdToNsSuperPagesCampaignMap(logTag, connection, prodInstId, "www.web.com").isEmpty());
    }

    @Test
    public void getNsAdGroupIdToNsAdMapTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsAdGroupIdToNsAdMap(logTag, connection, null, null).isEmpty());
    }

    @Test
    public void getNsAdGroupIdToNsAdMapTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0003";
        assertTrue(nsEntityHelper.getNsAdGroupIdToNsAdMap(logTag, connection, prodInstId, AdType.TEXT_AD, "melonhaus-de.netsolads.com").isEmpty());
    }

    @Test
    public void getNsAdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsAd(logTag, connection, null, 0L));
    }

    @Test
    public void getNsAdTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsAd(logTag, connection, null, 0, 0L, 0L));
    }

    @Test
    public void getNsAdTest3() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorAdId = 162346584L;
        long nsAdId = 18891L;
        assertEquals(Long.valueOf(vendorAdId), nsEntityHelper.getNsAd(logTag, connection, prodInstId, nsAdId).getVendorAdId());
    }

    @Test
    public void getNsAdTest4() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorAdId = 162346584L;
        long nsAdGroupId = 7883L;
        long nsAdId = 18891L;
        assertEquals(nsAdId, nsEntityHelper.getNsAd(logTag, connection, prodInstId, VendorId.MICROSOFT, vendorAdId, nsAdGroupId).getNsAdId());
    }

    @Test
    public void getNsAdGroupTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsAdGroup(logTag, connection, null, 0L));
    }

    @Test
    public void getNsAdGroupTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsAdGroup(logTag, connection, null, 0, 0L, 0L));
    }

    @Test
    public void getNsAdGroupTest3() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorAdGroupId = 257914076;
        long nsAdGroupId = 100000100L;
        assertEquals(Long.valueOf(vendorAdGroupId), nsEntityHelper.getNsAdGroup(logTag, connection, prodInstId, nsAdGroupId).getVendorAdGroupId());
    }

    @Test
    public void getNsAdGroupTest4() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorAdGroupId = 257914076;
        long nsCampaignId = 4718L;
        long nsAdGroupId = 100000100L;
        assertEquals(nsAdGroupId, nsEntityHelper.getNsAdGroup(logTag, connection, prodInstId, VendorId.MICROSOFT, vendorAdGroupId, nsCampaignId).getNsAdGroupId());
    }

    @Test
    public void getNsCampaignTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaign(logTag, connection, null, 0L));
    }

    @Test
    public void getNsCampaignTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaign(logTag, connection, null, 0, 0L));
    }

    @Test
    public void getNsCampaignTest3() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorCampaignId = 52405497;
        long nsCampaignId = 4671L;
        assertEquals(Long.valueOf(vendorCampaignId), nsEntityHelper.getNsCampaign(logTag, connection, prodInstId, nsCampaignId).getVendorCampaignId());
    }

    @Test
    public void getNsCampaignTest4() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorCampaignId = 52405497;
        long nsCampaignId = 4671L;
        assertEquals(nsCampaignId, nsEntityHelper.getNsCampaign(logTag, connection, prodInstId, VendorId.GOOGLE, vendorCampaignId).getNsCampaignId());
    }

    @Test
    public void getNsCampaignByNsAdGroupIdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 4718L;
        long nsAdGroupId = 100000100L;;
        assertEquals(nsCampaignId, nsEntityHelper.getNsCampaignByNsAdGroupId(logTag, connection, prodInstId, nsAdGroupId).getNsCampaignId());
    }

    @Test
    public void getNsCampaignByNsAdGroupIdTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = null;
        long nsCampaignId = 4718L;
        long nsAdGroupId = 100000100L;;
        assertEquals(nsCampaignId, nsEntityHelper.getNsCampaignByNsAdGroupId(logTag, connection, prodInstId, nsAdGroupId).getNsCampaignId());
    }

    @Test
    public void getNsKeywordTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsKeyword(logTag, connection, null, 0L));
    }

    @Test
    public void getNsKeywordTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsKeyword(logTag, connection, null, 0, 0L, 0L));
    }

    @Test
    public void getNsKeywordTest3() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorKeywordId = 92497594;
        long nsKeywordId = 74490L;
        assertEquals(Long.valueOf(vendorKeywordId), nsEntityHelper.getNsKeyword(logTag, connection, prodInstId, nsKeywordId).getVendorKeywordId());
    }

    @Test
    public void getNsKeywordTest4() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorKeywordId = 92497594;
        long nsAdGroupId = 7882L;
        long nsKeywordId = 74490L;

        assertEquals(nsKeywordId, nsEntityHelper.getNsKeyword(logTag, connection, prodInstId, VendorId.GOOGLE, vendorKeywordId, nsAdGroupId).getNsKeywordId());
    }

    @Test
    public void getNsKeywordTest5() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long vendorKeywordId = 10320260;
        long nsAdGroupId = 100000091L;
        long nsKeywordId = 100000652L;

        assertEquals(nsKeywordId, nsEntityHelper.getNsKeyword(logTag, connection, prodInstId, VendorId.GOOGLE, vendorKeywordId, nsAdGroupId).getNsKeywordId());
    }

    @Test
    public void getNsNegativeKeywordsExcludingStatusesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsNegativeKeywordsExcludingStatuses(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsNegativeKeywordsExcludingStatusesTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsNegativeKeywordsExcludingStatuses(logTag, connection, null, 0L, NegativeKeywordStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsNegativeKeywordsExcludingStatusesTest3() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsAdGroupId = 100000102L;
        assertFalse(nsEntityHelper.getNsNegativeKeywordsExcludingStatuses(logTag, connection, prodInstId, nsAdGroupId).isEmpty());
    }

    @Test
    public void getNsNegativeKeywordsExcludingStatusesTest4() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsAdGroupId = 100000102L;
        assertFalse(nsEntityHelper.getNsNegativeKeywordsExcludingStatuses(logTag, connection, prodInstId, nsAdGroupId, NegativeKeywordStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsNegativeKeywordsExcludingStatusesTest5() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsAdGroupId = 100000102L;
        assertTrue(nsEntityHelper.getNsNegativeKeywordsExcludingStatuses(logTag, connection, prodInstId, nsAdGroupId, NegativeKeywordStatus.ACTIVE, NegativeKeywordStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsCampaignNegativeKeywordsTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignNegativeKeywords(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsCampaignNegativeKeywordsTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100001082L;
        nsEntityHelper.deleteNsCampaignNegativeKeywords(prodInstId, connection, prodInstId, nsCampaignId);
        NsCampaignNegativeKeyword nsCampaignNegativeKeyword = new NsCampaignNegativeKeyword();
        nsCampaignNegativeKeyword.setKeyword("puggle");
        nsCampaignNegativeKeyword.setKeywordType(KeywordMatchType.EXACT);
        nsCampaignNegativeKeyword.setNsCampaignId(nsCampaignId);
        nsCampaignNegativeKeyword.setProdInstId(prodInstId);
        nsEntityHelper.insertNsCampaignNegativeKeywords(prodInstId, connection, Collections.singleton(nsCampaignNegativeKeyword));
        assertEquals("puggle", nsEntityHelper.getNsCampaignNegativeKeywords(logTag, connection, prodInstId, nsCampaignId).get(0).getKeyword());
    }

    @Test
    public void getNsCampaignLanguageTargetsTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignLanguageTargets(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsCampaignLanguageTargetsTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100001082L;
        nsEntityHelper.deleteNsCampaignLanguageTargets(prodInstId, connection, prodInstId, nsCampaignId);
        nsEntityHelper.insertNsCampaignLanguageTargets(prodInstId, connection, prodInstId, nsCampaignId, Arrays.asList(1, 2, 3));
        assertEquals(3, nsEntityHelper.getNsCampaignLanguageTargets(logTag, connection, prodInstId, nsCampaignId).size());
    }

    @Test
    public void getNsCampaignLocationTargetsTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignLocationTargets(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsCampaignLocationTargetsTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100001082L;
        nsEntityHelper.deleteNsCampaignLocationTargets(prodInstId, connection, prodInstId, nsCampaignId);
        nsEntityHelper.insertNsCampaignLocationTargets(prodInstId, connection, prodInstId, nsCampaignId, Collections.singleton(2));
        assertFalse(nsEntityHelper.getNsCampaignLocationTargets(logTag, connection, prodInstId, nsCampaignId).isEmpty());
    }

    @Test
    public void getNsCampaignPlatformTargetsTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignPlatformTargets(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsCampaignPlatformTargetsTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100001082L;
        nsEntityHelper.deleteNsCampaignPlatformTargets(prodInstId, connection, prodInstId, nsCampaignId);
        nsEntityHelper.insertNsCampaignPlatformTargets(prodInstId, connection, prodInstId, nsCampaignId, Arrays.asList(3, 1, 2));
        assertEquals(3, nsEntityHelper.getNsCampaignPlatformTargets(logTag, connection, prodInstId, nsCampaignId).size());
    }

    @Test
    public void getNsCampaignProximityTargetsTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignProximityTargets(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsCampaignProximityTargetsTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100001082L;
        nsEntityHelper.deleteNsCampaignProximityTargets(prodInstId, connection, prodInstId, nsCampaignId);
        NsCampaignProximityTarget nsCampaignProximityTarget = new NsCampaignProximityTarget();
        nsCampaignProximityTarget.setNsCampaignId(nsCampaignId);
        nsCampaignProximityTarget.setProdInstId(prodInstId);
        nsCampaignProximityTarget.setRadius(10D);
        nsCampaignProximityTarget.setZip("20194");
        nsEntityHelper.insertNsCampaignProximityTargets(prodInstId, connection, Collections.singleton(nsCampaignProximityTarget));
        assertFalse(nsEntityHelper.getNsCampaignProximityTargets(logTag, connection, prodInstId, nsCampaignId).isEmpty());
    }

    @Test
    public void getNsCampaignBusinessLocationTargetsTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignBusinessLocationTargets(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsBusinessLocationTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsBusinessLocation(logTag, connection, null, 0L));
    }

    @Test
    public void getNsBusinessLocationTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsBusinessLocation(logTag, connection, null, 0, 0L));
    }

    @Test
    public void getNsBusinessLocationTest3() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsBusinessLocationsExcludingStatuses(logTag, connection, null, 0).isEmpty());
    }

    @Test
    public void getNsBusinessLocationTest4() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertFalse(nsEntityHelper.getNsBusinessLocationsExcludingStatuses(logTag, connection, "WN.DEV.BING.0003", VendorId.MICROSOFT, BusinessLocationStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsCampaignAdCallExtensionTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignAdCallExtension(logTag, connection, null, 0L));
    }

    @Test
    public void getNsCampaignAdCallExtensionTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignAdCallExtension(logTag, connection, null, 0, 0L));
    }

    @Test
    public void getNsCampaignAdLocationExtensionTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignAdLocationExtension(logTag, connection, null, 0L));
    }

    @Test
    public void getNsCampaignAdLocationExtensionTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignAdLocationExtension(logTag, connection, null, 0, 0L));
    }

    @Test
    public void getNsCampaignAdSitelinksExtensionTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignAdSitelinksExtension(logTag, connection, null, 0L));
    }

    @Test
    public void getNsCampaignAdSitelinksExtensionTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignAdSitelinksExtension(logTag, connection, null, 0, 0L));
    }

    @Test
    public void getNsCampaignAdCallExtensionsExcludingStatusesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignAdCallExtensionsExcludingStatuses(logTag, connection, null, 0L, CampaignAdExtensionStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsCampaignAdLocationExtensionsExcludingStatusesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignAdLocationExtensionsExcludingStatuses(logTag, connection, null, 0L, CampaignAdExtensionStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsCampaignAdSitelinksExtensionsExcludingStatusesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignAdSitelinksExtensionsExcludingStatuses(logTag, connection, null, 0L, CampaignAdExtensionStatus.DELETED).isEmpty());
    }

    @Test
    public void getNsCampaignCategoryIdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignCategoryId(logTag, connection, null, 0L, 0L));
    }

    @Test
    public void getNsCampaignCategoryIdTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNotNull(nsEntityHelper.getNsCampaignCategoryId(logTag, connection, "WN.DEV.BING.0003", 100000845L, 320L));
    }

    @Test
    public void getNsCampaignGeographyIdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getNsCampaignGeographyId(logTag, connection, null, 0L, 0L));
    }

    @Test
    public void getNsCampaignGeographyIdTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNotNull(nsEntityHelper.getNsCampaignGeographyId(logTag, connection, "WN.DEV.BING.0003", 100000845L, 107828L));
    }

    @Test
    public void getBaseLocationIdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getBaseLocationId(logTag, connection, 0, null));
    }

    @Test
    public void getBaseLocationIdTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNotNull(nsEntityHelper.getBaseLocationId(logTag, connection, VendorId.SUPERPAGES, "Seattle, WA"));
    }

    @Test
    public void getBaseLocationIdTest3() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNotNull(nsEntityHelper.getBaseLocationId(logTag, connection, VendorId.SUPERPAGES, "Seattle"));
    }

    @Test
    public void getVendorCategoryIdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNull(nsEntityHelper.getVendorCategoryId(logTag, connection, 0, null));
    }

    @Test
    public void getVendorCategoryIdTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertNotNull(nsEntityHelper.getVendorCategoryId(logTag, connection, VendorId.SUPERPAGES, "Iron & Iron Work"));
    }

    @Test
    public void getNsCampaignAdSchedulesTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        assertTrue(nsEntityHelper.getNsCampaignAdSchedules(logTag, connection, null, 0L).isEmpty());
    }

    @Test
    public void getNsCampaignAdSchedulesTest2() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100001082L;
        nsEntityHelper.deleteNsCampaignAdSchedules(prodInstId, connection, prodInstId, nsCampaignId);
        NsCampaignAdSchedule schedule1 = new NsCampaignAdSchedule();
        schedule1.setBidModifier(1.0D);
        schedule1.setDayOfWeek("Monday");
        schedule1.setEndHour(11);
        schedule1.setNsCampaignId(nsCampaignId);
        schedule1.setProdInstId(prodInstId);
        schedule1.setStartHour(8);
        NsCampaignAdSchedule schedule2 = new NsCampaignAdSchedule();
        schedule2.setBidModifier(2.0D);
        schedule2.setDayOfWeek("Friday");
        schedule2.setEndHour(10);
        schedule2.setNsCampaignId(nsCampaignId);
        schedule2.setProdInstId(prodInstId);
        schedule2.setStartHour(6);
        nsEntityHelper.insertNsCampaignAdSchedules(logTag, connection, Arrays.asList(schedule1, schedule2));
        assertEquals(2, nsEntityHelper.getNsCampaignAdSchedules(logTag, connection, prodInstId, nsCampaignId).size());
    }

    @Test
    public void insertNsBusinessLocationTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        NsBusinessLocation nsBusinessLocation = new NsBusinessLocation();
        nsBusinessLocation.setCity("Seattle");
        nsBusinessLocation.setCountryCode("US");
        nsBusinessLocation.setDescription("Unit test business description");
        nsBusinessLocation.setName("Unit test business name");
        nsBusinessLocation.setNsStatus("UNKNOWN");
        nsBusinessLocation.setProdInstId(prodInstId);
        nsBusinessLocation.setState("WA");
        nsBusinessLocation.setStreetAddress1("123 1st Avenue");
        nsBusinessLocation.setUpdatedBySystem(updatedBySystem);
        nsBusinessLocation.setUpdatedByUser(updatedByUser);
        nsBusinessLocation.setVendorId(VendorId.MICROSOFT);
        nsBusinessLocation.setZip("98100");
        nsEntityHelper.insertNsBusinessLocation(logTag, connection, nsBusinessLocation);
        long nsBusinessLocationId = nsBusinessLocation.getNsBusinessLocationId();
        assertTrue(nsBusinessLocationId > 0L);
        nsBusinessLocation = nsEntityHelper.getNsBusinessLocation(logTag, connection, prodInstId, nsBusinessLocationId);
        assertEquals("98100", nsBusinessLocation.getZip());
        nsBusinessLocation.setCity("Lakewood");
        nsBusinessLocation.setUpdatedBySystem(updatedBySystem);
        nsBusinessLocation.setUpdatedByUser(updatedByUser);
        nsEntityHelper.updateNsBusinessLocation(logTag, connection, nsBusinessLocation);
        nsBusinessLocation = nsEntityHelper.getNsBusinessLocation(logTag, connection, prodInstId, nsBusinessLocationId);
        assertEquals("Lakewood", nsBusinessLocation.getCity());
    }

    @Test
    public void insertNsCampaignAdCallExtensionTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 4671L;
        NsCampaignAdCallExtension nsCampaignAdCallExtension = new NsCampaignAdCallExtension();
        nsCampaignAdCallExtension.setCountryCode("US");
        nsCampaignAdCallExtension.setNsCampaignId(nsCampaignId);
        nsCampaignAdCallExtension.setNsStatus(CampaignAdExtensionStatus.ACTIVE);
        nsCampaignAdCallExtension.setPhoneNumber("5551234567");
        nsCampaignAdCallExtension.setProdInstId(prodInstId);
        nsCampaignAdCallExtension.setUpdatedBySystem(updatedBySystem);
        nsCampaignAdCallExtension.setUpdatedByUser(updatedByUser);
        nsCampaignAdCallExtension.setVendorId(VendorId.GOOGLE);
        nsEntityHelper.insertNsCampaignAdCallExtension(logTag, connection, nsCampaignAdCallExtension);
        long nsCampaignAdCallExtensionId = nsCampaignAdCallExtension.getNsCampaignAdCallExtensionId();
        assertTrue(nsCampaignAdCallExtensionId > 0L);
        nsCampaignAdCallExtension = nsEntityHelper.getNsCampaignAdCallExtension(logTag, connection, prodInstId, nsCampaignAdCallExtensionId);
        assertEquals("5551234567", nsCampaignAdCallExtension.getPhoneNumber());
        assertFalse(nsEntityHelper.getNsCampaignAdCallExtensionsExcludingStatuses(logTag, connection, prodInstId, nsCampaignId, CampaignAdExtensionStatus.DELETED).isEmpty());
        nsCampaignAdCallExtension.clearTrackedUpdates();
        nsCampaignAdCallExtension.setCallOnly(true);
        nsCampaignAdCallExtension.setUpdatedBySystem(updatedBySystem);
        nsCampaignAdCallExtension.setUpdatedByUser(updatedByUser);
        nsEntityHelper.updateNsCampaignAdCallExtension(logTag, connection, nsCampaignAdCallExtension);
        nsCampaignAdCallExtension = nsEntityHelper.getNsCampaignAdCallExtension(logTag, connection, prodInstId, nsCampaignAdCallExtensionId);
        assertTrue(nsCampaignAdCallExtension.isCallOnly());
    }

    @Test
    public void insertNsCampaignAdLocationExtensionTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 4671L;
        NsCampaignAdLocationExtension nsCampaignAdLocationExtension = new NsCampaignAdLocationExtension();
        nsCampaignAdLocationExtension.setBusinessName("UNIT TEST INC.");
        nsCampaignAdLocationExtension.setCity("Herndon");
        nsCampaignAdLocationExtension.setCountryCode("US");
        nsCampaignAdLocationExtension.setNsCampaignId(nsCampaignId);
        nsCampaignAdLocationExtension.setNsStatus(CampaignAdExtensionStatus.ACTIVE);
        nsCampaignAdLocationExtension.setPhoneNumber("5551234567");
        nsCampaignAdLocationExtension.setProdInstId(prodInstId);
        nsCampaignAdLocationExtension.setState("VA");
        nsCampaignAdLocationExtension.setStreetAddress1("123 Elden Street");
        nsCampaignAdLocationExtension.setUpdatedBySystem(updatedBySystem);
        nsCampaignAdLocationExtension.setUpdatedByUser(updatedByUser);
        nsCampaignAdLocationExtension.setVendorId(VendorId.GOOGLE);
        nsCampaignAdLocationExtension.setZip("20199");
        nsEntityHelper.insertNsCampaignAdLocationExtension(logTag, connection, nsCampaignAdLocationExtension);
        long nsCampaignAdLocationExtensionId = nsCampaignAdLocationExtension.getNsCampaignAdLocationExtensionId();
        assertTrue(nsCampaignAdLocationExtensionId > 0L);
        nsCampaignAdLocationExtension = nsEntityHelper.getNsCampaignAdLocationExtension(logTag, connection, prodInstId, nsCampaignAdLocationExtensionId);
        assertEquals("20199", nsCampaignAdLocationExtension.getZip());
        assertFalse(nsEntityHelper.getNsCampaignAdLocationExtensionsExcludingStatuses(logTag, connection, prodInstId, nsCampaignId, CampaignAdExtensionStatus.DELETED).isEmpty());
        nsCampaignAdLocationExtension.clearTrackedUpdates();
        nsCampaignAdLocationExtension.setCity("Reston");
        nsCampaignAdLocationExtension.setUpdatedBySystem(updatedBySystem);
        nsCampaignAdLocationExtension.setUpdatedByUser(updatedByUser);
        nsEntityHelper.updateNsCampaignAdLocationExtension(prodInstId, connection, nsCampaignAdLocationExtension);
        nsCampaignAdLocationExtension = nsEntityHelper.getNsCampaignAdLocationExtension(logTag, connection, prodInstId, nsCampaignAdLocationExtensionId);
        assertEquals("Reston", nsCampaignAdLocationExtension.getCity());
    }

    @Test
    public void insertNsCampaignAdSitelinksExtensionTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100001082L;
        NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension = new NsCampaignAdSitelinksExtension();
        nsCampaignAdSitelinksExtension.setNsCampaignId(nsCampaignId);
        nsCampaignAdSitelinksExtension.setNsStatus(CampaignAdExtensionStatus.ACTIVE);
        nsCampaignAdSitelinksExtension.setProdInstId(prodInstId);
        nsCampaignAdSitelinksExtension.setUpdatedBySystem(updatedBySystem);
        nsCampaignAdSitelinksExtension.setUpdatedByUser(updatedByUser);
        nsCampaignAdSitelinksExtension.setVendorId(VendorId.GOOGLE);
        List<Sitelink> sitelinks = new ArrayList<Sitelink>();
        Sitelink sitelink = new Sitelink();
        sitelink.setDestinationUrl("http://www.example.com/sitelink1");
        sitelink.setDisplayText("SITELINK 1");
        sitelinks.add(sitelink);
        sitelink = new Sitelink();
        sitelink.setDestinationUrl("http://www.example.com/sitelink2");
        sitelink.setDisplayText("SITELINK 2");
        sitelinks.add(sitelink);
        nsCampaignAdSitelinksExtension.setSitelinks(sitelinks);
        nsEntityHelper.insertNsCampaignAdSitelinksExtension(logTag, connection, nsCampaignAdSitelinksExtension);
        long nsCampaignAdSitelinksExtensionId = nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId();
        assertTrue(nsCampaignAdSitelinksExtensionId > 0L);
        nsCampaignAdSitelinksExtension = nsEntityHelper.getNsCampaignAdSitelinksExtension(logTag, connection, prodInstId, nsCampaignAdSitelinksExtensionId);
        assertEquals(2, nsCampaignAdSitelinksExtension.getSitelinks().size());
        assertTrue(nsEntityHelper.getNsCampaignAdLocationExtensionsExcludingStatuses(logTag, connection, prodInstId, nsCampaignId, CampaignAdExtensionStatus.DELETED).isEmpty());
        nsCampaignAdSitelinksExtension.clearTrackedUpdates();

        sitelinks = new ArrayList<Sitelink>();
        sitelink = new Sitelink();
        sitelink.setDestinationUrl("http://www.example.com/sitelink3");
        sitelink.setDisplayText("SITELINK 3");
        sitelinks.add(sitelink);
        nsCampaignAdSitelinksExtension.setSitelinks(sitelinks);
        nsCampaignAdSitelinksExtension.setUpdatedBySystem(updatedBySystem);
        nsCampaignAdSitelinksExtension.setUpdatedByUser(updatedByUser);
        nsEntityHelper.updateNsCampaignAdSitelinksExtension(logTag, connection, nsCampaignAdSitelinksExtension);
        nsCampaignAdSitelinksExtension = nsEntityHelper.getNsCampaignAdSitelinksExtension(prodInstId, connection, prodInstId, nsCampaignAdSitelinksExtensionId);
        assertEquals(1, nsCampaignAdSitelinksExtension.getSitelinks().size());

        Map<Long, List<NsCampaignAdSitelinksExtension>> map = nsEntityHelper.getNsCampaignIdToNsCampaignAdSitelinksExtensionMap(prodInstId, connection, prodInstId);
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertNotNull(map.get(Long.valueOf(nsCampaignId)));
        assertFalse(map.get(Long.valueOf(nsCampaignId)).isEmpty());
    }

    @Test
    public void insertNsCampaignTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        NsCampaign nsCampaign = new NsCampaign();
        nsCampaign.setName("UNIT TEST");
        nsCampaign.setNsStatus(CampaignStatus.DELETED);
        nsCampaign.setProdInstId(prodInstId);
        nsCampaign.setUpdatedBySystem(updatedBySystem);
        nsCampaign.setUpdatedByUser(updatedByUser);
        nsCampaign.setVendorId(VendorId.MICROSOFT);
        nsEntityHelper.insertNsCampaign(prodInstId, connection, nsCampaign);
        NsAdGroup nsAdGroup = new NsAdGroup();
        nsAdGroup.setName("UNIT TEST");
        nsAdGroup.setNsCampaignId(nsCampaign.getNsCampaignId());
        nsAdGroup.setNsStatus(AdGroupStatus.DELETED);
        nsAdGroup.setProdInstId(prodInstId);
        nsAdGroup.setUpdatedBySystem(updatedBySystem);
        nsAdGroup.setUpdatedByUser(updatedByUser);
        nsAdGroup.setVendorId(VendorId.MICROSOFT);
        nsEntityHelper.insertNsAdGroup(prodInstId, connection, nsAdGroup);
        NsAd nsAd = new NsAd();
        nsAd.setHeadline("UNIT TEST");
        nsAd.setNsAdGroupId(nsAdGroup.getNsAdGroupId());
        nsAd.setNsStatus(AdStatus.DELETED);
        nsAd.setProdInstId(prodInstId);
        nsAd.setUpdatedBySystem(updatedBySystem);
        nsAd.setUpdatedByUser(updatedByUser);
        nsAd.setVendorId(VendorId.MICROSOFT);
        nsEntityHelper.insertNsAd(prodInstId, connection, nsAd);
        NsKeyword nsKeyword = new NsKeyword();
        nsKeyword.setBaseKeyword("UNIT TEST");
        nsKeyword.setNsAdGroupId(nsAdGroup.getNsAdGroupId());
        nsKeyword.setNsStatus(AdStatus.DELETED);
        nsKeyword.setProdInstId(prodInstId);
        nsKeyword.setUpdatedBySystem(updatedBySystem);
        nsKeyword.setUpdatedByUser(updatedByUser);
        nsKeyword.setVendorId(VendorId.MICROSOFT);
        nsEntityHelper.insertNsKeyword(prodInstId, connection, nsKeyword);
        NsNegativeKeyword nsNegativeKeyword = new NsNegativeKeyword();
        nsNegativeKeyword.setKeyword("UNIT TEST");
        nsNegativeKeyword.setNsAdGroupId(nsAdGroup.getNsAdGroupId());
        nsNegativeKeyword.setNsStatus(NegativeKeywordStatus.DELETED);
        nsNegativeKeyword.setProdInstId(prodInstId);
        nsNegativeKeyword.setUpdatedBySystem(prodInstId);
        nsNegativeKeyword.setUpdatedByUser(prodInstId);
    }

    @Test
    public void updateNsAdTest1() throws SQLException {
        NsEntityHelper nsEntityHelper = new NsEntityHelper("");
        String prodInstId = "WN.DEV.BING.0002";
        long nsAdId = 18891L;
        NsAd nsAd = nsEntityHelper.getNsAd(prodInstId, connection, prodInstId, nsAdId);
        nsAd.setEditorialStatus("UNIT TESTED");
        nsAd.setUpdatedBySystem(updatedBySystem);
        nsAd.setUpdatedByUser(updatedByUser);
        nsEntityHelper.updateNsAdFromVendorReport(prodInstId, connection, nsAd);
        nsAd = nsEntityHelper.getNsAd(prodInstId, connection, prodInstId, nsAdId);
        assertEquals("UNIT TESTED", nsAd.getEditorialStatus());
    }
}
