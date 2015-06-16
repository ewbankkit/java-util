/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.github.ewbankkit.util.codes.VendorId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ewbankkit.util.CalendarUtil;
import com.netsol.adagent.util.beans.VendorAd;
import com.netsol.adagent.util.beans.VendorAdGroup;
import com.netsol.adagent.util.beans.VendorCampaign;
import com.netsol.adagent.util.beans.VendorCampaignCategoryGeography;
import com.netsol.adagent.util.beans.VendorKeyword;

public class VendorEntityHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:51 VendorEntityHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static Date startDate1; // Trigger row update.
    private static Date startDate2; // Trigger row replacement.
    private static Date updateDate;

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
        startDate1 = CalendarUtil.calendarToDate(calendar);
        calendar.add(Calendar.HOUR, 2);
        startDate2 = CalendarUtil.calendarToDate(calendar);
        updateDate = new Date();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getAdGroupStatisticsTest1() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        Object[] stats = vendorEntityHelper.getAdGroupStatistics(logTag, connection, null, 0L, null);
        assertNotNull(stats);
        assertEquals(0D, ((Double)stats[0]).doubleValue(), 0D);
        assertEquals(0L, ((Long)stats[1]).longValue());
        assertEquals(0L, ((Long)stats[2]).longValue());
        assertEquals(0D, ((Double)stats[3]).doubleValue(), 0D);
    }

    @Test
    public void getCampaignStatisticsTest1() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        Object[] stats = vendorEntityHelper.getCampaignStatistics(logTag, connection, null, 0L, null);
        assertNotNull(stats);
        assertEquals(0D, ((Double)stats[0]).doubleValue(), 0D);
        assertEquals(0L, ((Long)stats[1]).longValue());
        assertEquals(0L, ((Long)stats[2]).longValue());
        assertEquals(0D, ((Double)stats[3]).doubleValue(), 0D);
    }

    @Test
    public void replaceVendorAdTest1() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        String prodInstId = "WN.DEV.BING.0002";
        long nsAdId = 18891L;

        vendorEntityHelper.deleteVendorAd(prodInstId, connection, prodInstId, nsAdId, updateDate);

        VendorAd vendorAd = new VendorAd();
        vendorAd.setAveragePosition(3.3D);
        vendorAd.setClicks(3);
        vendorAd.setCost(33.3D);
        vendorAd.setImpressions(333);
        vendorAd.setNsAdId(nsAdId);
        vendorAd.setProdInstId(prodInstId);
        vendorAd.setUpdateDate(updateDate);
        vendorEntityHelper.replaceVendorAd(prodInstId, connection, vendorAd, startDate1);
        MyVendorEntity vendorEntity = vendorEntityHelper.getVendorAd(prodInstId, connection, prodInstId, nsAdId, updateDate);
        assertEquals(11.1D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(3.3D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(3, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(33.3D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(333, vendorEntity.getImpressions());
        assertEquals(nsAdId, vendorEntity.getNsEntityId());
        assertEquals(prodInstId, vendorEntity.getProdInstId());

        vendorAd.setAveragePosition(2.2D);
        vendorAd.setClicks(2);
        vendorAd.setCost(44.4D);
        vendorAd.setImpressions(222);
        vendorEntityHelper.replaceVendorAd(prodInstId, connection, vendorAd, startDate1);
        vendorEntity = vendorEntityHelper.getVendorAd(prodInstId, connection, prodInstId, nsAdId, updateDate);
        assertEquals(15.54D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(2.86D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(5, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(77.7D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(555, vendorEntity.getImpressions());
    }

    @Test
    public void replaceVendorAdTest2() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        String prodInstId = "WN.DEV.BING.0002";
        long nsAdId = 18891L;

        vendorEntityHelper.deleteVendorAd(prodInstId, connection, prodInstId, nsAdId, updateDate);

        VendorAd vendorAd = new VendorAd();
        vendorAd.setAveragePosition(3.3D);
        vendorAd.setClicks(3);
        vendorAd.setCost(33.3D);
        vendorAd.setImpressions(333);
        vendorAd.setNsAdId(nsAdId);
        vendorAd.setProdInstId(prodInstId);
        vendorAd.setUpdateDate(updateDate);
        vendorEntityHelper.replaceVendorAd(prodInstId, connection, vendorAd, startDate1);
        MyVendorEntity vendorEntity = vendorEntityHelper.getVendorAd(prodInstId, connection, prodInstId, nsAdId, updateDate);
        assertEquals(11.1D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(3.3D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(3, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(33.3D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(333, vendorEntity.getImpressions());
        assertEquals(nsAdId, vendorEntity.getNsEntityId());
        assertEquals(prodInstId, vendorEntity.getProdInstId());

        vendorAd.setAveragePosition(2.2D);
        vendorAd.setClicks(2);
        vendorAd.setCost(44.4D);
        vendorAd.setImpressions(222);
        vendorEntityHelper.replaceVendorAd(prodInstId, connection, vendorAd, startDate2);
        vendorEntity = vendorEntityHelper.getVendorAd(prodInstId, connection, prodInstId, nsAdId, updateDate);
        assertEquals(22.2D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(2.2D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(2, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(44.4D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(222, vendorEntity.getImpressions());
    }

    @Test
    public void replaceVendorAdGroupTest1() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        String prodInstId = "WN.DEV.BING.0002";
        long nsAdGroupId = 100000106L;

        vendorEntityHelper.deleteVendorAdGroup(prodInstId, connection, prodInstId, nsAdGroupId, updateDate);

        VendorAdGroup vendorAdGroup = new VendorAdGroup();
        vendorAdGroup.setAveragePosition(3.3D);
        vendorAdGroup.setClicks(3);
        vendorAdGroup.setCost(33.3D);
        vendorAdGroup.setImpressions(333);
        vendorAdGroup.setNsAdGroupId(nsAdGroupId);
        vendorAdGroup.setProdInstId(prodInstId);
        vendorAdGroup.setUpdateDate(updateDate);
        vendorEntityHelper.replaceVendorAdGroup(prodInstId, connection, vendorAdGroup);
        MyVendorEntity vendorEntity = vendorEntityHelper.getVendorAdGroup(prodInstId, connection, prodInstId, nsAdGroupId, updateDate);
        assertEquals(11.1D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(3.3D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(3, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(33.3D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(333, vendorEntity.getImpressions());
        assertEquals(nsAdGroupId, vendorEntity.getNsEntityId());
        assertEquals(prodInstId, vendorEntity.getProdInstId());

        vendorAdGroup.setAveragePosition(2.2D);
        vendorAdGroup.setClicks(2);
        vendorAdGroup.setCost(44.4D);
        vendorAdGroup.setImpressions(222);
        vendorEntityHelper.replaceVendorAdGroup(prodInstId, connection, vendorAdGroup);
        vendorEntity = vendorEntityHelper.getVendorAdGroup(prodInstId, connection, prodInstId, nsAdGroupId, updateDate);
        assertEquals(22.2D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(2.2D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(2, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(44.4D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(222, vendorEntity.getImpressions());

        vendorAdGroup.setExactMatchImpressionShare(Double.valueOf(18.18D));
        vendorAdGroup.setImpressionShare(Double.valueOf(28.28D));
        vendorAdGroup.setLostImpressionShareBudget(Double.valueOf(38.38D));
        vendorAdGroup.setLostImpressionShareRank(Double.valueOf(48.48D));
        vendorEntityHelper.updateVendorAdGroup(prodInstId, connection, vendorAdGroup);
    }

    @Test
    public void replaceVendorCampaignTest1() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();

        String prodInstId = "WN.DEV.BING.0002";
        long nsCampaignId = 100000113L;

        vendorEntityHelper.deleteVendorCampaign(prodInstId, connection, prodInstId, nsCampaignId, updateDate);

        VendorCampaign vendorCampaign = new VendorCampaign();
        vendorCampaign.setAveragePosition(3.3D);
        vendorCampaign.setClicks(3);
        vendorCampaign.setCost(33.3D);
        vendorCampaign.setImpressions(333);
        vendorCampaign.setNsCampaignId(nsCampaignId);
        vendorCampaign.setProdInstId(prodInstId);
        vendorCampaign.setUpdateDate(updateDate);
        vendorEntityHelper.replaceVendorCampaign(prodInstId, connection, vendorCampaign);
        MyVendorEntity vendorEntity = vendorEntityHelper.getVendorCampaign(prodInstId, connection, prodInstId, nsCampaignId, updateDate);
        assertEquals(11.1D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(3.3D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(3, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(33.3D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(333, vendorEntity.getImpressions());
        assertEquals(nsCampaignId, vendorEntity.getNsEntityId());
        assertEquals(prodInstId, vendorEntity.getProdInstId());

        vendorCampaign.setAveragePosition(2.2D);
        vendorCampaign.setClicks(2);
        vendorCampaign.setCost(44.4D);
        vendorCampaign.setImpressions(222);
        vendorEntityHelper.replaceVendorCampaign(prodInstId, connection, vendorCampaign);
        vendorEntity = vendorEntityHelper.getVendorCampaign(prodInstId, connection, prodInstId, nsCampaignId, updateDate);
        assertEquals(22.2D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(2.2D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(2, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(44.4D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(222, vendorEntity.getImpressions());

        vendorCampaign.setExactMatchImpressionShare(Double.valueOf(18.18D));
        vendorCampaign.setImpressionShare(Double.valueOf(28.28D));
        vendorCampaign.setLostImpressionShareBudget(Double.valueOf(38.38D));
        vendorCampaign.setLostImpressionShareRank(Double.valueOf(48.48D));
        vendorEntityHelper.updateVendorCampaign(prodInstId, connection, vendorCampaign);
    }

    @Test
    public void replaceVendorKeywordTest1() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        String prodInstId = "WN.DEV.BING.0002";
        long nsKeywordId = 100000549L;

        Date maxDate = updateDate;
        Date minDate = updateDate;
        int vendorId = VendorId.GOOGLE;

        vendorEntityHelper.zeroOutVendorKeywords(prodInstId, connection, prodInstId, vendorId, minDate, maxDate);

        vendorEntityHelper.deleteVendorKeyword(prodInstId, connection, prodInstId, nsKeywordId, updateDate);

        VendorKeyword vendorKeyword = new VendorKeyword();
        vendorKeyword.setAveragePosition(3.3D);
        vendorKeyword.setClicks(3);
        vendorKeyword.setCost(33.3D);
        vendorKeyword.setImpressions(333);
        vendorKeyword.setNsKeywordId(nsKeywordId);
        vendorKeyword.setProdInstId(prodInstId);
        vendorKeyword.setUpdateDate(updateDate);
        vendorEntityHelper.replaceVendorKeyword(prodInstId, connection, vendorKeyword, startDate1);
        MyVendorEntity vendorEntity = vendorEntityHelper.getVendorKeyword(prodInstId, connection, prodInstId, nsKeywordId, updateDate);
        assertEquals(11.1D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(3.3D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(3, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(33.3D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(333, vendorEntity.getImpressions());
        assertEquals(nsKeywordId, vendorEntity.getNsEntityId());
        assertEquals(prodInstId, vendorEntity.getProdInstId());

        vendorKeyword.setAveragePosition(2.2D);
        vendorKeyword.setClicks(2);
        vendorKeyword.setCost(44.4D);
        vendorKeyword.setImpressions(222);
        vendorEntityHelper.replaceVendorKeyword(prodInstId, connection, vendorKeyword, startDate1);
        vendorEntity = vendorEntityHelper.getVendorKeyword(prodInstId, connection, prodInstId, nsKeywordId, updateDate);
        assertEquals(15.54D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(2.86D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(5, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(77.7D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(555, vendorEntity.getImpressions());

        String[] prodInstIds = new String[] {prodInstId};
        vendorEntityHelper.rollupVendorKeywordsToAdGroupLevel(prodInstId, connection, prodInstIds, vendorId, minDate, maxDate);
        vendorEntityHelper.rollupVendorAdGroupsToCampaignLevel(prodInstId, connection, prodInstIds, vendorId, minDate, maxDate);
    }

    @Test
    public void replaceVendorKeywordTest2() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        String prodInstId = "WN.DEV.BING.0002";
        long nsKeywordId = 100000549L;

        vendorEntityHelper.deleteVendorKeyword(prodInstId, connection, prodInstId, nsKeywordId, updateDate);

        VendorKeyword vendorKeyword = new VendorKeyword();
        vendorKeyword.setAveragePosition(3.3D);
        vendorKeyword.setClicks(3);
        vendorKeyword.setCost(33.3D);
        vendorKeyword.setImpressions(333);
        vendorKeyword.setNsKeywordId(nsKeywordId);
        vendorKeyword.setProdInstId(prodInstId);
        vendorKeyword.setUpdateDate(updateDate);
        vendorEntityHelper.replaceVendorKeyword(prodInstId, connection, vendorKeyword, startDate1);
        MyVendorEntity vendorEntity = vendorEntityHelper.getVendorKeyword(prodInstId, connection, prodInstId, nsKeywordId, updateDate);
        assertEquals(11.1D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(3.3D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(3, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(33.3D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(333, vendorEntity.getImpressions());
        assertEquals(nsKeywordId, vendorEntity.getNsEntityId());
        assertEquals(prodInstId, vendorEntity.getProdInstId());

        vendorKeyword.setAveragePosition(2.2D);
        vendorKeyword.setClicks(2);
        vendorKeyword.setCost(44.4D);
        vendorKeyword.setImpressions(222);
        vendorEntityHelper.replaceVendorKeyword(prodInstId, connection, vendorKeyword, startDate2);
        vendorEntity = vendorEntityHelper.getVendorKeyword(prodInstId, connection, prodInstId, nsKeywordId, updateDate);
        assertEquals(22.2D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(2.2D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(2, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(44.4D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(222, vendorEntity.getImpressions());
    }

    @Test
    public void replaceVendorCampaignCategoryGeographyTest1() throws SQLException {
        VendorEntityHelper vendorEntityHelper = new VendorEntityHelper();
        String prodInstId = "WN.TEST.20091116121040";
        long nsCampaignCategoryId = 4L;
        long nsCampaignGeographyId = 38L;

        vendorEntityHelper.deleteVendorCampaignCategoryGeography(prodInstId, connection, prodInstId, nsCampaignCategoryId, updateDate);

        VendorCampaignCategoryGeography vendorCampaignCategoryGeography = new VendorCampaignCategoryGeography();
        vendorCampaignCategoryGeography.setAveragePosition(3.3D);
        vendorCampaignCategoryGeography.setClicks(3);
        vendorCampaignCategoryGeography.setCost(33.3D);
        vendorCampaignCategoryGeography.setImpressions(333);
        vendorCampaignCategoryGeography.setNsCampaignCategoryId(nsCampaignCategoryId);
        vendorCampaignCategoryGeography.setNsCampaignGeographyId(nsCampaignGeographyId);
        vendorCampaignCategoryGeography.setProdInstId(prodInstId);
        vendorCampaignCategoryGeography.setUpdateDate(updateDate);
        vendorEntityHelper.replaceVendorCampaignCategoryGeography(prodInstId, connection, vendorCampaignCategoryGeography);
        MyVendorEntity vendorEntity = vendorEntityHelper.getVendorCampaignCategoryGeography(prodInstId, connection, prodInstId, nsCampaignCategoryId, updateDate);
        assertEquals(11.1D, vendorEntity.getAverageCostPerClick().doubleValue(), 0D);
        assertEquals(3.3D, vendorEntity.getAveragePosition().doubleValue(), 0D);
        assertEquals(3, vendorEntity.getClicks());
        assertEquals(0.009D, vendorEntity.getClickThroughRate().doubleValue(), 0D);
        assertEquals(33.3D, vendorEntity.getCost().doubleValue(), 0D);
        assertEquals(333, vendorEntity.getImpressions());
        assertEquals(nsCampaignCategoryId, vendorEntity.getNsEntityId());
        assertEquals(prodInstId, vendorEntity.getProdInstId());
    }

    private static class MyVendorEntity extends com.netsol.adagent.util.beans.VendorEntity {
        private BigDecimal averageCostPerClick = BigDecimal.ZERO;
        private BigDecimal clickThroughRate = BigDecimal.ZERO;

        public void setAverageCostPerClick(double averageCostPerClick) {
            this.averageCostPerClick = BaseHelper.toBigDecimal(averageCostPerClick, 2);
        }

        public BigDecimal getAverageCostPerClick() {
            return averageCostPerClick;
        }

        public void setClickThroughRate(double clickThroughRate) {
            this.clickThroughRate = BaseHelper.toBigDecimal(clickThroughRate, 4);
        }

        public BigDecimal getClickThroughRate() {
            return clickThroughRate;
        }

        public void setNsEntityId(long nsEntityId) {
            super.setNsEntityId(nsEntityId);
        }

        public long getNsEntityId() {
            return super.getNsEntityId();
        }
    }

    private static class VendorEntityHelper extends com.netsol.adagent.util.dbhelpers.VendorEntityHelper {
        public VendorEntityHelper() {
            super("");
        }

        /**
         * Delete a vendor ad.
         */
        public void deleteVendorAd(String logTag, Connection connection, String prodInstId, long nsAdId, Date updateDate) throws SQLException {
            deleteVendorEntity(logTag, connection, "vendor_ad", "ns_ad_id", prodInstId, nsAdId, updateDate);
        }

        /**
         * Delete a vendor ad group.
         */
        public void deleteVendorAdGroup(String logTag, Connection connection, String prodInstId, long nsAdGroupId, Date updateDate) throws SQLException {
            deleteVendorEntity(logTag, connection, "vendor_ad_group", "ns_ad_group_id", prodInstId, nsAdGroupId, updateDate);
        }

        /**
         * Delete a vendor campaign.
         */
        public void deleteVendorCampaign(String logTag, Connection connection, String prodInstId, long nsCampaignId, Date updateDate) throws SQLException {
            deleteVendorEntity(logTag, connection, "vendor_campaign", "ns_campaign_id", prodInstId, nsCampaignId, updateDate);
        }

        /**
         * Delete a vendor campaign category geography.
         */
        public void deleteVendorCampaignCategoryGeography(String logTag, Connection connection, String prodInstId, long nsCampaignCategoryId, Date updateDate) throws SQLException {
            deleteVendorEntity(logTag, connection, "vendor_campaign_category_geography", "ns_campaign_category_id", prodInstId, nsCampaignCategoryId, updateDate);
        }

        /**
         * Delete a vendor keyword.
         */
        public void deleteVendorKeyword(String logTag, Connection connection, String prodInstId, long nsKeywordId, Date updateDate) throws SQLException {
            deleteVendorEntity(logTag, connection, "vendor_keyword", "ns_keyword_id", prodInstId, nsKeywordId, updateDate);
        }

        /**
         * Return a vendor ad.
         */
        public MyVendorEntity getVendorAd(String logTag, Connection connection, String prodInstId, long nsAdId, Date updateDate) throws SQLException {
            return getVendorEntity(logTag, connection, "vendor_ad", "ns_ad_id", prodInstId, nsAdId, updateDate);
        }

        /**
         * Return a vendor ad group.
         */
        public MyVendorEntity getVendorAdGroup(String logTag, Connection connection, String prodInstId, long nsAdGroupId, Date updateDate) throws SQLException {
            return getVendorEntity(logTag, connection, "vendor_ad_group", "ns_ad_group_id", prodInstId, nsAdGroupId, updateDate);
        }

        /**
         * Return a vendor campaign.
         */
        public MyVendorEntity getVendorCampaign(String logTag, Connection connection, String prodInstId, long nsCampaignId, Date updateDate) throws SQLException {
            return getVendorEntity(logTag, connection, "vendor_campaign", "ns_campaign_id", prodInstId, nsCampaignId, updateDate);
        }

        /**
         * Return a vendor keyword.
         */
        public MyVendorEntity getVendorCampaignCategoryGeography(String logTag, Connection connection, String prodInstId, long nsCampaignCategoryId, Date updateDate) throws SQLException {
            return getVendorEntity(logTag, connection, "vendor_campaign_category_geography", "ns_campaign_category_id", prodInstId, nsCampaignCategoryId, updateDate);
        }

        /**
         * Return a vendor keyword.
         */
        public MyVendorEntity getVendorKeyword(String logTag, Connection connection, String prodInstId, long nsKeywordId, Date updateDate) throws SQLException {
            return getVendorEntity(logTag, connection, "vendor_keyword", "ns_keyword_id", prodInstId, nsKeywordId, updateDate);
        }

        private void deleteVendorEntity(String logTag, Connection connection, String tableName, String nsEntityColumnName, String prodInstId, long nsEntityId, Date updateDate) throws SQLException {
            final String SQL =
                "DELETE FROM" +
                "  %1$s " +
                "WHERE" +
                "  prod_inst_id = ? AND" +
                "  %2$s = ? AND" +
                "  update_date = ?;";

            PreparedStatement statement = null;
            try {
                String sql = String.format(SQL, tableName, nsEntityColumnName);
                statement = connection.prepareStatement(sql);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, prodInstId);
                statement.setLong(parameterIndex++, nsEntityId);
                statement.setDate(parameterIndex++, toSqlDate(updateDate));
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }

        private MyVendorEntity getVendorEntity(String logTag, Connection connection, final String tableName, final String nsEntityColumnName, String prodInstId, long nsEntityId, Date updateDate) throws SQLException {
            final String SQL =
                "SELECT" +
                "  prod_inst_id," +
                "  %2$s," +
                "  update_date," +
                "  clicks," +
                "  impressions," +
                "  ctr," +
                "  cost_per_click," +
                "  cost," +
                "  position " +
                "FROM" +
                "  %1$s " +
                "WHERE" +
                "  prod_inst_id = ? AND" +
                "  %2$s = ? AND" +
                "  update_date = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                String sql = String.format(SQL, tableName, nsEntityColumnName);
                statement = connection.prepareStatement(sql);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, prodInstId);
                statement.setLong(parameterIndex++, nsEntityId);
                statement.setDate(parameterIndex++, toSqlDate(updateDate));
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, new Factory<MyVendorEntity>() {
                    public MyVendorEntity newInstance(ResultSet resultSet) throws SQLException {
                        MyVendorEntity vendorEntity = new MyVendorEntity();
                        vendorEntity.setAverageCostPerClick(resultSet.getDouble("cost_per_click"));
                        vendorEntity.setAveragePosition(resultSet.getDouble("position"));
                        vendorEntity.setClicks(resultSet.getInt("clicks"));
                        vendorEntity.setClickThroughRate(resultSet.getDouble("ctr"));
                        vendorEntity.setCost(resultSet.getDouble("cost"));
                        vendorEntity.setImpressions(resultSet.getInt("impressions"));
                        vendorEntity.setNsEntityId(resultSet.getLong(nsEntityColumnName));
                        vendorEntity.setProdInstId(resultSet.getString("prod_inst_id"));
                        vendorEntity.setUpdateDate(resultSet.getDate("update_date"));
                        return vendorEntity;
                    }});
            }
            finally {
                close(statement, resultSet);
            }
        }
    }
}
