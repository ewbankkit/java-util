/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.Quadruple;
import com.netsol.adagent.util.beans.VendorAd;
import com.netsol.adagent.util.beans.VendorAdGroup;
import com.netsol.adagent.util.beans.VendorCampaign;
import com.netsol.adagent.util.beans.VendorCampaignCategoryGeography;
import com.netsol.adagent.util.beans.VendorEntity;
import com.netsol.adagent.util.beans.VendorEntityWithShareOfVoice;
import com.netsol.adagent.util.beans.VendorKeyword;

/**
 * DB helpers for vendor entities.
 */
public class VendorEntityHelper extends EntityHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:56 VendorEntityHelper.java NSI";

    private static final Double ZERO_DOUBLE = Double.valueOf(0D);
    private static final Long ZERO_LONG = Long.valueOf(0L);
    private static final Quadruple<Double, Long, Long, Double> ZERO_STATISTICS =
        Quadruple.from(ZERO_DOUBLE, ZERO_LONG, ZERO_LONG, ZERO_DOUBLE);

    /**
     * Constructor.
     */
    public VendorEntityHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public VendorEntityHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public VendorEntityHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public VendorEntityHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Return the statistics for the ad group updated on the specified date.
     *  First is the average position
     *  Second is the total number of clicks
     *  Third is the total number of impressions
     *  Fourth is the total cost.
     */
    public Object[] getAdGroupStatistics(String logTag, Connection connection, String prodInstId, long nsAdGroupId, Date updateDate) throws SQLException {
        final String SQL =
            "SELECT" +
            "  COALESCE(SUM(v.position * v.impressions) / SUM(v.impressions), 0)," +
            "  COALESCE(SUM(v.clicks), 0)," +
            "  COALESCE(SUM(v.impressions), 0)," +
            "  COALESCE(SUM(v.cost), 0) " +
            "FROM" +
            "  vendor_keyword AS v " +
            "INNER JOIN" +
            "  ns_keyword AS n " +
            "ON" +
            "  n.prod_inst_id = v.prod_inst_id AND" +
            "  n.ns_keyword_id = v.ns_keyword_id " +
            "WHERE" +
            "  v.update_date = ? AND" +
            "  n.ns_ad_group_id = ? AND" +
            "  n.prod_inst_id = ?;";

        return getStatistics(logTag, connection, SQL, prodInstId, nsAdGroupId, updateDate).toArray();
    }

    /**
     * Return the statistics for the campaign updated on the specified date.
     *  First is the average position
     *  Second is the total number of clicks
     *  Third is the total number of impressions
     *  Fourth is the total cost.
     */
    public Object[] getCampaignStatistics(String logTag, Connection connection, String prodInstId, long nsCampaignId, Date updateDate) throws SQLException {
        final String SQL =
            "SELECT" +
            "  COALESCE(SUM(v.position * v.impressions) / SUM(v.impressions), 0)," +
            "  COALESCE(SUM(v.clicks), 0)," +
            "  COALESCE(SUM(v.impressions), 0)," +
            "  COALESCE(SUM(v.cost), 0) " +
            "FROM" +
            "  vendor_ad_group AS v " +
            "INNER JOIN" +
            "  ns_ad_group AS n " +
            "ON" +
            "  n.prod_inst_id = v.prod_inst_id AND" +
            "  n.ns_ad_group_id = v.ns_ad_group_id " +
            "WHERE" +
            "  v.update_date = ? AND" +
            "  n.ns_campaign_id = ? AND" +
            "  n.prod_inst_id = ?;";

        return getStatistics(logTag, connection, SQL, prodInstId, nsCampaignId, updateDate).toArray();
    }

    /**
     * Replace a vendor ad.
     */
    public void replaceVendorAd(String logTag, Connection connection, VendorAd vendorAd, Date startDate) throws SQLException {
        replaceVendorEntity(logTag, connection, "vendor_ad", "ns_ad_id", vendorAd, vendorAd.getNsAdId(), startDate);
    }

    /**
     * Replace a vendor ad group.
     */
    public void replaceVendorAdGroup(String logTag, Connection connection, VendorAdGroup vendorAdGroup) throws SQLException {
        replaceVendorEntity(logTag, connection, "vendor_ad_group", "ns_ad_group_id", vendorAdGroup, vendorAdGroup.getNsAdGroupId());
    }

    /**
     * Replace a vendor campaign.
     */
    public void replaceVendorCampaign(String logTag, Connection connection, VendorCampaign vendorCampaign) throws SQLException {
        replaceVendorEntity(logTag, connection, "vendor_campaign", "ns_campaign_id", vendorCampaign, vendorCampaign.getNsCampaignId());
    }

    /**
     * Replace a vendor keyword.
     */
    public void replaceVendorKeyword(String logTag, Connection connection, VendorKeyword vendorKeyword, Date startDate) throws SQLException {
        replaceVendorEntity(logTag, connection, "vendor_keyword", "ns_keyword_id", vendorKeyword, vendorKeyword.getNsKeywordId(), startDate);
    }

    /**
     * Replace a vendor campaign category geography.
     */
    public void replaceVendorCampaignCategoryGeography(String logTag, Connection connection, VendorCampaignCategoryGeography vendorCampaignCategoryGeography) throws SQLException {
        final String SQL =
            "REPLACE INTO %1$s" +
            "  (prod_inst_id, %2$s, %3$s, update_date, clicks, impressions, ctr, cost_per_click, cost, position) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, IF(? > 0, ? / ?, 0), IF(? > 0, ? / ?, 0), ?, ?);";

        replaceVendorEntity(
                logTag,
                connection,
                SQL,
                "vendor_campaign_category_geography",
                new String[] {
                    "ns_campaign_category_id",
                    "ns_campaign_geography_id"
                },
                vendorCampaignCategoryGeography,
                new long[] {
                    vendorCampaignCategoryGeography.getNsCampaignCategoryId(),
                    vendorCampaignCategoryGeography.getNsCampaignGeographyId()
                });
    }

    /**
     * Rollup vendor ad group data to vendor campaign.
     */
    public void rollupVendorAdGroupsToCampaignLevel(String logTag, Connection connection, String[] prodInstIds, int vendorId, Date minDate, Date maxDate) throws SQLException {
        final String SQL =
            "INSERT INTO vendor_campaign" +
            "            (prod_inst_id," +
            "             ns_campaign_id," +
            "             update_date," +
            "             clicks," +
            "             impressions," +
            "             ctr," +
            "             cost_per_click," +
            "             cost," +
            "             position) " +
            "SELECT nsag.prod_inst_id                                                       AS prod_inst_id," +
            "       nsag.ns_campaign_id                                                     AS ns_campaign_id," +
            "       vag.update_date                                                         AS update_date," +
            "       COALESCE(SUM(vag.clicks), 0)                                            AS total_clicks," +
            "       COALESCE(SUM(vag.impressions), 0)                                       AS total_impressions," +
            "       COALESCE(SUM(vag.clicks) / SUM(vag.impressions), 0)                     AS click_through_rate," +
            "       COALESCE(SUM(vag.cost) / SUM(vag.clicks), 0)                            AS cost_per_click," +
            "       COALESCE(SUM(vag.cost), 0)                                              AS total_cost," +
            "       COALESCE(SUM(vag.position * vag.impressions) / SUM(vag.impressions), 0) AS average_position " +
            "FROM   vendor_ad_group AS vag" +
            "       INNER JOIN ns_ad_group AS nsag" +
            "         ON nsag.ns_ad_group_id = vag.ns_ad_group_id" +
            "            AND nsag.prod_inst_id = vag.prod_inst_id " +
            "WHERE  vag.prod_inst_id IN (%1$s)" +
            "       AND vag.update_date BETWEEN ? AND ?" +
            "       AND IF(? IS NULL, TRUE, nsag.vendor_id = ?) " +
            "GROUP  BY 1," +
            "          2," +
            "          3 " +
            "ON DUPLICATE KEY UPDATE" +
            "       clicks = VALUES(clicks)," +
            "       impressions = VALUES(impressions)," +
            "       ctr = VALUES(ctr)," +
            "       cost_per_click = VALUES(cost_per_click)," +
            "       cost = VALUES(cost)," +
            "       position = VALUES(position);";

        rollupVendorEntities(logTag, connection, SQL, prodInstIds, Integer.valueOf(vendorId), minDate, maxDate);
    }

    /**
     * Rollup vendor keyword data to vendor ad group.
     */
    public void rollupVendorKeywordsToAdGroupLevel(String logTag, Connection connection, String[] prodInstIds, int vendorId, Date minDate, Date maxDate) throws SQLException {
        final String SQL =
            "INSERT INTO vendor_ad_group" +
            "            (prod_inst_id," +
            "             ns_ad_group_id," +
            "             update_date," +
            "             clicks," +
            "             impressions," +
            "             ctr," +
            "             cost_per_click," +
            "             cost," +
            "             position) " +
            "SELECT nsk.prod_inst_id                                                     AS prod_inst_id," +
            "       nsk.ns_ad_group_id                                                   AS ns_ad_group_id," +
            "       vk.update_date                                                       AS update_date," +
            "       COALESCE(SUM(vk.clicks), 0)                                          AS total_clicks," +
            "       COALESCE(SUM(vk.impressions), 0)                                     AS total_impressions," +
            "       COALESCE(SUM(vk.clicks) / SUM(vk.impressions), 0)                    AS click_through_rate," +
            "       COALESCE(SUM(vk.cost) / SUM(vk.clicks), 0)                           AS cost_per_click," +
            "       COALESCE(SUM(vk.cost), 0)                                            AS total_cost," +
            "       COALESCE(SUM(vk.position * vk.impressions) / SUM(vk.impressions), 0) AS average_position " +
            "FROM   vendor_keyword AS vk" +
            "       INNER JOIN ns_keyword AS nsk" +
            "         ON nsk.ns_keyword_id = vk.ns_keyword_id" +
            "            AND nsk.prod_inst_id = vk.prod_inst_id " +
            "WHERE  vk.prod_inst_id IN (%1$s)" +
            "       AND vk.update_date BETWEEN ? AND ?" +
            "       AND IF(? IS NULL, TRUE, nsk.vendor_id = ?) " +
            "GROUP  BY 1," +
            "          2," +
            "          3 " +
            "ON DUPLICATE KEY UPDATE" +
            "       clicks = VALUES(clicks)," +
            "       impressions = VALUES(impressions)," +
            "       ctr = VALUES(ctr)," +
            "       cost_per_click = VALUES(cost_per_click)," +
            "       cost = VALUES(cost)," +
            "       position = VALUES(position);";

        rollupVendorEntities(logTag, connection, SQL, prodInstIds, Integer.valueOf(vendorId), minDate, maxDate);
    }

    /**
     * Update a vendor ad group's share of voice statistics.
     */
    public void updateVendorAdGroup(String logTag, Connection connection, VendorAdGroup vendorAdGroup) throws SQLException {
        updateVendorEntity(logTag, connection, "vendor_ad_group", "ns_ad_group_id", vendorAdGroup, vendorAdGroup.getNsAdGroupId());
    }

    /**
     * Update a vendor campaign's share of voice statistics.
     */
    public void updateVendorCampaign(String logTag, Connection connection, VendorCampaign vendorCampaign) throws SQLException {
        updateVendorEntity(logTag, connection, "vendor_campaign", "ns_campaign_id", vendorCampaign, vendorCampaign.getNsCampaignId());
    }

    /**
     * Zero out vendor keywords.
     */
    public void zeroOutVendorKeywords(String logTag, Connection connection, String prodInstId, int vendorId, Date minDate, Date maxDate) throws SQLException {
        final String SQL =
            "UPDATE vendor_keyword," +
            "       ns_keyword " +
            "SET    vendor_keyword.clicks = 0," +
            "       vendor_keyword.impressions = 0," +
            "       vendor_keyword.ctr = 0," +
            "       vendor_keyword.cost_per_click = 0," +
            "       vendor_keyword.cost = 0," +
            "       vendor_keyword.position = 0 " +
            "WHERE  ns_keyword.prod_inst_id = vendor_keyword.prod_inst_id" +
            "       AND ns_keyword.ns_keyword_id = vendor_keyword.ns_keyword_id" +
            "       AND vendor_keyword.prod_inst_id = ?" +
            "       AND vendor_keyword.update_date BETWEEN ? AND ?" +
            "       AND IF(? IS NULL, TRUE, ns_keyword.vendor_id = ?);";

        zeroOutVendorEntities(logTag, connection, SQL, prodInstId, Integer.valueOf(vendorId), minDate, maxDate);
    }

    private Quadruple<Double, Long, Long, Double> getStatistics(String logTag, Connection connection, String sql, String prodInstId, long nsEntityId, Date updateDate) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            int parameterIndex = 1;
            statement.setDate(parameterIndex++, toSqlDate(updateDate));
            statement.setLong(parameterIndex++, nsEntityId);
            statement.setString(parameterIndex++, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            if ((resultSet == null) || !resultSet.next()) {
                return ZERO_STATISTICS;
            }

            Double d1 = getDoubleValue(resultSet, 1);
            Long l2 = getLongValue(resultSet, 2);
            Long l3 = getLongValue(resultSet, 3);
            Double d4 = getDoubleValue(resultSet, 4);
            if (ZERO_DOUBLE.equals(d1) &&
                ZERO_LONG.equals(l2) &&
                ZERO_LONG.equals(l3) &&
                ZERO_DOUBLE.equals(d4)) {
                return ZERO_STATISTICS;
            }
            return Quadruple.from(d1, l2, l3, d4);
        }
        finally {
            close(statement, resultSet);
        }
    }

    private <T extends VendorEntity> void replaceVendorEntity(String logTag, Connection connection, String tableName, String nsEntityIdColumnName, T vendorEntity, long nsEntityId) throws SQLException {
        final String SQL =
            "REPLACE INTO %1$s" +
            "  (prod_inst_id, %2$s, update_date, clicks, impressions, ctr, cost_per_click, cost, position) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, IF(? > 0, ? / ?, 0), IF(? > 0, ? / ?, 0), ?, ?);";

        replaceVendorEntity(
                logTag,
                connection,
                SQL,
                tableName,
                new String[] {
                    nsEntityIdColumnName
                },
                vendorEntity,
                new long[] {
                    nsEntityId
                });
    }

    private <T extends VendorEntity> void replaceVendorEntity(String logTag, Connection connection, String sql, String tableName, String[] nsEntityIdColumnNames, T vendorEntity, long[] nsEntityIds) throws SQLException {
        PreparedStatement statement = null;
        try {
            Object[] args = new String[nsEntityIdColumnNames.length + 1];
            args[0] = tableName;
            System.arraycopy(nsEntityIdColumnNames, 0, args, 1, nsEntityIdColumnNames.length);
            sql = String.format(sql, args);
            statement = connection.prepareStatement(sql);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, vendorEntity.getProdInstId());
            for (long nsEntityId : nsEntityIds) {
                statement.setLong(parameterIndex++, nsEntityId);
            }
            statement.setDate(parameterIndex++, toSqlDate(vendorEntity.getUpdateDate()));
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setInt(parameterIndex++, vendorEntity.getImpressions());
            statement.setInt(parameterIndex++, vendorEntity.getImpressions());
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setInt(parameterIndex++, vendorEntity.getImpressions());
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setBigDecimal(parameterIndex++, vendorEntity.getCost());
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setBigDecimal(parameterIndex++, vendorEntity.getCost());
            statement.setBigDecimal(parameterIndex++, vendorEntity.getAveragePosition());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    private <T extends VendorEntity> void replaceVendorEntity(String logTag, Connection connection, String tableName, String nsEntityIdColumnName, T vendorEntity, long nsEntityId, Date startDate) throws SQLException {
        // Vendors might send more than one row for the same entity in a report. When that happens we
        // need to add the existing impressions, clicks and cost values to the new figures provided in the
        // vendorEntity received, and recalculate the costPerClick.
        // For more information see TR # 90020 - bugzilla issue # 2156 and TR # 93817 - bugzilla issue # 2369.
        final String SQL =
            "INSERT INTO %1$s" +
            "  (prod_inst_id, %2$s, update_date, clicks, impressions, ctr, cost_per_click, cost, position) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, IF(? > 0, ? / ?, 0), IF(? > 0, ? / ?, 0), ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            // Do the calculated columns first as MySQL will use the updated value of the columns in the calculations.
            // has the row been updated AFTER this job started?
            // Yes => calculate new click thru rate
            // No => overwrite click thru rate
            "  ctr = IF(update_timestamp > ?," +
            "           IF(impressions + VALUES(impressions) > 0," +
            "              (clicks + VALUES(clicks)) / (impressions + VALUES(impressions))," +
            "              0)," +
            "           VALUES(ctr))," +
            // has the row been updated AFTER this job started?
            // Yes => calculate new cost per click
            // No => overwrite cost per click
            "  cost_per_click = IF(update_timestamp > ?," +
            "                      IF(clicks + VALUES(clicks) > 0," +
            "                         (cost + VALUES(cost)) / (clicks + VALUES(clicks))," +
            "                         0)," +
            "                      VALUES(cost_per_click))," +
            // has the row been updated AFTER this job started?
            // Yes => calculate new average position
            // No => overwrite average position
            "  position = IF(update_timestamp > ?," +
            "                IF(impressions + VALUES(impressions) > 0," +
            "                   (impressions * position + VALUES(impressions) * VALUES(position)) / (impressions + VALUES(impressions))," +
            "                   0)," +
            "                VALUES(position))," +
            // has the row been updated AFTER this job started?
            // Yes => increment clicks
            // No => overwrite clicks
            "  clicks = IF(update_timestamp > ?," +
            "              clicks + VALUES(clicks)," +
            "              VALUES(clicks))," +
            // has the row been updated AFTER this job started?
            // Yes => increment impressions
            // No => overwrite impressions
            "  impressions = IF(update_timestamp > ?," +
            "                   impressions + VALUES(impressions)," +
            "                   VALUES(impressions))," +
            // has the row been updated AFTER this job started?
            // Yes => increment cost
            // No => overwrite cost
            "  cost = IF(update_timestamp > ?," +
            "            cost + VALUES(cost)," +
            "            VALUES(cost));";

        final Timestamp startTimestamp = toSqlTimestamp(startDate);
        PreparedStatement statement = null;
        try {
            String sql = String.format(SQL, tableName, nsEntityIdColumnName);
            statement = connection.prepareStatement(sql);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, vendorEntity.getProdInstId());
            statement.setLong(parameterIndex++, nsEntityId);
            statement.setDate(parameterIndex++, toSqlDate(vendorEntity.getUpdateDate()));
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setInt(parameterIndex++, vendorEntity.getImpressions());
            statement.setInt(parameterIndex++, vendorEntity.getImpressions());
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setInt(parameterIndex++, vendorEntity.getImpressions());
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setBigDecimal(parameterIndex++, vendorEntity.getCost());
            statement.setInt(parameterIndex++, vendorEntity.getClicks());
            statement.setBigDecimal(parameterIndex++, vendorEntity.getCost());
            statement.setBigDecimal(parameterIndex++, vendorEntity.getAveragePosition());
            statement.setTimestamp(parameterIndex++, startTimestamp);
            statement.setTimestamp(parameterIndex++, startTimestamp);
            statement.setTimestamp(parameterIndex++, startTimestamp);
            statement.setTimestamp(parameterIndex++, startTimestamp);
            statement.setTimestamp(parameterIndex++, startTimestamp);
            statement.setTimestamp(parameterIndex++, startTimestamp);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    private void rollupVendorEntities(String logTag, Connection connection, String sql, String[] prodInstIds, Integer vendorId, Date minDate, Date maxDate) throws SQLException {
        updateForParameters(logTag, connection, String.format(sql, getInClauseValuesSnippet(prodInstIds.length)),
                new InClauseParameters(prodInstIds),
                toSqlDate(minDate),
                toSqlDate(maxDate),
                vendorId,
                vendorId);
    }

    private <T extends VendorEntityWithShareOfVoice> void updateVendorEntity(String logTag, Connection connection, String tableName, String nsEntityIdColumnName, T vendorEntity, long nsEntityId) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  %1$s " +
            "SET" +
            "  impression_share = ?, " +
            "  exact_match_impression_share = ?, " +
            "  lost_impression_share_budget = ?, " +
            "  lost_impression_share_rank = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  %2$s = ? AND" +
            "  update_date = ?;";

        PreparedStatement statement = null;
        try {
            String sql = String.format(SQL, tableName, nsEntityIdColumnName);
            statement = connection.prepareStatement(sql);
            int parameterIndex = 1;
            statement.setObject(parameterIndex++, vendorEntity.getImpressionShare());
            statement.setObject(parameterIndex++, vendorEntity.getExactMatchImpressionShare());
            statement.setObject(parameterIndex++, vendorEntity.getLostImpressionShareBudget());
            statement.setObject(parameterIndex++, vendorEntity.getLostImpressionShareRank());
            statement.setString(parameterIndex++, vendorEntity.getProdInstId());
            statement.setLong(parameterIndex++, nsEntityId);
            statement.setDate(parameterIndex++, toSqlDate(vendorEntity.getUpdateDate()));
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    private void zeroOutVendorEntities(String logTag, Connection connection, String sql, String prodInstId, Integer vendorId, Date minDate, Date maxDate) throws SQLException {
        updateForParameters(logTag, connection, sql, prodInstId, toSqlDate(minDate), toSqlDate(maxDate), vendorId, vendorId);
    }
}
