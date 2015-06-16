/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.beans.BaseData.arrayIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.coalesce;
import static com.netsol.adagent.util.beans.BaseData.collectionIsNotEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.ArrayBuilder;
import com.netsol.adagent.util.F1;
import com.netsol.adagent.util.beans.NsAd;
import com.netsol.adagent.util.beans.NsAdGroup;
import com.netsol.adagent.util.beans.NsBusinessLocation;
import com.netsol.adagent.util.beans.NsCampaign;
import com.netsol.adagent.util.beans.NsCampaignAdCallExtension;
import com.netsol.adagent.util.beans.NsCampaignAdLocationExtension;
import com.netsol.adagent.util.beans.NsCampaignAdSchedule;
import com.netsol.adagent.util.beans.NsCampaignAdSitelinksExtension;
import com.netsol.adagent.util.beans.NsCampaignAdSitelinksExtension.Sitelink;
import com.netsol.adagent.util.beans.NsCampaignBusinessLocationTarget;
import com.netsol.adagent.util.beans.NsCampaignNegativeKeyword;
import com.netsol.adagent.util.beans.NsCampaignProximityTarget;
import com.netsol.adagent.util.beans.NsEntity;
import com.netsol.adagent.util.beans.NsKeyword;
import com.netsol.adagent.util.beans.NsNegativeKeyword;
import com.netsol.adagent.util.beans.NsSuperPagesCampaign;
import com.netsol.adagent.util.beans.Option;
import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.codes.AdGroupStatus;
import com.netsol.adagent.util.codes.AdStatus;
import com.netsol.adagent.util.codes.CampaignAdExtensionStatus;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.KeywordStatus;
import com.netsol.adagent.util.codes.StatusBase;
import com.netsol.adagent.util.codes.VendorStatus;
import com.netsol.adagent.util.codes.VendorType;
import com.netsol.adagent.util.log.BaseLoggable;
import com.netsol.vendor.beans.NSCampaignCategoryData;
import com.netsol.vendor.beans.NSCampaignGeoData;
import com.netsol.vendor.beans.NSSuperPagesCampaignData;

/**
 * DB helpers for NS entities.
 */
public class NsEntityHelper extends EntityHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:52 NsEntityHelper.java NSI";

    private static final Option<Long> IGNORE_TARGET_ID = Option.none();

    /**
     * Constructor.
     */
    public NsEntityHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public NsEntityHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public NsEntityHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public NsEntityHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public NsEntityHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Delete the NS campaign ad schedules corresponding to the given prodInstId and NS campaign ID.
     */
    public int deleteNsCampaignAdSchedules(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, "ns_campaign_ad_schedule", prodInstId, nsCampaignId);
    }

    /**
     * Delete the NS campaign business location targets corresponding to the given prodInstId and NS campaign ID.
     */
    public int deleteNsCampaignBusinessLocationTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, "ns_campaign_business_location_targets", prodInstId, nsCampaignId);
    }

    /**
     * Delete the NS campaign language targets corresponding to the given prodInstId and NS campaign ID.
     */
    public int deleteNsCampaignLanguageTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, "ns_campaign_language_targets", prodInstId, nsCampaignId);
    }

    /**
     * Delete the NS campaign location targets corresponding to the given prodInstId and NS campaign ID.
     */
    public int deleteNsCampaignLocationTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, "ns_campaign_location_targets", prodInstId, nsCampaignId);
    }

    /**
     * Delete the NS campaign negative keywords corresponding to the given prodInstId and NS campaign ID.
     */
    public int deleteNsCampaignNegativeKeywords(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, "ns_campaign_negative_keyword", prodInstId, nsCampaignId);
    }

    /**
     * Delete the NS campaign platform targets corresponding to the given prodInstId and NS campaign ID.
     */
    public int deleteNsCampaignPlatformTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, "ns_campaign_platform_targets", prodInstId, nsCampaignId);
    }

    /**
     * Delete the NS campaign proximity targets corresponding to the given prodInstId and NS campaign ID.
     */
    public int deleteNsCampaignProximityTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, "ns_campaign_proximity_targets", prodInstId, nsCampaignId);
    }

    /**
     * Return the NS ad corresponding to the given prodInstId and NS ad ID.
     */
    public NsAd getNsAd(String logTag, Connection connection, String prodInstId, long nsAdId) throws SQLException {
        final String SQL = NsAdFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nsa.prod_inst_id = ?)" +
            "       AND nsa.ns_ad_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsAdFactory.INSTANCE, prodInstId, nsAdId);
    }

    /**
     * Return the NS ad corresponding to the given prodInstId, vendor ID and vendor ad ID.
     */
    public NsAd getNsAd(String logTag, Connection connection, String prodInstId, int vendorId, long vendorAdId, long nsAdGroupId) throws SQLException {
        final String SQL = NsAdFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nsa.prod_inst_id = ?)" +
            "       AND nsa.vendor_id = ?" +
            "       AND nsa.vendor_ad_id = ?" +
            "       AND nsa.ns_ad_group_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsAdFactory.INSTANCE, prodInstId, vendorId, vendorAdId, nsAdGroupId);
    }

    /**
     * Return the NS ad group ID corresponding to the given vendor ad group ID.
     */
    public Long getNsAdGroupId(String logTag, Connection connection, NsAdGroup nsAdGroup) throws SQLException {
        final String SQL =
            "SELECT" +
            "  ns_ad_group_id " +
            "FROM" +
            "  ns_ad_group " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_campaign_id = ? AND" +
            "  vendor_ad_group_id = ? AND" +
            "  vendor_id = ? " +
            "LIMIT 1;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsAdGroup.getProdInstId());
            statement.setLong(parameterIndex++, nsAdGroup.getNsCampaignId());
            statement.setLong(parameterIndex++, nsAdGroup.getVendorAdGroupId());
            statement.setInt(parameterIndex++, nsAdGroup.getVendorId());
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstNsAdGroupId(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return a map of NS ad group IDs to NS ads for the specified product instance ID, ad type and optional host names.
     */
    public Map<Long, List<NsAd>> getNsAdGroupIdToNsAdMap(String logTag, Connection connection, String prodInstId, String adType, String... hostNames) throws SQLException {
        final String SQL = NsAdFactory.SQL_SELECT_EXPRESSION +
            "       INNER JOIN ns_ad_group AS nsag" +
            "           ON nsag.prod_inst_id = nsa.prod_inst_id" +
            "              AND nsag.ns_ad_group_id = nsa.ns_ad_group_id" +
            "       INNER JOIN ns_campaign AS nsc" +
            "           ON nsc.prod_inst_id = nsag.prod_inst_id" +
            "              AND nsc.ns_campaign_id = nsag.ns_campaign_id " +
            "WHERE  nsa.prod_inst_id = ?" +
            "       AND nsa.ad_type = ?" +
            "       AND nsa.`status` <> ?" +
            "       AND nsag.`status` <> ?" +
            "       AND nsc.`status` <> ?" +
            "       AND IF(?, TRUE, HOST_NAME(nsa.destination_url) IN (%1$s));";

        hostNames = coalesce(hostNames, new String[0]);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(String.format(SQL, getInClauseValuesSnippet(hostNames.length)));
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, adType);
            statement.setString(parameterIndex++, AdStatus.DELETED);
            statement.setString(parameterIndex++, AdGroupStatus.DELETED);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setBoolean(parameterIndex++, arrayIsEmpty(hostNames));
            parameterIndex = setInClauseParameters(statement, parameterIndex, hostNames);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newMapOfLists(resultSet, new Factory<Pair<Long, NsAd>>() {
                public Pair<Long, NsAd> newInstance(ResultSet resultSet) throws SQLException {
                    NsAd nsAd = NsAdFactory.INSTANCE.newInstance(resultSet);
                    return Pair.from(Long.valueOf(nsAd.getNsAdGroupId()), nsAd);
                }});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return a map of NS ad group IDs to NS keywords for the specified product instance ID and optional host names.
     */
    public Map<Long, List<NsKeyword>> getNsAdGroupIdToNsKeywordMap(String logTag, Connection connection, String prodInstId, String... hostNames) throws SQLException {
        final String SQL = NsKeywordFactory.SQL_SELECT_EXPRESSION +
            "       INNER JOIN ns_ad_group AS nsag" +
            "           ON nsag.prod_inst_id = nsk.prod_inst_id" +
            "              AND nsag.ns_ad_group_id = nsk.ns_ad_group_id" +
            "       INNER JOIN ns_campaign AS nsc" +
            "           ON nsc.prod_inst_id = nsag.prod_inst_id" +
            "              AND nsc.ns_campaign_id = nsag.ns_campaign_id " +
            "WHERE  nsk.prod_inst_id = ?" +
            "       AND nsk.`status` <> ?" +
            "       AND nsag.`status` <> ?" +
            "       AND nsc.`status` <> ?" +
            "       AND IF(?, TRUE, HOST_NAME(nsk.dest_url) IN (%1$s));";

        hostNames = coalesce(hostNames, new String[0]);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(String.format(SQL, getInClauseValuesSnippet(hostNames.length)));
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, KeywordStatus.DELETED);
            statement.setString(parameterIndex++, AdGroupStatus.DELETED);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setBoolean(parameterIndex++, arrayIsEmpty(hostNames));
            parameterIndex = setInClauseParameters(statement, parameterIndex, hostNames);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newMapOfLists(resultSet, new Factory<Pair<Long, NsKeyword>>() {
                public Pair<Long, NsKeyword> newInstance(ResultSet resultSet) throws SQLException {
                    NsKeyword nsKeyword = NsKeywordFactory.INSTANCE.newInstance(resultSet);
                    return Pair.from(Long.valueOf(nsKeyword.getNsAdGroupId()), nsKeyword);
                }});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return a map of NS ad group IDs to NS SuperPages campaigns for the specified product instance ID and optional host names.
     */
    public Map<Long, List<NsSuperPagesCampaign>> getNsAdGroupIdToNsSuperPagesCampaignMap(String logTag, Connection connection, String prodInstId, String... hostNames) throws SQLException {
        final String SQL =
            "SELECT nsag.ns_ad_group_id," +
            "       nssc.ns_superpages_campaign_id," +
            "       nssc.prod_inst_id," +
            "       nssc.ns_campaign_id," +
            "       nssc.is_local," +
            "       nssc.title," +
            "       nssc.description," +
            "       nssc.display_url," +
            "       nssc.destination_url," +
            "       nssc.street_addr_1," +
            "       nssc.street_addr_2," +
            "       nssc.city," +
            "       nssc.state," +
            "       nssc.zip," +
            "       nssc.phone_number," +
            "       nssc.email," +
            "       nssc.display_address_option," +
            "       nssc.display_phone_number," +
            "       nssc.display_email," +
            "       nssc.display_map " +
            "FROM   ns_superpages_campaign AS nssc" +
            "       INNER JOIN ns_campaign AS nsc" +
            "           ON nsc.prod_inst_id = nssc.prod_inst_id" +
            "              AND nsc.ns_campaign_id = nssc.ns_campaign_id" +
            "       INNER JOIN ns_ad_group AS nsag" +
            "           ON nsag.prod_inst_id = nsc.prod_inst_id" +
            "              AND nsag.ns_campaign_id = nsc.ns_campaign_id " +
            "WHERE  nssc.prod_inst_id = ?" +
            "       AND nsc.`status` <> ?" +
            "       AND IF(?, TRUE, HOST_NAME(nssc.destination_url) IN (%1$s));";

        hostNames = coalesce(hostNames, new String[0]);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(String.format(SQL, getInClauseValuesSnippet(hostNames.length)));
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setBoolean(parameterIndex++, arrayIsEmpty(hostNames));
            parameterIndex = setInClauseParameters(statement, parameterIndex, hostNames);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newMapOfLists(resultSet, new Factory<Pair<Long, NsSuperPagesCampaign>>() {
                public Pair<Long, NsSuperPagesCampaign> newInstance(ResultSet resultSet) throws SQLException {
                    long nsAdGroupId = resultSet.getLong("ns_ad_group_id");
                    NsSuperPagesCampaign nsSuperPages = NsSuperPagesCampaignFactory.INSTANCE.newInstance(resultSet);
                    return Pair.from(Long.valueOf(nsAdGroupId), nsSuperPages);
                }});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the NS ad group corresponding to the given prodInstId and NS ad group ID.
     */
    public NsAdGroup getNsAdGroup(String logTag, Connection connection, String prodInstId, long nsAdGroupId) throws SQLException {
        final String SQL = NsAdGroupFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND ns_ad_group_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsAdGroupFactory.INSTANCE, prodInstId, nsAdGroupId);
    }

    /**
     * Return the NS ad group corresponding to the given prodInstId, vendor ID and vendor ad group ID.
     */
    public NsAdGroup getNsAdGroup(String logTag, Connection connection, String prodInstId, int vendorId, long vendorAdGroupId, long nsCampaignId) throws SQLException {
        final String SQL = NsAdGroupFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND vendor_id = ?" +
            "       AND vendor_ad_group_id = ?" +
            "       AND ns_campaign_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsAdGroupFactory.INSTANCE, prodInstId, vendorId, vendorAdGroupId, nsCampaignId);
    }

    /**
     * Return the NS ad groups corresponding to the given NS campaign ID.
     */
    public List<NsAdGroup> getNsAdGroupsExcludingStatuses(String logTag, Connection connection, String prodInstId, long nsCampaignId, String... excludedAdGroupStatuses) throws SQLException {
        final String SQL = NsAdGroupFactory.SQL_SELECT_EXPRESSION +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_id = ?" +
            "       AND IF(? IS NULL, TRUE, vendor_id = ?)" +
            "       AND IF(?, TRUE, `status` NOT IN (%1$s));";

        return getNsEntitiesConsideringStatuses(logTag, connection, SQL, NsAdGroupFactory.INSTANCE, prodInstId, nsCampaignId, null, excludedAdGroupStatuses);
    }

    /**
     * Return the NS ad ID corresponding to the given vendor ad ID.
     */
    public Long getNsAdId(String logTag, Connection connection, NsAd nsAd) throws SQLException {
        final String SQL =
            "SELECT" +
            "  ns_ad_id " +
            "FROM" +
            "  ns_ad " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_ad_group_id = ? AND" +
            "  vendor_ad_id = ? AND" +
            "  vendor_id = ? " +
            "LIMIT 1;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsAd.getProdInstId());
            statement.setLong(parameterIndex++, nsAd.getNsAdGroupId());
            statement.setLong(parameterIndex++, nsAd.getVendorAdId());
            statement.setInt(parameterIndex++, nsAd.getVendorId());
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstNsAdId(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the vendor category ID corresponding to the given category name.
     */
    public Long getVendorCategoryId(String logTag, Connection connection, int vendorId, String categoryName) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_category_id " +
            "FROM" +
            "  vendor_category " +
            "WHERE" +
            "  `name` = ? AND" +
            "  vendor_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, categoryName);
            statement.setInt(parameterIndex++, vendorId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstVendorCategoryId(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the vendor category ID corresponding to the given description.
     */
    public Long getBaseLocationId(String logTag, Connection connection, int vendorId, String description) throws SQLException {
        final String SQL =
            "SELECT" +
            "  id " +
            "FROM" +
            "  base_locations " +
            "WHERE" +
            "  (description = ? OR description LIKE CONCAT(?, ', %')) AND" +
            "  vendor_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, description);
            statement.setString(parameterIndex++, description);
            statement.setInt(parameterIndex++, vendorId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstBaseLocationId(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the NS ads corresponding to the given NS ad group ID.
     */
    public List<NsAd> getNsAdsExcludingStatuses(String logTag, Connection connection, String prodInstId, long nsAdGroupId, String... excludedAdStatuses) throws SQLException {
        final String SQL = NsAdFactory.SQL_SELECT_EXPRESSION +
            "WHERE  nsa.prod_inst_id = ?" +
            "       AND nsa.ns_ad_group_id = ?" +
            "       AND IF(? IS NULL, TRUE, nsa.vendor_id = ?)" +
            "       AND IF(?, TRUE, nsa.`status` NOT IN (%1$s));";

        return getNsEntitiesConsideringStatuses(logTag, connection, SQL, NsAdFactory.INSTANCE, prodInstId, nsAdGroupId, null, excludedAdStatuses);
    }

    /**
     * Return a map of NS campaign IDs to NS campaign ad sitelinks extensions for the specified product instance ID.
     */
    public Map<Long, List<NsCampaignAdSitelinksExtension>> getNsCampaignIdToNsCampaignAdSitelinksExtensionMap(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL = NsCampaignAdSitelinksExtensionFactory.SQL_SELECT_EXPRESSION +
        "       INNER JOIN ns_campaign AS nsc" +
        "           ON nsc.prod_inst_id = nscase.prod_inst_id" +
        "              AND nsc.ns_campaign_id = nscase.ns_campaign_id " +
        "WHERE  nscase.prod_inst_id = ?" +
        "       AND nscase.`status` <> ?" +
        "       AND nsc.`status` <> ?;";

        Map<Long, List<NsCampaignAdSitelinksExtension>> map = null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, CampaignAdExtensionStatus.DELETED);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            map = newMapOfLists(resultSet, new Factory<Pair<Long, NsCampaignAdSitelinksExtension>>() {
                public Pair<Long, NsCampaignAdSitelinksExtension> newInstance(ResultSet resultSet) throws SQLException {
                    NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension =
                        NsCampaignAdSitelinksExtensionFactory.INSTANCE.newInstance(resultSet);
                    return Pair.from(Long.valueOf(nsCampaignAdSitelinksExtension.getNsCampaignId()), nsCampaignAdSitelinksExtension);
                }});
        }
        finally {
            close(statement, resultSet);
        }

        for (List<NsCampaignAdSitelinksExtension> nsCampaignAdSitelinksExtensions : map.values()) {
            for (NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension : nsCampaignAdSitelinksExtensions) {
                nsCampaignAdSitelinksExtension.setSitelinks(getSitelinks(logTag, connection, prodInstId, nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId()));
            }
        }

        return map;
    }

    /**
     * Return the NS campaign corresponding to the given prodInstId and NS campaign ID.
     */
    public NsCampaign getNsCampaign(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        final String SQL = NsCampaignFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nsc.prod_inst_id = ?)" +
            "       AND nsc.ns_campaign_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsCampaignFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS campaign corresponding to the given prodInstId and NS ad group ID.
     */
    public NsCampaign getNsCampaignByNsAdGroupId(String logTag, Connection connection, String prodInstId, long nsAdGroupId) throws SQLException {
        final String SQL = NsCampaignFactory.SQL_SELECT_EXPRESSION +
            "       INNER JOIN ns_ad_group AS nsag" +
            "           ON IF(? IS NULL, TRUE, nsag.prod_inst_id = nsc.prod_inst_id)" +
            "              AND nsag.ns_campaign_id = nsc.ns_campaign_id " +
            "WHERE  IF(? IS NULL, TRUE, nsag.prod_inst_id = ?)" +
            "       AND nsag.ns_ad_group_id = ?;";

        return getParentNsEntity(logTag, connection, SQL, NsCampaignFactory.INSTANCE, prodInstId, nsAdGroupId);
    }

    /**
     * Return the NS campaign corresponding to the given prodInstId, vendor ID and vendor campaign ID.
     */
    public NsCampaign getNsCampaign(String logTag, Connection connection, String prodInstId, int vendorId, long vendorCampaignId) throws SQLException {
        final String SQL = NsCampaignFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nsc.prod_inst_id = ?)" +
            "       AND nsc.vendor_id = ?" +
            "       AND nsc.vendor_campaign_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsCampaignFactory.INSTANCE, prodInstId, vendorId, vendorCampaignId);
    }

    /**
     * Return the NS campaigns corresponding to the given prodInstId.
     */
    public List<NsCampaign> getNsAdCampaignsExcludingStatuses(String logTag, Connection connection, String prodInstId, String... excludedCampaignStatuses) throws SQLException {
        final String SQL = NsCampaignFactory.SQL_SELECT_EXPRESSION +
            "       INNER JOIN vendor_service AS vs" +
            "           ON vs.vendor_id = nsc.vendor_id " +
            "WHERE  nsc.prod_inst_id = ?" +
            "       AND IF(?, TRUE, nsc.target_id <=> ?)" +
            "       AND IF(?, TRUE, nsc.`status` NOT IN (%1$s))" +
            "       AND (vs.vendor_type & ?) = ?" +
            "       AND vs.`status` = ?;";

        return getNsAdCampaignsConsideringStatuses(logTag, connection, SQL, prodInstId, IGNORE_TARGET_ID, excludedCampaignStatuses);
    }

    /**
     * Return the NS campaigns corresponding to the given prodInstId and targetId.
     */
    public List<NsCampaign> getNsAdCampaignsForTargetExcludingStatuses(String logTag, Connection connection, String prodInstId, Long targetId, String... excludedCampaignStatuses) throws SQLException {
        final String SQL = NsCampaignFactory.SQL_SELECT_EXPRESSION +
            "       INNER JOIN vendor_service AS vs" +
            "           ON vs.vendor_id = nsc.vendor_id " +
            "WHERE  nsc.prod_inst_id = ?" +
            "       AND IF(?, TRUE, nsc.target_id <=> ?)" +
            "       AND IF(?, TRUE, nsc.`status` NOT IN (%1$s))" +
            "       AND (vs.vendor_type & ?) = ?" +
            "       AND vs.`status` = ?;";

        return getNsAdCampaignsConsideringStatuses(logTag, connection, SQL, prodInstId, Option.some(targetId), excludedCampaignStatuses);
    }

    /**
     * Return the NS campaigns corresponding to the given prodInstId.
     */
    public List<NsCampaign> getNsAdCampaignsIncludingStatuses(String logTag, Connection connection, String prodInstId, String... includedCampaignStatuses) throws SQLException {
        final String SQL = NsCampaignFactory.SQL_SELECT_EXPRESSION +
            "       INNER JOIN vendor_service AS vs" +
            "           ON vs.vendor_id = nsc.vendor_id " +
            "WHERE  nsc.prod_inst_id = ?" +
            "       AND IF(?, TRUE, nsc.target_id <=> ?)" +
            "       AND IF(?, FALSE, nsc.`status` IN (%1$s))" +
            "       AND (vs.vendor_type & ?) = ?" +
            "       AND vs.`status` = ?;";

        return getNsAdCampaignsConsideringStatuses(logTag, connection, SQL, prodInstId, IGNORE_TARGET_ID, includedCampaignStatuses);
    }

    /**
     * Return the NS business location corresponding to the given prodInstId and NS business location ID.
     */
    public NsBusinessLocation getNsBusinessLocation(String logTag, Connection connection, String prodInstId, long nsBusinessLocationId) throws SQLException {
        final String SQL = NsBusinessLocationFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND ns_business_location_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsBusinessLocationFactory.INSTANCE, prodInstId, nsBusinessLocationId);
    }

    /**
     * Return the NS business location corresponding to the given prodInstId, vendor ID and vendor business location ID.
     */
    public NsBusinessLocation getNsBusinessLocation(String logTag, Connection connection, String prodInstId, int vendorId, long vendorBusinessLocationId) throws SQLException {
        final String SQL = NsBusinessLocationFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND vendor_id = ?" +
            "       AND vendor_business_location_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsBusinessLocationFactory.INSTANCE, prodInstId, vendorId, vendorBusinessLocationId);
    }

    /**
     * Return the NS business locations corresponding to the given prodInstId and vendor ID.
     */
    public List<NsBusinessLocation> getNsBusinessLocationsExcludingStatuses(String logTag, Connection connection, String prodInstId, int vendorId, String... excludedBusinessLocationStatuses) throws SQLException {
        final String SQL = NsBusinessLocationFactory.SQL_SELECT_EXPRESSION +
            "WHERE  prod_inst_id = ?" +
        	"       AND vendor_id = ?" +
            "       AND IF(?, TRUE, `status` NOT IN (%1$s));";
        excludedBusinessLocationStatuses = coalesce(excludedBusinessLocationStatuses, new String[0]);
        String sql = String.format(SQL, getInClauseValuesSnippet(excludedBusinessLocationStatuses.length));

        return newListFromParameters(logTag, connection, sql, NsBusinessLocationFactory.INSTANCE,
        		prodInstId,
        		Integer.valueOf(vendorId),
        		Boolean.valueOf(arrayIsEmpty(excludedBusinessLocationStatuses)),
                new InClauseParameters(excludedBusinessLocationStatuses));
    }

    /**
     * Return the NS campaign ad call extension corresponding to the given prodInstId and NS campaign ad call extension ID.
     */
    public NsCampaignAdCallExtension getNsCampaignAdCallExtension(String logTag, Connection connection, String prodInstId, long nsCampaignAdCallExtensionId) throws SQLException {
        final String SQL = NsCampaignAdCallExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND ns_campaign_ad_call_extension_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsCampaignAdCallExtensionFactory.INSTANCE, prodInstId, nsCampaignAdCallExtensionId);
    }

    /**
     * Return the NS campaign ad call extension corresponding to the given prodInstId, vendor ID and vendor ad extension ID.
     */
    public NsCampaignAdCallExtension getNsCampaignAdCallExtension(String logTag, Connection connection, String prodInstId, int vendorId, long vendorAdExtensionId) throws SQLException {
        final String SQL = NsCampaignAdCallExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND vendor_id = ?" +
            "       AND vendor_ad_extension_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsCampaignAdCallExtensionFactory.INSTANCE, prodInstId, vendorId, vendorAdExtensionId);
    }

    /**
     * Return the NS campaign ad call extensions corresponding to the given NS campaign ID.
     */
    public List<NsCampaignAdCallExtension> getNsCampaignAdCallExtensionsExcludingStatuses(String logTag, Connection connection, String prodInstId, long nsCampaignId, String... excludedCampaignAdExtensionStatuses) throws SQLException {
        final String SQL = NsCampaignAdCallExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_id = ?" +
            "       AND IF(? IS NULL, TRUE, vendor_id = ?)" +
            "       AND IF(?, TRUE, `status` NOT IN (%1$s));";

        return getNsEntitiesConsideringStatuses(logTag, connection, SQL, NsCampaignAdCallExtensionFactory.INSTANCE, prodInstId, nsCampaignId, null, excludedCampaignAdExtensionStatuses);
    }

    /**
     * Return the NS campaign ad location extension corresponding to the given prodInstId and NS campaign ad location extension ID.
     */
    public NsCampaignAdLocationExtension getNsCampaignAdLocationExtension(String logTag, Connection connection, String prodInstId, long nsCampaignAdLocationExtensionId) throws SQLException {
        final String SQL = NsCampaignAdLocationExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND ns_campaign_ad_location_extension_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsCampaignAdLocationExtensionFactory.INSTANCE, prodInstId, nsCampaignAdLocationExtensionId);
    }

    /**
     * Return the NS campaign ad location extension corresponding to the given prodInstId, vendor ID and vendor ad extension ID.
     */
    public NsCampaignAdLocationExtension getNsCampaignAdLocationExtension(String logTag, Connection connection, String prodInstId, int vendorId, long vendorAdExtensionId) throws SQLException {
        final String SQL = NsCampaignAdLocationExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)" +
            "       AND vendor_id = ?" +
            "       AND vendor_ad_extension_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsCampaignAdLocationExtensionFactory.INSTANCE, prodInstId, vendorId, vendorAdExtensionId);
    }

    /**
     * Return the NS campaign ad location extensions corresponding to the given NS campaign ID.
     */
    public List<NsCampaignAdLocationExtension> getNsCampaignAdLocationExtensionsExcludingStatuses(String logTag, Connection connection, String prodInstId, long nsCampaignId, String... excludedCampaignAdExtensionStatuses) throws SQLException {
        final String SQL = NsCampaignAdLocationExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_id = ?" +
            "       AND IF(? IS NULL, TRUE, vendor_id = ?)" +
            "       AND IF(?, TRUE, `status` NOT IN (%1$s));";

        return getNsEntitiesConsideringStatuses(logTag, connection, SQL, NsCampaignAdLocationExtensionFactory.INSTANCE, prodInstId, nsCampaignId, null, excludedCampaignAdExtensionStatuses);
    }

    /**
     * Return the NS campaign ad sitelinks extension corresponding to the given prodInstId and NS campaign ad sitelinks extension ID.
     */
    public NsCampaignAdSitelinksExtension getNsCampaignAdSitelinksExtension(String logTag, Connection connection, String prodInstId, long nsCampaignAdSitelinksExtensionId) throws SQLException {
        final String SQL = NsCampaignAdSitelinksExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nscase.prod_inst_id = ?)" +
            "       AND nscase.ns_campaign_ad_sitelinks_extension_id = ?;";

        NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension =
            getNsEntity(logTag, connection, SQL, NsCampaignAdSitelinksExtensionFactory.INSTANCE, prodInstId, nsCampaignAdSitelinksExtensionId);
        if (nsCampaignAdSitelinksExtension != null) {
            nsCampaignAdSitelinksExtension.setSitelinks(getSitelinks(logTag, connection, prodInstId, nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId()));
        }

        return nsCampaignAdSitelinksExtension;
    }

    /**
     * Return the NS campaign ad sitelinks extension corresponding to the given prodInstId, vendor ID and vendor ad extension ID.
     */
    public NsCampaignAdSitelinksExtension getNsCampaignAdSitelinksExtension(String logTag, Connection connection, String prodInstId, int vendorId, long vendorAdExtensionId) throws SQLException {
        final String SQL = NsCampaignAdSitelinksExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nscase.prod_inst_id = ?)" +
            "       AND nscase.vendor_id = ?" +
            "       AND nscase.vendor_ad_extension_id = ?;";

        NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension =
            getNsEntity(logTag, connection, SQL, NsCampaignAdSitelinksExtensionFactory.INSTANCE, prodInstId, vendorId, vendorAdExtensionId);
        if (nsCampaignAdSitelinksExtension != null) {
            nsCampaignAdSitelinksExtension.setSitelinks(getSitelinks(logTag, connection, prodInstId, nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId()));
        }

        return nsCampaignAdSitelinksExtension;
    }

    /**
     * Return the NS campaign ad sitelinks extensions corresponding to the given NS campaign ID.
     */
    public List<NsCampaignAdSitelinksExtension> getNsCampaignAdSitelinksExtensionsExcludingStatuses(String logTag, Connection connection, String prodInstId, long nsCampaignId, String... excludedCampaignAdExtensionStatuses) throws SQLException {
        final String SQL = NsCampaignAdSitelinksExtensionFactory.SQL_SELECT_EXPRESSION +
            "WHERE  nscase.prod_inst_id = ?" +
            "       AND nscase.ns_campaign_id = ?" +
            "       AND IF(? IS NULL, TRUE, nscase.vendor_id = ?)" +
            "       AND IF(?, TRUE, nscase.`status` NOT IN (%1$s));";

        List<NsCampaignAdSitelinksExtension> nsCampaignAdSitelinksExtensions =
            getNsEntitiesConsideringStatuses(logTag, connection, SQL, NsCampaignAdSitelinksExtensionFactory.INSTANCE, prodInstId, nsCampaignId, null, excludedCampaignAdExtensionStatuses);
        for (NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension : nsCampaignAdSitelinksExtensions) {
            nsCampaignAdSitelinksExtension.setSitelinks(getSitelinks(logTag, connection, prodInstId, nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId()));
        }

        return nsCampaignAdSitelinksExtensions;
    }

    /**
     * Return the NS campaign ad schedules corresponding to the given prodInstId and NS campaign ID.
     */
    public List<NsCampaignAdSchedule> getNsCampaignAdSchedules(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, NsCampaignAdScheduleFactory.SQL_SELECT_EXPRESSION, NsCampaignAdScheduleFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS campaign business location targets corresponding to the given prodInstId and NS campaign ID.
     */
    public List<NsCampaignBusinessLocationTarget> getNsCampaignBusinessLocationTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, NsCampaignBusinessLocationTargetFactory.SQL_SELECT_EXPRESSION, NsCampaignBusinessLocationTargetFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS campaign category ID corresponding to the given NS campaign ID and vendor category ID.
     */
    public Long getNsCampaignCategoryId(String logTag, Connection connection, String prodInstId, long nsCampaignId, long vendorCampaignId) throws SQLException {
        final String SQL =
            "SELECT ns_campaign_category_id " +
            "FROM   ns_campaign_category " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_id = ?" +
            "       AND vendor_category_id = ? " +
            // Prefer active categories.
            "ORDER  BY FIELD(`status`, ?) DESC;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setLong(parameterIndex++, nsCampaignId);
            statement.setLong(parameterIndex++, vendorCampaignId);
            statement.setString(parameterIndex++, StatusBase.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, "ns_campaign_category_id", Long.class);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the NS campaign geography ID corresponding to the given NS campaign ID and base location ID.
     */
    public Long getNsCampaignGeographyId(String logTag, Connection connection, String prodInstId, long nsCampaignId, long baseLocationId) throws SQLException {
        final String SQL =
            "SELECT ns_campaign_geography_id " +
            "FROM   ns_campaign_geography " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_id = ?" +
            "       AND base_location_id = ? " +
            // Prefer active categories.
            "ORDER  BY FIELD(`status`, ?) DESC;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setLong(parameterIndex++, nsCampaignId);
            statement.setLong(parameterIndex++, baseLocationId);
            statement.setString(parameterIndex++, StatusBase.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, "ns_campaign_geography_id", Long.class);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the NS campaign ID corresponding to the given vendor campaign ID.
     */
    public Long getNsCampaignId(String logTag, Connection connection, NsCampaign nsCampaign) throws SQLException {
        final String SQL =
            "SELECT" +
            "  ns_campaign_id " +
            "FROM" +
            "  ns_campaign " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  vendor_campaign_id = ? AND" +
            "  vendor_id = ? " +
            "LIMIT 1;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsCampaign.getProdInstId());
            statement.setLong(parameterIndex++, nsCampaign.getVendorCampaignId());
            statement.setInt(parameterIndex++, nsCampaign.getVendorId());
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstNsCampaignId(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the NS campaign ID corresponding to the given NS ad group ID.
     * The campaign cannot have DELETED status.
     */
    public Long getNsCampaignIdFromNsAdGroupId(String logTag, Connection connection, long nsAdGroupId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  ag.ns_campaign_id AS ns_campaign_id " +
            "FROM" +
            "  ns_ad_group AS ag," +
            "  ns_campaign AS c " +
            "WHERE" +
            "  ag.ns_ad_group_id = ? AND" +
            "  ag.ns_campaign_id = c.ns_campaign_id AND" +
            "  c.`status` <> ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setLong(parameterIndex++, nsAdGroupId);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstNsCampaignId(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the NS campaign language targets corresponding to the given prodInstId and NS campaign ID.
     */
    public List<Integer> getNsCampaignLanguageTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, NsCampaignLanguageTargetFactory.SQL_SELECT_EXPRESSION, NsCampaignLanguageTargetFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS campaign location targets corresponding to the given prodInstId and NS campaign ID.
     */
    public List<Integer> getNsCampaignLocationTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, NsCampaignLocationTargetFactory.SQL_SELECT_EXPRESSION, NsCampaignLocationTargetFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS campaign negative keywords corresponding to the given NS campaign ID.
     */
    public List<NsCampaignNegativeKeyword> getNsCampaignNegativeKeywords(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, NsCampaignNegativeKeywordFactory.SQL_SELECT_EXPRESSION, NsCampaignNegativeKeywordFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS campaign platform targets corresponding to the given prodInstId and NS campaign ID.
     */
    public List<Integer> getNsCampaignPlatformTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, NsCampaignPlatformTargetFactory.SQL_SELECT_EXPRESSION, NsCampaignPlatformTargetFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS campaign proximity targets corresponding to the given prodInstId and NS campaign ID.
     */
    public List<NsCampaignProximityTarget> getNsCampaignProximityTargets(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, NsCampaignProximityTargetFactory.SQL_SELECT_EXPRESSION, NsCampaignProximityTargetFactory.INSTANCE, prodInstId, nsCampaignId);
    }

    /**
     * Return the NS keyword ID corresponding to the given vendor keyword ID.
     */
    public Long getNsKeywordId(String logTag, Connection connection, NsKeyword nsKeyword) throws SQLException {
        final String SQL  =
            "SELECT" +
            "  ns_keyword_id " +
            "FROM" +
            "  ns_keyword " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_ad_group_id = ? AND" +
            "  vendor_keyword_id = ? AND" +
            "  vendor_id = ? " +
            "ORDER BY" +
            // Prefer non-deleted keywords.
            "  IF(`status` = ?, 1, 0) ASC " +
            "LIMIT 1;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsKeyword.getProdInstId());
            statement.setLong(parameterIndex++, nsKeyword.getNsAdGroupId());
            statement.setLong(parameterIndex++, nsKeyword.getVendorKeywordId());
            statement.setInt(parameterIndex++, nsKeyword.getVendorId());
            statement.setString(parameterIndex++, KeywordStatus.DELETED);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstNsKeywordId(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the NS keyword corresponding to the given prodInstId and NS keyword ID.
     */
    public NsKeyword getNsKeyword(String logTag, Connection connection, String prodInstId, long nsKeywordId) throws SQLException {
        final String SQL = NsKeywordFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nsk.prod_inst_id = ?)" +
            "       AND nsk.ns_keyword_id = ?;";

        return getNsEntity(logTag, connection, SQL, NsKeywordFactory.INSTANCE, prodInstId, nsKeywordId);
    }

    /**
     * Return the NS keyword corresponding to the given prodInstId, vendor ID and vendor keyword ID.
     */
    public NsKeyword getNsKeyword(String logTag, Connection connection, String prodInstId, int vendorId, long vendorKeywordId, long nsAdGroupId) throws SQLException {
        final String SQL = NsKeywordFactory.SQL_SELECT_EXPRESSION +
            "WHERE  IF(? IS NULL, TRUE, nsk.prod_inst_id = ?)" +
            "       AND nsk.vendor_id = ?" +
            "       AND nsk.vendor_keyword_id = ?" +
            "       AND nsk.ns_ad_group_id = ? " +
            // Prefer non-deleted keywords.
            "ORDER  BY IF(nsk.`status` = ?, 1, 0) ASC;";

        return getNsEntity(logTag, connection, SQL, NsKeywordFactory.INSTANCE, prodInstId, vendorId, vendorKeywordId, Long.valueOf(nsAdGroupId), KeywordStatus.DELETED);
    }

    /**
     * Return the NS keywords corresponding to the given NS ad group ID.
     */
    public List<NsKeyword> getNsKeywordsExcludingStatuses(String logTag, Connection connection, String prodInstId, long nsAdGroupId, String... excludedKeywordStatuses) throws SQLException {
        final String SQL = NsKeywordFactory.SQL_SELECT_EXPRESSION +
            "WHERE  nsk.prod_inst_id = ?" +
            "       AND nsk.ns_ad_group_id = ?" +
            "       AND IF(? IS NULL, TRUE, nsk.vendor_id = ?)" +
            "       AND IF(?, TRUE, nsk.`status` NOT IN (%1$s));";

        return getNsEntitiesConsideringStatuses(logTag, connection, SQL, NsKeywordFactory.INSTANCE, prodInstId, nsAdGroupId, null, excludedKeywordStatuses);
    }

    /**
     * Return the NS negative keywords corresponding to the given NS ad group ID.
     */
    public List<NsNegativeKeyword> getNsNegativeKeywordsExcludingStatuses(String logTag, Connection connection, String prodInstId, long nsAdGroupId, String... excludedNegativeKeywordStatuses) throws SQLException {
        final String SQL = NsNegativeKeywordFactory.SQL_SELECT_EXPRESSION +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_ad_group_id = ?" +
            "       AND IF(? IS NULL, TRUE, vendor_id = ?)" +
            "       AND IF(?, TRUE, `status` NOT IN (%1$s));";

        return getNsEntitiesConsideringStatuses(logTag, connection, SQL, NsNegativeKeywordFactory.INSTANCE, prodInstId, nsAdGroupId, null, excludedNegativeKeywordStatuses);
    }

    /**
     * Return the vendor campaign ID corresponding to the given campaign name.
     */
    public Long getVendorCampaignIdFromCampaignName(String logTag, Connection connection, String prodInstId, int vendorId, String campaignName) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_campaign_id " +
            "FROM" +
            "  ns_campaign " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  `name` = ? AND" +
            "  vendor_id = ? " +
            "ORDER BY" +
            // Prefer non-deleted campaigns.
            "  IF(`status` = ?, 1, 0) ASC " +
            "LIMIT 1;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, campaignName);
            statement.setInt(parameterIndex++, vendorId);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, "vendor_campaign_id", Long.class);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert an NS ad.
     */
    public void insertNsAd(String logTag, Connection connection, NsAd nsAd) throws SQLException {
        final String SQL =
            "INSERT INTO ns_ad" +
            "  (prod_inst_id, ns_ad_group_id, vendor_ad_id, vendor_id, headline, desc_1, desc_2, display_url, destination_url, ad_type," +
            "   `status`, created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
            "   ?, NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsAd.getProdInstId());
            statement.setLong(parameterIndex++, nsAd.getNsAdGroupId());
            statement.setObject(parameterIndex++, nsAd.getVendorAdId());
            statement.setInt(parameterIndex++, nsAd.getVendorId());
            statement.setString(parameterIndex++, nsAd.getHeadline());
            statement.setString(parameterIndex++, nsAd.getDescriptionLine1());
            statement.setString(parameterIndex++, nsAd.getDescriptionLine2());
            statement.setString(parameterIndex++, nsAd.getDisplayUrl());
            statement.setString(parameterIndex++, truncateDestinationUrl(nsAd.getDestinationUrl()));
            statement.setString(parameterIndex++, nsAd.getAdType());
            statement.setString(parameterIndex++, nsAd.getNsStatus());
            statement.setBoolean(parameterIndex++, nsAd.isVendorSync());
            statement.setString(parameterIndex++, nsAd.getUpdatedByUser());
            statement.setString(parameterIndex++, nsAd.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsAd.setNsAdId(nsEntityId.longValue());
            }
            nsAd.setVendorSync(false);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS ad group.
     */
    public void insertNsAdGroup(String logTag, Connection connection, NsAdGroup nsAdGroup) throws SQLException {
        final String SQL =
            "INSERT INTO ns_ad_group" +
            "  (prod_inst_id, ns_campaign_id, vendor_ad_group_id, vendor_id, `name`, `status`," +
            "   created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?," +
            "   NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsAdGroup.getProdInstId());
            statement.setLong(parameterIndex++, nsAdGroup.getNsCampaignId());
            statement.setObject(parameterIndex++, nsAdGroup.getVendorAdGroupId());
            statement.setInt(parameterIndex++, nsAdGroup.getVendorId());
            statement.setString(parameterIndex++, nsAdGroup.getName());
            statement.setString(parameterIndex++, nsAdGroup.getNsStatus());
            statement.setBoolean(parameterIndex++, nsAdGroup.isVendorSync());
            statement.setString(parameterIndex++, nsAdGroup.getUpdatedByUser());
            statement.setString(parameterIndex++, nsAdGroup.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsAdGroup.setNsAdGroupId(nsEntityId.longValue());
            }
            nsAdGroup.setVendorSync(false);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS business location.
     */
    public void insertNsBusinessLocation(String logTag, Connection connection, NsBusinessLocation nsBusinessLocation) throws SQLException {
        final String SQL =
            "INSERT INTO ns_business_location" +
            "  (prod_inst_id, vendor_id, vendor_business_location_id, `name`, `status`, geo_code_status, description," +
            "   street_addr_1, street_addr_2, city, state, zip, country_code, " +
            "   created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?," +
            "   ?, ?, ?, ?, ?, ?," +
            "   NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        Long nsEntityId = insertForParameters(logTag, connection, SQL,
                nsBusinessLocation.getProdInstId(),
                nsBusinessLocation.getVendorId(),
                nsBusinessLocation.getVendorBusinessLocationId(),
                nsBusinessLocation.getName(),
                nsBusinessLocation.getNsStatus(),
                nsBusinessLocation.getGeoCodeStatus(),
                nsBusinessLocation.getDescription(),
                nsBusinessLocation.getStreetAddress1(),
                nsBusinessLocation.getStreetAddress2(),
                nsBusinessLocation.getCity(),
                nsBusinessLocation.getState(),
                nsBusinessLocation.getZip(),
                nsBusinessLocation.getCountryCode(),
                nsBusinessLocation.isVendorSync(),
                nsBusinessLocation.getUpdatedByUser(),
                nsBusinessLocation.getUpdatedBySystem());
        if (nsEntityId != null) {
            nsBusinessLocation.setNsBusinessLocationId(nsEntityId.longValue());
        }
        nsBusinessLocation.setVendorSync(false);
    }

    /**
     * Insert an NS campaign.
     */
    public void insertNsCampaign(String logTag, Connection connection, NsCampaign nsCampaign) throws SQLException {
        final String SQL =
            "INSERT INTO ns_campaign" +
            "  (prod_inst_id, vendor_id, vendor_campaign_id, search_engine_id, target_id, `name`, `status`," +
            "   start_date, created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?," +
            "   NOW(), NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsCampaign.getProdInstId());
            statement.setInt(parameterIndex++, nsCampaign.getVendorId());
            statement.setObject(parameterIndex++, nsCampaign.getVendorCampaignId());
            statement.setObject(parameterIndex++, nsCampaign.getSearchEngineId());
            statement.setObject(parameterIndex++, nsCampaign.getTargetId());
            statement.setString(parameterIndex++, nsCampaign.getName());
            statement.setString(parameterIndex++, nsCampaign.getNsStatus());
            statement.setBoolean(parameterIndex++, nsCampaign.isVendorSync());
            statement.setString(parameterIndex++, nsCampaign.getUpdatedByUser());
            statement.setString(parameterIndex++, nsCampaign.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsCampaign.setNsCampaignId(nsEntityId.longValue());
            }
            nsCampaign.setVendorSync(false);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS SuperPage campaign.
     */
    public void insertNsSuperPageCampaign(String logTag, Connection connection, NSSuperPagesCampaignData nsSuperPageCampaign) throws SQLException {
        final String SQL =
            "INSERT INTO ns_superpages_campaign" +
            "  (prod_inst_id, ns_campaign_id, is_local, title, description, display_url," +
            "   destination_url, email, created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?, ?, " +
            "   NOW(), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, nsSuperPageCampaign.getProdInstId());
            statement.setLong(2, nsSuperPageCampaign.getNsCampaignId());
            if(nsSuperPageCampaign.getLocal().equals("true")){
                statement.setObject(3, true);
            }else{
                statement.setObject(3, false);
            }
            statement.setString(4, nsSuperPageCampaign.getCampaignTitle());
            statement.setString(5, nsSuperPageCampaign.getCampaignDescription());
            statement.setString(6, nsSuperPageCampaign.getDisplayUrl());
            statement.setString(7, nsSuperPageCampaign.getDestinationUrl());
            if(nsSuperPageCampaign.getDisplayEmailFlag().equals("true")){
                statement.setBoolean(8, true);
            }else{
                statement.setBoolean(8, false);
            }
            statement.setString(9, nsSuperPageCampaign.getUpdatedByUser());
            statement.setString(10, nsSuperPageCampaign.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsSuperPageCampaign.setNsSuperPagesCampaignId(nsEntityId.longValue());
            }
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS campaign category.
     */
    public void insertNsCampaignCategory(String logTag, Connection connection, NSCampaignCategoryData nsCampaignCategoryData) throws SQLException {
        final String SQL =
            "INSERT INTO ns_campaign_category" +
            "  (prod_inst_id, ns_campaign_id, vendor_category_id, `status`, " +
            "    created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, " +
            "   NOW(), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsCampaignCategoryData.getProdInstId());
            statement.setLong(parameterIndex++, nsCampaignCategoryData.getNsCampaignId());
            statement.setLong(parameterIndex++, nsCampaignCategoryData.getVendorCategoryId());
            statement.setString(parameterIndex++, nsCampaignCategoryData.getStatus());
            statement.setString(parameterIndex++, nsCampaignCategoryData.getUpdated_by_user());
            statement.setString(parameterIndex++, nsCampaignCategoryData.getUpdated_by_system());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsCampaignCategoryData.setNsCategoryId(nsEntityId.longValue());
            }
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS campaign category.
     */
    public void insertNsCampaignGeography(String logTag, Connection connection, NSCampaignGeoData nsCampaignGeoData) throws SQLException {
        final String SQL =
            "INSERT INTO ns_campaign_geography" +
            "  (prod_inst_id, ns_campaign_id, base_location_id, `status`, " +
            "    created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, " +
            "   NOW(), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsCampaignGeoData.getProdInstId());
            statement.setLong(parameterIndex++, nsCampaignGeoData.getNsCampaignId());
            statement.setLong(parameterIndex++, nsCampaignGeoData.getBaseLocation().getBaseLocationId());
            statement.setString(parameterIndex++, nsCampaignGeoData.getStatus());
            statement.setString(parameterIndex++, nsCampaignGeoData.getUpdated_by_user());
            statement.setString(parameterIndex++, nsCampaignGeoData.getUpdated_by_system());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsCampaignGeoData.setNsGeographyId(nsEntityId.longValue());
            }
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS campaign ad call extension.
     */
    public void insertNsCampaignAdCallExtension(String logTag, Connection connection, NsCampaignAdCallExtension nsCampaignAdCallExtension) throws SQLException {
        final String SQL =
            "INSERT INTO ns_campaign_ad_call_extension" +
            "  (prod_inst_id, ns_campaign_id, vendor_id, vendor_ad_extension_id, `status`, editorial_status," +
            "   phone_number, country_code, call_only," +
            "   created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?," +
            "   ?, ?, ?," +
            "   NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsCampaignAdCallExtension.getProdInstId());
            statement.setLong(parameterIndex++, nsCampaignAdCallExtension.getNsCampaignId());
            statement.setInt(parameterIndex++, nsCampaignAdCallExtension.getVendorId());
            statement.setObject(parameterIndex++, nsCampaignAdCallExtension.getVendorAdExtensionId());
            statement.setString(parameterIndex++, nsCampaignAdCallExtension.getNsStatus());
            statement.setString(parameterIndex++, nsCampaignAdCallExtension.getEditorialStatus());
            statement.setString(parameterIndex++, nsCampaignAdCallExtension.getPhoneNumber());
            statement.setString(parameterIndex++, nsCampaignAdCallExtension.getCountryCode());
            statement.setBoolean(parameterIndex++, nsCampaignAdCallExtension.isCallOnly());
            statement.setBoolean(parameterIndex++, nsCampaignAdCallExtension.isVendorSync());
            statement.setString(parameterIndex++, nsCampaignAdCallExtension.getUpdatedByUser());
            statement.setString(parameterIndex++, nsCampaignAdCallExtension.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsCampaignAdCallExtension.setNsCampaignAdCallExtensionId(nsEntityId.longValue());
            }
            nsCampaignAdCallExtension.setVendorSync(false);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS campaign ad location extension.
     */
    public void insertNsCampaignAdLocationExtension(String logTag, Connection connection, NsCampaignAdLocationExtension nsCampaignAdLocationExtension) throws SQLException {
        final String SQL =
            "INSERT INTO ns_campaign_ad_location_extension" +
            "  (prod_inst_id, ns_campaign_id, vendor_id, vendor_ad_extension_id, `status`, editorial_status," +
            "   business_name, street_addr_1, street_addr_2, city, state, zip, country_code, phone_number, encoded_location," +
            "   created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?," +
            "   ?, ?, ?, ?, ?, ?, ?, ?, ?," +
            "   NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getProdInstId());
            statement.setLong(parameterIndex++, nsCampaignAdLocationExtension.getNsCampaignId());
            statement.setInt(parameterIndex++, nsCampaignAdLocationExtension.getVendorId());
            statement.setObject(parameterIndex++, nsCampaignAdLocationExtension.getVendorAdExtensionId());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getNsStatus());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getEditorialStatus());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getBusinessName());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getStreetAddress1());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getStreetAddress2());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getCity());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getState());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getZip());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getCountryCode());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getPhoneNumber());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getEncodedLocation());
            statement.setBoolean(parameterIndex++, nsCampaignAdLocationExtension.isVendorSync());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getUpdatedByUser());
            statement.setString(parameterIndex++, nsCampaignAdLocationExtension.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsCampaignAdLocationExtension.setNsCampaignAdLocationExtensionId(nsEntityId.longValue());
            }
            nsCampaignAdLocationExtension.setVendorSync(false);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS campaign ad sitelinks extension.
     */
    public void insertNsCampaignAdSitelinksExtension(String logTag, Connection connection, NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension) throws SQLException {
        final String SQL =
            "INSERT INTO ns_campaign_ad_sitelinks_extension" +
            "  (prod_inst_id, ns_campaign_id, vendor_id, vendor_ad_extension_id, `status`, editorial_status," +
            "   created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?," +
            "   NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsCampaignAdSitelinksExtension.getProdInstId());
            statement.setLong(parameterIndex++, nsCampaignAdSitelinksExtension.getNsCampaignId());
            statement.setInt(parameterIndex++, nsCampaignAdSitelinksExtension.getVendorId());
            statement.setObject(parameterIndex++, nsCampaignAdSitelinksExtension.getVendorAdExtensionId());
            statement.setString(parameterIndex++, nsCampaignAdSitelinksExtension.getNsStatus());
            statement.setString(parameterIndex++, nsCampaignAdSitelinksExtension.getEditorialStatus());
            statement.setBoolean(parameterIndex++, nsCampaignAdSitelinksExtension.isVendorSync());
            statement.setString(parameterIndex++, nsCampaignAdSitelinksExtension.getUpdatedByUser());
            statement.setString(parameterIndex++, nsCampaignAdSitelinksExtension.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsCampaignAdSitelinksExtension.setNsCampaignAdSitelinksExtensionId(nsEntityId.longValue());
            }
            nsCampaignAdSitelinksExtension.setVendorSync(false);
        }
        finally {
            close(statement);
        }

        insertSitelinks(logTag, connection, nsCampaignAdSitelinksExtension);
    }

    /**
     * Insert NS campaign ad schedules.
     */
    public void insertNsCampaignAdSchedules(String logTag, Connection connection, Iterable<NsCampaignAdSchedule> nsCampaignAdSchedules) throws SQLException {
        insertAllForParameters(logTag, connection, NsCampaignAdScheduleFactory.SQL_INSERT_EXPRESSION, nsCampaignAdSchedules, NsCampaignAdScheduleFactory.INSERTER);
    }

    /**
     * Insert NS campaign business location targets.
     */
    public void insertNsCampaignBusinessLocationTargets(String logTag, Connection connection, Iterable<NsCampaignBusinessLocationTarget> nsCampaignBusinessLocationTargets) throws SQLException {
        insertAllForParameters(logTag, connection, NsCampaignBusinessLocationTargetFactory.SQL_INSERT_EXPRESSION, nsCampaignBusinessLocationTargets, NsCampaignBusinessLocationTargetFactory.INSERTER);
    }

    /**
     * Insert NS campaign language targets.
     */
    public void insertNsCampaignLanguageTargets(String logTag, Connection connection, final String prodInstId, final long nsCampaignId, Iterable<Integer> languageIds) throws SQLException {
        insertAllForParameters(logTag, connection, NsCampaignLanguageTargetFactory.SQL_INSERT_EXPRESSION, languageIds, new F1<Integer, Object[]>() {
            @Override
            public Object[] apply(Integer languageId) throws Exception {
                return new Object[] {
                        prodInstId,
                        Long.valueOf(nsCampaignId),
                        languageId
                };
            }});
    }

    /**
     * Insert NS campaign location targets.
     */
    public void insertNsCampaignLocationTargets(String logTag, Connection connection, final String prodInstId, final long nsCampaignId, Iterable<Integer> locationIds) throws SQLException {
        insertAllForParameters(logTag, connection, NsCampaignLocationTargetFactory.SQL_INSERT_EXPRESSION, locationIds, new F1<Integer, Object[]>() {
            @Override
            public Object[] apply(Integer locationId) throws Exception {
                return new Object[] {
                        prodInstId,
                        Long.valueOf(nsCampaignId),
                        locationId
                };
            }});
    }

    /**
     * Insert NS campaign negative keywords.
     */
    public void insertNsCampaignNegativeKeywords(String logTag, Connection connection, Iterable<NsCampaignNegativeKeyword> nsCampaignNegativeKeywords) throws SQLException {
        insertAllForParameters(logTag, connection, NsCampaignNegativeKeywordFactory.SQL_INSERT_EXPRESSION, nsCampaignNegativeKeywords, NsCampaignNegativeKeywordFactory.INSERTER);
    }

    /**
     * Insert NS campaign platform targets.
     */
    public void insertNsCampaignPlatformTargets(String logTag, Connection connection, final String prodInstId, final long nsCampaignId, Iterable<Integer> platformIds) throws SQLException {
        insertAllForParameters(logTag, connection, NsCampaignPlatformTargetFactory.SQL_INSERT_EXPRESSION, platformIds, new F1<Integer, Object[]>() {
            @Override
            public Object[] apply(Integer platformId) throws Exception {
                return new Object[] {
                        prodInstId,
                        Long.valueOf(nsCampaignId),
                        platformId
                };
            }});
    }

    /**
     * Insert NS campaign proximity targets.
     */
    public void insertNsCampaignProximityTargets(String logTag, Connection connection, Iterable<NsCampaignProximityTarget> nsCampaignProximityTargets) throws SQLException {
        insertAllForParameters(logTag, connection, NsCampaignProximityTargetFactory.SQL_INSERT_EXPRESSION, nsCampaignProximityTargets, NsCampaignProximityTargetFactory.INSERTER);
    }

    /**
     * Insert an NS keyword.
     */
    public void insertNsKeyword(String logTag, Connection connection, NsKeyword nsKeyword) throws SQLException {
        final String SQL =
            "INSERT INTO ns_keyword" +
            "  (prod_inst_id, ns_ad_group_id, vendor_id, vendor_keyword_id," +
            "   market_sub_category_group_id, market_geography_keyword_id, base_keyword, `status`, vendor_quality_score," +
            "   created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?," +
            "   ?, ?, ?, ?, ?," +
            "   NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsKeyword.getProdInstId());
            statement.setLong(parameterIndex++, nsKeyword.getNsAdGroupId());
            statement.setInt(parameterIndex++, nsKeyword.getVendorId());
            statement.setObject(parameterIndex++, nsKeyword.getVendorKeywordId());
            statement.setObject(parameterIndex++, nsKeyword.getMarketSubCategoryGroupId());
            statement.setObject(parameterIndex++, nsKeyword.getMarketGeographyKeywordId());
            statement.setString(parameterIndex++, nsKeyword.getBaseKeyword());
            statement.setString(parameterIndex++, nsKeyword.getNsStatus());
            statement.setLong(parameterIndex++, nsKeyword.getVendorQualityScore());
            statement.setBoolean(parameterIndex++, nsKeyword.isVendorSync());
            statement.setString(parameterIndex++, nsKeyword.getUpdatedByUser());
            statement.setString(parameterIndex++, nsKeyword.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsKeyword.setNsKeywordId(nsEntityId.longValue());
            }
            nsKeyword.setVendorSync(false);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Insert an NS negative keyword.
     */
    public void insertNsNegativeKeyword(String logTag, Connection connection, NsNegativeKeyword nsNegativeKeyword) throws SQLException {
        final String SQL =
            "INSERT INTO ns_negative_keyword" +
            "  (prod_inst_id, ns_ad_group_id, vendor_id, vendor_negative_id, market_sub_category_group_id, keyword, `status`," +
            "   created_date, last_sync_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?," +
            "   NOW(), IF(?, NOW(), DEFAULT(last_sync_date)), NOW(), ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsNegativeKeyword.getProdInstId());
            statement.setLong(parameterIndex++, nsNegativeKeyword.getNsAdGroupId());
            statement.setInt(parameterIndex++, nsNegativeKeyword.getVendorId());
            statement.setObject(parameterIndex++, nsNegativeKeyword.getVendorNegativeId());
            statement.setObject(parameterIndex++, nsNegativeKeyword.getMarketSubCategoryGroupId());
            statement.setString(parameterIndex++, nsNegativeKeyword.getKeyword());
            statement.setString(parameterIndex++, nsNegativeKeyword.getNsStatus());
            statement.setBoolean(parameterIndex++, nsNegativeKeyword.isVendorSync());
            statement.setString(parameterIndex++, nsNegativeKeyword.getUpdatedByUser());
            statement.setString(parameterIndex++, nsNegativeKeyword.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long nsEntityId = getAutoIncrementId(statement);
            if (nsEntityId != null) {
                nsNegativeKeyword.setNsNegativeId(nsEntityId.longValue());
            }
            nsNegativeKeyword.setVendorSync(false);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Are the specified ad group ID and vendor ID valid for the product instance ID?
     */
    public boolean isValidAdGroupIdAndVendorId(String logTag, Connection connection, String prodInstId, long nsAdGroupId, int vendorId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  COUNT(*) " +
            "FROM" +
            "  ns_ad_group " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_ad_group_id = ? AND" +
            "  vendor_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setLong(parameterIndex++, nsAdGroupId);
            statement.setInt(parameterIndex++, vendorId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return countGreaterThanZero(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Update an NS ad.
     */
    public void updateNsAd(String logTag, Connection connection, NsAd nsAd) throws SQLException {
        final String SQL =
            "UPDATE ns_ad " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_ad_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsAd, nsAd.getNsAdId());
    }

    /**
     * Update an ad from the vendor report.
     */
    public void updateNsAdFromVendorReport(String logTag, Connection connection, NsAd nsAd) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  ns_ad " +
            "SET" +
            "  editorial_status = ?," +
            "  desc_1 = ?," +
            "  desc_2 = ?," +
            "  display_url = ?," +
            "  destination_url = ?," +
            "  updated_date = NOW()," +
            "  updated_by_user = ?," +
            "  updated_by_system = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_ad_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, nsAd.getEditorialStatus());
            statement.setString(parameterIndex++, nsAd.getDescriptionLine1());
            statement.setString(parameterIndex++, nsAd.getDescriptionLine2());
            statement.setString(parameterIndex++, nsAd.getDisplayUrl());
            statement.setString(parameterIndex++, nsAd.getDestinationUrl());
            statement.setString(parameterIndex++, nsAd.getUpdatedByUser());
            statement.setString(parameterIndex++, nsAd.getUpdatedBySystem());
            statement.setString(parameterIndex++, nsAd.getProdInstId());
            statement.setLong(parameterIndex++, nsAd.getNsAdId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update an NS ad group.
     */
    public void updateNsAdGroup(String logTag, Connection connection, NsAdGroup nsAdGroup) throws SQLException {
        final String SQL =
            "UPDATE ns_ad_group " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_ad_group_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsAdGroup, nsAdGroup.getNsAdGroupId());
    }

    /**
     * Update an NS business location.
     */
    public void updateNsBusinessLocation(String logTag, Connection connection, NsBusinessLocation nsBusinessLocation) throws SQLException {
        final String SQL =
            "UPDATE ns_business_location " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_business_location_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsBusinessLocation, nsBusinessLocation.getNsBusinessLocationId());
    }

    /**
     * Update an NS campaign.
     */
    public void updateNsCampaign(String logTag, Connection connection, NsCampaign nsCampaign) throws SQLException {
        final String SQL =
            "UPDATE ns_campaign " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsCampaign, nsCampaign.getNsCampaignId());
    }

    /**
     * Update a campaign's % of budget value.
     */
    public void updateNsCampaignPercentOfBudget(String logTag, Connection connection, NsCampaign nsCampaign) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  ns_campaign " +
            "SET" +
            "  percent_of_budget = ?," +
            "  updated_date = NOW()," +
            "  updated_by_system = ?," +
            "  updated_by_user = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_campaign_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setDouble(parameterIndex++, nsCampaign.getPercentOfBudget());
            statement.setString(parameterIndex++, nsCampaign.getUpdatedBySystem());
            statement.setString(parameterIndex++, nsCampaign.getUpdatedByUser());
            statement.setString(parameterIndex++, nsCampaign.getProdInstId());
            statement.setLong(parameterIndex++, nsCampaign.getNsCampaignId());
            logSqlStatement(logTag, statement);
            statement.execute();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update an NS campaign ad call extension.
     */
    public void updateNsCampaignAdCallExtension(String logTag, Connection connection, NsCampaignAdCallExtension nsCampaignAdCallExtension) throws SQLException {
        final String SQL =
            "UPDATE ns_campaign_ad_call_extension " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_ad_call_extension_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsCampaignAdCallExtension, nsCampaignAdCallExtension.getNsCampaignAdCallExtensionId());
    }

    /**
     * Update an NS campaign ad location extension.
     */
    public void updateNsCampaignAdLocationExtension(String logTag, Connection connection, NsCampaignAdLocationExtension nsCampaignAdLocationExtension) throws SQLException {
        final String SQL =
            "UPDATE ns_campaign_ad_location_extension " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_ad_location_extension_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsCampaignAdLocationExtension, nsCampaignAdLocationExtension.getNsCampaignAdLocationExtensionId());
    }

    /**
     * Update an NS campaign ad sitelinks extension.
     */
    public void updateNsCampaignAdSitelinksExtension(String logTag, Connection connection, NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension) throws SQLException {
        final String SQL =
            "UPDATE ns_campaign_ad_sitelinks_extension " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_ad_sitelinks_extension_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsCampaignAdSitelinksExtension, nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId());

        // Replace all sitelinks.
        deleteSitelinks(logTag, connection, nsCampaignAdSitelinksExtension.getProdInstId(), nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId());
        insertSitelinks(logTag, connection, nsCampaignAdSitelinksExtension);
    }

    /**
     * Update an NS keyword.
     */
    public void updateNsKeyword(String logTag, Connection connection, NsKeyword nsKeyword) throws SQLException {
        final String SQL =
            "UPDATE ns_keyword " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_keyword_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsKeyword, nsKeyword.getNsKeywordId());
    }

    /**
     * Update a keyword's bid value.
     */
    public void updateNsKeywordBid(String logTag, Connection connection, NsKeyword nsKeyword) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  ns_keyword " +
            "SET" +
            "  bid = ?," +
            "  bid_cap_opt_date = NOW()," +
            "  updated_date = NOW()," +
            "  updated_by_system = ?," +
            "  updated_by_user = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_keyword_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setBigDecimal(parameterIndex++, nsKeyword.getBid());
            statement.setString(parameterIndex++, nsKeyword.getUpdatedBySystem());
            statement.setString(parameterIndex++, nsKeyword.getUpdatedByUser());
            statement.setString(parameterIndex++, nsKeyword.getProdInstId());
            statement.setLong(parameterIndex++, nsKeyword.getNsKeywordId());
            logSqlStatement(logTag, statement);
            statement.execute();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update a keyword from the vendor report.
     */
    public void updateNsKeywordFromVendorReport(String logTag, Connection connection, NsKeyword nsKeyword) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  ns_keyword " +
            "SET" +
            "  vendor_quality_score = ?," +
            "  updated_date = NOW()," +
            "  updated_by_user = ?," +
            "  updated_by_system = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  ns_keyword_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setLong(parameterIndex++, nsKeyword.getVendorQualityScore());
            statement.setString(parameterIndex++, nsKeyword.getUpdatedByUser());
            statement.setString(parameterIndex++, nsKeyword.getUpdatedBySystem());
            statement.setString(parameterIndex++, nsKeyword.getProdInstId());
            statement.setLong(parameterIndex++, nsKeyword.getNsKeywordId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update an NS negative keyword.
     */
    public void updateNsNegativeKeyword(String logTag, Connection connection, NsNegativeKeyword nsNegativeKeyword) throws SQLException {
        final String SQL =
            "UPDATE ns_negative_keyword " +
            "SET    %1$s," +
            "       last_sync_date = IF(?, NOW(), last_sync_date)," +
            "       updated_date = NOW() " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_negative_id = ?;";

        updateNsEntity(logTag, connection, SQL, nsNegativeKeyword, nsNegativeKeyword.getNsNegativeId());
    }

    /**
     * Return the NS ad campaigns corresponding to the given prodInstId.
     */
    private List<NsCampaign> getNsAdCampaignsConsideringStatuses(String logTag, Connection connection, String sql, String prodInstId, Option<Long> targetIdMaybe, String... campaignStatuses) throws SQLException {
        boolean ignoreTargetId = targetIdMaybe.isNone();
        campaignStatuses = coalesce(campaignStatuses, new String[0]);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(String.format(sql, getInClauseValuesSnippet(campaignStatuses.length)));
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setBoolean(parameterIndex++, ignoreTargetId);
            statement.setObject(parameterIndex++, ignoreTargetId ? null : targetIdMaybe.getValue());
            statement.setBoolean(parameterIndex++, arrayIsEmpty(campaignStatuses));
            parameterIndex = setInClauseParameters(statement, parameterIndex, campaignStatuses);
            statement.setInt(parameterIndex++, VendorType.AD);
            statement.setInt(parameterIndex++, VendorType.AD);
            statement.setString(parameterIndex++, VendorStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, NsCampaignFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    private int deleteNsCampaignCriteria(String logTag, Connection connection, String tableName, String prodInstId, long nsCampaignId) throws SQLException {
        return deleteNsCampaignCriteria(logTag, connection, tableName, prodInstId, Long.valueOf(nsCampaignId));
    }

    /**
     * Delete NS campaign criteria.
     */
    private int deleteNsCampaignCriteria(String logTag, Connection connection, String tableName, String prodInstId, Long nsCampaignId) throws SQLException {
        final String SQL =
            "DELETE FROM %1$s " +
            "WHERE       prod_inst_id = ?" +
            "            AND IF(? IS NULL, TRUE, ns_campaign_id = ?);";
        final String sql = String.format(SQL, tableName);

        return deleteForParameters(logTag, connection, sql,
                prodInstId,
                nsCampaignId,
                nsCampaignId);
    }

    private <T> List<T> getNsCampaignCriteria(String logTag, Connection connection, String sql, Factory<T> factory, String prodInstId, long nsCampaignId) throws SQLException {
        return getNsCampaignCriteria(logTag, connection, sql, factory, prodInstId, Long.valueOf(nsCampaignId));
    }

    /**
     * Return the NS campaign criteria corresponding to the given prodInstId and NS campaign ID.
     * The SQL is of the form:
     *  SELECT *
     *  FROM   ns_campaign_criteria
     *  WHERE  prod_inst_id = ?
     *         AND IF(? IS NULL, TRUE, ns_campaign_id = ?);
     */
    private <T> List<T> getNsCampaignCriteria(String logTag, Connection connection, String sql, Factory<T> factory, String prodInstId, Long nsCampaignId) throws SQLException {
        return newListFromParameters(logTag, connection, sql, factory,
                prodInstId,
                nsCampaignId,
                nsCampaignId);
    }

    /**
     * Return the NS entities corresponding to the given prodInstId and NS parent entity ID.
     * The SQL is of the form:
     *  SELECT *
     *  FROM   ns_entity
     *  WHERE  prod_inst_id = ?
     *         AND ns_parent_entity_id = ?
     *         AND IF(? IS NULL, TRUE, vendor_id = ?)
     *         AND IF(?, TRUE, status NOT IN (%1$s));
     * OR
     *  SELECT *
     *  FROM   ns_entity
     *  WHERE  prod_inst_id = ?
     *         AND ns_parent_entity_id = ?
     *         AND IF(? IS NULL, TRUE, vendor_id = ?)
     *         AND IF(?, FALSE, status IN (%1$s));
     */
    private <T extends NsEntity> List<T> getNsEntitiesConsideringStatuses(String logTag, Connection connection, String sql, Factory<T> factory, String prodInstId, long nsParentEntityId, Integer vendorId, String... entityStatuses) throws SQLException {
        entityStatuses = coalesce(entityStatuses, new String[0]);
        sql = String.format(sql, getInClauseValuesSnippet(entityStatuses.length));

        return newListFromParameters(logTag, connection, sql, factory,
                prodInstId,
                Long.valueOf(nsParentEntityId),
                vendorId,
                vendorId,
                Boolean.valueOf(arrayIsEmpty(entityStatuses)),
                new InClauseParameters(entityStatuses));
    }

    /**
     * Return the NS entity corresponding to the given prodInstId and NS entity ID.
     * The SQL is of the form:
     * SELECT *
     * FROM   ns_entity
     * WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)
     *        AND ns_entity_id = ?;
     */
    private <T extends NsEntity> T getNsEntity(String logTag, Connection connection, String sql, Factory<T> factory, String prodInstId, long nsEntityId) throws SQLException {
        Object[] parameters = new ArrayBuilder<Object>().add(prodInstId, prodInstId, Long.valueOf(nsEntityId)).toArray();

        return firstValueFromParameters(logTag, connection, sql, factory, parameters);
    }

    /**
     * Return the NS entity corresponding to the given prodInstId, vendor ID and vendor entity ID.
     * The SQL is of the form:
     * SELECT *
     * FROM   ns_entity
     * WHERE  IF(? IS NULL, TRUE, prod_inst_id = ?)
     *        AND vendor_id = ?
     *        AND vendor_entity_id = ?;
     */
    private <T extends NsEntity> T getNsEntity(String logTag, Connection connection, String sql, Factory<T> factory, String prodInstId, int vendorId, long vendorEntityId, long nsParentEntityId) throws SQLException {
        return getNsEntity(logTag, connection, sql, factory, prodInstId, vendorId, vendorEntityId, new Object[] {Long.valueOf(nsParentEntityId)});
    }

    private <T extends NsEntity> T getNsEntity(String logTag, Connection connection, String sql, Factory<T> factory, String prodInstId, int vendorId, long vendorEntityId, Object... values) throws SQLException {
        Object[] parameters = new ArrayBuilder<Object>().add(prodInstId, prodInstId, Integer.valueOf(vendorId), Long.valueOf(vendorEntityId)).add(values).toArray();

        return firstValueFromParameters(logTag, connection, sql, factory, parameters);
    }

    /**
     * Return the parent NS entity corresponding to the given prodInstId and NS entity ID.
     * The SQL is of the form:
     * SELECT a.*
     * FROM   ns_parent_entity AS a
     *        INNER JOIN ns_entity AS b
     *            ON IF(? IS NULL, TRUE, b.prod_inst_id = a.prod_inst_id)
     *               AND b.ns_parent_entity_id = a.ns_parent_entity_id
     * WHERE  IF(? IS NULL, TRUE, b.prod_inst_id = ?)
     *        AND b.ns_entity_id = ?;
     */
    private <T extends NsEntity> T getParentNsEntity(String logTag, Connection connection, String sql, Factory<T> factory, String prodInstId, long nsEntityId) throws SQLException {
        Object[] parameters = new ArrayBuilder<Object>().add(prodInstId, prodInstId, prodInstId, Long.valueOf(nsEntityId)).toArray();

        return firstValueFromParameters(logTag, connection, sql, factory, parameters);
    }

    /**
     * Update an NS entity.
     * The SQL is of the form:
     * UPDATE ns_entity
     * SET    %1$s,
     *        last_sync_date = IF(?, NOW(), last_sync_date),
     *        updated_date = NOW()
     * WHERE  prod_inst_id = ?
     *        AND ns_entity_id = ?;
     */
    private <T extends NsEntity> void updateNsEntity(String logTag, Connection connection, String sql, T nsEntity, long nsEntityId) throws SQLException {
        sql = String.format(sql, nsEntity.getUpdateValuesSnippet());
        updateForParameters(logTag, connection, sql,
                new UpdateParameters(nsEntity),
                Boolean.valueOf(nsEntity.isVendorSync()),
                nsEntity.getProdInstId(),
                Long.valueOf(nsEntityId));
        nsEntity.setVendorSync(false);
    }

    /**
     * Return the sitelinks for the given prodInstId and NS campaign ad sitelink extension ID.
     */
    private List<Sitelink> getSitelinks(String logTag, Connection connection, String prodInstId, long nsCampaignAdSitelinksExtensionId) throws SQLException {
        return newListFromParameters(logTag, connection, SitelinkFactory.SQL_SELECT_EXPRESSION, SitelinkFactory.INSTANCE,
                prodInstId,
                Long.valueOf(nsCampaignAdSitelinksExtensionId));
    }

    /**
     * Delete sitelinks.
     */
    private void deleteSitelinks(String logTag, Connection connection, String prodInstId, long nsCampaignAdSitelinksExtensionId) throws SQLException {
        final String SQL =
            "DELETE FROM ns_sitelink " +
            "WHERE       prod_inst_id = ?" +
            "            AND ns_campaign_ad_sitelinks_extension_id = ?;";

        deleteForParameters(logTag, connection, SQL,
                prodInstId,
                Long.valueOf(nsCampaignAdSitelinksExtensionId));
    }

    /**
     * Insert sitelinks for the specified campaign ad sitelinks extension.
     */
    private void insertSitelinks(String logTag, Connection connection, NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension) throws SQLException {
        final String SQL =
            "INSERT INTO ns_sitelink" +
            "           (prod_inst_id, ns_campaign_ad_sitelinks_extension_id, display_text, destination_url) " +
            "VALUES     (?, ?, ?, ?);";

        List<Sitelink> sitelinks = nsCampaignAdSitelinksExtension.getSitelinks();
        if (collectionIsNotEmpty(sitelinks)) {
            final long nsCampaignAdSitelinksExtensionId = nsCampaignAdSitelinksExtension.getNsCampaignAdSitelinksExtensionId();
            final String prodInstId = nsCampaignAdSitelinksExtension.getProdInstId();

            insertAll(logTag, connection, SQL, sitelinks, new ParametersSetter<Sitelink>() {
                public void setParameters(PreparedStatement statement, Sitelink sitelink) throws SQLException {
                    int parameterIndex = 1;
                    statement.setString(parameterIndex++, prodInstId);
                    statement.setLong(parameterIndex++, nsCampaignAdSitelinksExtensionId);
                    statement.setString(parameterIndex++, sitelink.getDisplayText());
                    statement.setString(parameterIndex++, sitelink.getDestinationUrl());
                }});
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsAdFactory implements Factory<NsAd> {
        public static final NsAdFactory INSTANCE = new NsAdFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT nsa.prod_inst_id," +
            "       nsa.ns_ad_id," +
            "       nsa.ns_ad_group_id," +
            "       nsa.vendor_id," +
            "       nsa.vendor_ad_id," +
            "       nsa.headline," +
            "       nsa.`status`," +
            "       nsa.editorial_status," +
            "       nsa.disapproved_reason," +
            "       nsa.desc_1," +
            "       nsa.desc_2," +
            "       nsa.display_url," +
            "       nsa.destination_url," +
            "       nsa.ad_type " +
            "FROM   ns_ad AS nsa ";

        /**
         * Constructor.
         */
        private NsAdFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsAd newInstance(ResultSet resultSet) throws SQLException {
            NsAd nsAd = new NsAd();
            nsAd.setAdType(resultSet.getString("ad_type"));
            nsAd.setDescriptionLine1(resultSet.getString("desc_1"));
            nsAd.setDescriptionLine2(resultSet.getString("desc_2"));
            nsAd.setDestinationUrl(resultSet.getString("destination_url"));
            nsAd.setDisapprovedReason(resultSet.getString("disapproved_reason"));
            nsAd.setDisplayUrl(resultSet.getString("display_url"));
            nsAd.setEditorialStatus(resultSet.getString("editorial_status"));
            nsAd.setHeadline(resultSet.getString("headline"));
            nsAd.setNsAdGroupId(resultSet.getLong("ns_ad_group_id"));
            nsAd.setNsAdId(resultSet.getLong("ns_ad_id"));
            nsAd.setNsStatus(resultSet.getString("status"));
            nsAd.setProdInstId(resultSet.getString("prod_inst_id"));
            nsAd.setVendorAdId(getLongValue(resultSet, "vendor_ad_id"));
            nsAd.setVendorId(resultSet.getInt("vendor_id"));
            return nsAd;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsAdGroupFactory implements Factory<NsAdGroup> {
        public static final NsAdGroupFactory INSTANCE = new NsAdGroupFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_ad_group_id," +
            "       ns_campaign_id," +
            "       vendor_id," +
            "       vendor_ad_group_id," +
            "       `name`," +
            "       `status`," +
            "       max_cpc," +
            "       max_content_cpc," +
            "       max_cpm," +
            "       target_conv_rate " +
            "FROM   ns_ad_group ";

        /**
         * Constructor.
         */
        private NsAdGroupFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsAdGroup newInstance(ResultSet resultSet) throws SQLException {
            NsAdGroup nsAdGroup = new NsAdGroup();
            nsAdGroup.setMaxContentCpc(getDoubleValue(resultSet, "max_content_cpc"));
            nsAdGroup.setMaxCpc(getDoubleValue(resultSet, "max_cpc"));
            nsAdGroup.setMaxCpm(getDoubleValue(resultSet, "max_cpm"));
            nsAdGroup.setName(resultSet.getString("name"));
            nsAdGroup.setNsAdGroupId(resultSet.getLong("ns_ad_group_id"));
            nsAdGroup.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsAdGroup.setNsStatus(resultSet.getString("status"));
            nsAdGroup.setProdInstId(resultSet.getString("prod_inst_id"));
            nsAdGroup.setTargetConversionRate(resultSet.getDouble("target_conv_rate"));
            nsAdGroup.setVendorAdGroupId(getLongValue(resultSet, "vendor_ad_group_id"));
            nsAdGroup.setVendorId(resultSet.getInt("vendor_id"));
            return nsAdGroup;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsBusinessLocationFactory implements Factory<NsBusinessLocation> {
        public static final NsBusinessLocationFactory INSTANCE = new NsBusinessLocationFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_business_location_id," +
            "       vendor_id," +
            "       vendor_business_location_id," +
            "       `name`," +
            "       `status`," +
            "       geo_code_status," +
            "       description," +
            "       street_addr_1," +
            "       street_addr_2," +
            "       city," +
            "       state," +
            "       zip," +
            "       country_code "+
            "FROM   ns_business_location ";

        /**
         * Constructor.
         */
        private NsBusinessLocationFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsBusinessLocation newInstance(ResultSet resultSet) throws SQLException {
            NsBusinessLocation nsBusinessLocation = new NsBusinessLocation();
            nsBusinessLocation.setCity(resultSet.getString("city"));
            nsBusinessLocation.setCountryCode(resultSet.getString("country_code"));
            nsBusinessLocation.setDescription(resultSet.getString("description"));
            nsBusinessLocation.setGeoCodeStatus(resultSet.getString("geo_code_status"));
            nsBusinessLocation.setName(resultSet.getString("name"));
            nsBusinessLocation.setNsBusinessLocationId(resultSet.getLong("ns_business_location_id"));
            nsBusinessLocation.setNsStatus(resultSet.getString("status"));
            nsBusinessLocation.setProdInstId(resultSet.getString("prod_inst_id"));
            nsBusinessLocation.setState(resultSet.getString("state"));
            nsBusinessLocation.setStreetAddress1(resultSet.getString("street_addr_1"));
            nsBusinessLocation.setStreetAddress2(resultSet.getString("street_addr_2"));
            nsBusinessLocation.setVendorBusinessLocationId(getLongValue(resultSet, "vendor_business_location_id"));
            nsBusinessLocation.setVendorId(resultSet.getInt("vendor_id"));
            nsBusinessLocation.setZip(resultSet.getString("zip"));
            return nsBusinessLocation;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignAdScheduleFactory implements Factory<NsCampaignAdSchedule> {
        public static final NsCampaignAdScheduleFactory INSTANCE = new NsCampaignAdScheduleFactory();

        public static final F1<NsCampaignAdSchedule, Object[]> INSERTER = new F1<NsCampaignAdSchedule, Object[]>() {
            @Override
            public Object[] apply(NsCampaignAdSchedule nsCampaignAdSchedule) throws Exception {
                return new Object[] {
                        nsCampaignAdSchedule.getProdInstId(),
                        Long.valueOf(nsCampaignAdSchedule.getNsCampaignId()),
                        nsCampaignAdSchedule.getDayOfWeek(),
                        Integer.valueOf(nsCampaignAdSchedule.getStartHour()),
                        Integer.valueOf(nsCampaignAdSchedule.getEndHour()),
                        Double.valueOf(nsCampaignAdSchedule.getBidModifier())
                };
            }};

        public static final String SQL_INSERT_EXPRESSION =
            "INSERT INTO ns_campaign_ad_schedule" +
            "            (prod_inst_id, ns_campaign_id, day_of_week, start_hour, end_hour, bid_modifier) " +
            "VALUES      (?, ?, ?, ?, ?, ?);";

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_campaign_id," +
            "       day_of_week," +
            "       start_hour," +
            "       end_hour," +
            "       bid_modifier " +
            "FROM   ns_campaign_ad_schedule " +
            "WHERE  prod_inst_id = ?" +
            "       AND IF(? IS NULL, TRUE, ns_campaign_id = ?);";

        /**
         * Constructor.
         */
        private NsCampaignAdScheduleFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaignAdSchedule newInstance(ResultSet resultSet) throws SQLException {
            NsCampaignAdSchedule nsCampaignAdSchedule = new NsCampaignAdSchedule();
            nsCampaignAdSchedule.setBidModifier(resultSet.getDouble("bid_modifier"));
            nsCampaignAdSchedule.setDayOfWeek(resultSet.getString("day_of_week"));
            nsCampaignAdSchedule.setEndHour(resultSet.getInt("end_hour"));
            nsCampaignAdSchedule.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaignAdSchedule.setProdInstId(resultSet.getString("prod_inst_id"));
            nsCampaignAdSchedule.setStartHour(resultSet.getInt("start_hour"));
            return nsCampaignAdSchedule;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignFactory implements Factory<NsCampaign> {
        public static final NsCampaignFactory INSTANCE = new NsCampaignFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT nsc.prod_inst_id," +
            "       nsc.ns_campaign_id," +
            "       nsc.vendor_id," +
            "       nsc.vendor_campaign_id," +
            "       nsc.search_engine_id," +
            "       nsc.target_id," +
            "       nsc.`name`," +
            "       nsc.`status`," +
            "       nsc.percent_of_budget," +
            "       nsc.network_target," +
            "       nsc.start_date," +
            "       nsc.end_date," +
            "       nsc.daily_budget," +
            "       nsc.monthly_budget," +
            "       nsc.spend_aggressiveness," +
            "       nsc.ad_rotation " +
            "FROM   ns_campaign AS nsc ";

        /**
         * Constructor.
         */
        private NsCampaignFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaign newInstance(ResultSet resultSet) throws SQLException {
            NsCampaign nsCampaign = new NsCampaign();
            nsCampaign.setAdRotation(resultSet.getString("ad_rotation"));
            nsCampaign.setDailyBudget(resultSet.getDouble("daily_budget"));
            nsCampaign.setEndDate(resultSet.getDate("end_date"));
            nsCampaign.setMonthlyBudget(getDoubleValue(resultSet, "monthly_budget"));
            nsCampaign.setName(resultSet.getString("name"));
            nsCampaign.setNetworkTarget(resultSet.getString("network_target"));
            nsCampaign.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaign.setNsStatus(resultSet.getString("status"));
            nsCampaign.setPercentOfBudget(resultSet.getFloat("percent_of_budget"));
            nsCampaign.setProdInstId(resultSet.getString("prod_inst_id"));
            nsCampaign.setSearchEngineId(getIntegerValue(resultSet, "search_engine_id"));
            nsCampaign.setSpendAggressiveness(resultSet.getFloat("spend_aggressiveness"));
            nsCampaign.setStartDate(resultSet.getDate("start_date"));
            nsCampaign.setTargetId(getLongValue(resultSet, "target_id"));
            nsCampaign.setVendorCampaignId(getLongValue(resultSet, "vendor_campaign_id"));
            nsCampaign.setVendorId(resultSet.getInt("vendor_id"));
            return nsCampaign;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignAdCallExtensionFactory implements Factory<NsCampaignAdCallExtension> {
        public static final NsCampaignAdCallExtensionFactory INSTANCE = new NsCampaignAdCallExtensionFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_campaign_ad_call_extension_id," +
            "       ns_campaign_id," +
            "       vendor_id," +
            "       vendor_ad_extension_id," +
            "       `status`," +
            "       editorial_status," +
            "       phone_number," +
            "       country_code," +
            "       call_only " +
            "FROM   ns_campaign_ad_call_extension ";

        /**
         * Constructor.
         */
        private NsCampaignAdCallExtensionFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaignAdCallExtension newInstance(ResultSet resultSet) throws SQLException {
            NsCampaignAdCallExtension nsCampaignAdCallExtension = new NsCampaignAdCallExtension();
            nsCampaignAdCallExtension.setCallOnly(resultSet.getBoolean("call_only"));
            nsCampaignAdCallExtension.setCountryCode(resultSet.getString("country_code"));
            nsCampaignAdCallExtension.setEditorialStatus(resultSet.getString("editorial_status"));
            nsCampaignAdCallExtension.setNsCampaignAdCallExtensionId(resultSet.getLong("ns_campaign_ad_call_extension_id"));
            nsCampaignAdCallExtension.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaignAdCallExtension.setNsStatus(resultSet.getString("status"));
            nsCampaignAdCallExtension.setPhoneNumber(resultSet.getString("phone_number"));
            nsCampaignAdCallExtension.setProdInstId(resultSet.getString("prod_inst_id"));
            nsCampaignAdCallExtension.setVendorAdExtensionId(getLongValue(resultSet, "vendor_ad_extension_id"));
            nsCampaignAdCallExtension.setVendorId(resultSet.getInt("vendor_id"));
            return nsCampaignAdCallExtension;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignAdLocationExtensionFactory implements Factory<NsCampaignAdLocationExtension> {
        public static final NsCampaignAdLocationExtensionFactory INSTANCE = new NsCampaignAdLocationExtensionFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_campaign_ad_location_extension_id," +
            "       ns_campaign_id," +
            "       vendor_id," +
            "       vendor_ad_extension_id," +
            "       `status`," +
            "       editorial_status," +
            "       business_name," +
            "       street_addr_1," +
            "       street_addr_2," +
            "       city," +
            "       state," +
            "       zip," +
            "       country_code," +
            "       phone_number," +
            "       encoded_location " +
            "FROM   ns_campaign_ad_location_extension ";

        /**
         * Constructor.
         */
        private NsCampaignAdLocationExtensionFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaignAdLocationExtension newInstance(ResultSet resultSet) throws SQLException {
            NsCampaignAdLocationExtension nsCampaignAdLocationExtension = new NsCampaignAdLocationExtension();
            nsCampaignAdLocationExtension.setBusinessName(resultSet.getString("business_name"));
            nsCampaignAdLocationExtension.setCity(resultSet.getString("city"));
            nsCampaignAdLocationExtension.setCountryCode(resultSet.getString("country_code"));
            nsCampaignAdLocationExtension.setEditorialStatus(resultSet.getString("editorial_status"));
            nsCampaignAdLocationExtension.setEncodedLocation(resultSet.getString("encoded_location"));
            nsCampaignAdLocationExtension.setNsCampaignAdLocationExtensionId(resultSet.getLong("ns_campaign_ad_location_extension_id"));
            nsCampaignAdLocationExtension.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaignAdLocationExtension.setNsStatus(resultSet.getString("status"));
            nsCampaignAdLocationExtension.setPhoneNumber(resultSet.getString("phone_number"));
            nsCampaignAdLocationExtension.setProdInstId(resultSet.getString("prod_inst_id"));
            nsCampaignAdLocationExtension.setState(resultSet.getString("state"));
            nsCampaignAdLocationExtension.setStreetAddress1(resultSet.getString("street_addr_1"));
            nsCampaignAdLocationExtension.setStreetAddress2(resultSet.getString("street_addr_2"));
            nsCampaignAdLocationExtension.setVendorAdExtensionId(getLongValue(resultSet, "vendor_ad_extension_id"));
            nsCampaignAdLocationExtension.setVendorId(resultSet.getInt("vendor_id"));
            nsCampaignAdLocationExtension.setZip(resultSet.getString("zip"));
            return nsCampaignAdLocationExtension;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignAdSitelinksExtensionFactory implements Factory<NsCampaignAdSitelinksExtension> {
        public static final NsCampaignAdSitelinksExtensionFactory INSTANCE = new NsCampaignAdSitelinksExtensionFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT nscase.prod_inst_id," +
            "       nscase.ns_campaign_ad_sitelinks_extension_id," +
            "       nscase.ns_campaign_id," +
            "       nscase.vendor_id," +
            "       nscase.vendor_ad_extension_id," +
            "       nscase.`status`," +
            "       nscase.editorial_status " +
            "FROM   ns_campaign_ad_sitelinks_extension AS nscase ";

        /**
         * Constructor.
         */
        private NsCampaignAdSitelinksExtensionFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaignAdSitelinksExtension newInstance(ResultSet resultSet) throws SQLException {
            NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension = new NsCampaignAdSitelinksExtension();
            nsCampaignAdSitelinksExtension.setEditorialStatus(resultSet.getString("editorial_status"));
            nsCampaignAdSitelinksExtension.setNsCampaignAdSitelinksExtensionId(resultSet.getLong("ns_campaign_ad_sitelinks_extension_id"));
            nsCampaignAdSitelinksExtension.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaignAdSitelinksExtension.setNsStatus(resultSet.getString("status"));
            nsCampaignAdSitelinksExtension.setProdInstId(resultSet.getString("prod_inst_id"));
            nsCampaignAdSitelinksExtension.setVendorAdExtensionId(getLongValue(resultSet, "vendor_ad_extension_id"));
            nsCampaignAdSitelinksExtension.setVendorId(resultSet.getInt("vendor_id"));
            return nsCampaignAdSitelinksExtension;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class SitelinkFactory implements Factory<Sitelink> {
        public static final SitelinkFactory INSTANCE = new SitelinkFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT display_text," +
            "       destination_url " +
            "FROM   ns_sitelink " +
            "WHERE  prod_inst_id = ?" +
            "       AND ns_campaign_ad_sitelinks_extension_id = ?;";

        /**
         * Constructor.
         */
        private SitelinkFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public Sitelink newInstance(ResultSet resultSet) throws SQLException {
            Sitelink sitelink = new Sitelink();
            sitelink.setDestinationUrl(resultSet.getString("destination_url"));
            sitelink.setDisplayText(resultSet.getString("display_text"));
            return sitelink;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignNegativeKeywordFactory implements Factory<NsCampaignNegativeKeyword> {
        public static final NsCampaignNegativeKeywordFactory INSTANCE = new NsCampaignNegativeKeywordFactory();

        public static final F1<NsCampaignNegativeKeyword, Object[]> INSERTER = new F1<NsCampaignNegativeKeyword, Object[]>() {
            @Override
            public Object[] apply(NsCampaignNegativeKeyword nsCampaignNegativeKeyword) throws Exception {
                return new Object[] {
                        nsCampaignNegativeKeyword.getProdInstId(),
                        Long.valueOf(nsCampaignNegativeKeyword.getNsCampaignId()),
                        nsCampaignNegativeKeyword.getKeyword(),
                        nsCampaignNegativeKeyword.getKeywordType()
                };
            }};

        public static final String SQL_INSERT_EXPRESSION =
            "INSERT INTO ns_campaign_negative_keyword" +
            "            (prod_inst_id, ns_campaign_id, keyword, type," +
            "             created_date, updated_date, updated_by_system) " +
            "VALUES      (?, ?, ?, ?," +
            // TODO Remove created_date etc.
            "             NOW(), NOW(), '')";

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_negative_id," +
            "       ns_campaign_id," +
            "       keyword," +
            "       type " +
            "FROM   ns_campaign_negative_keyword " +
            "WHERE  prod_inst_id = ?" +
            "       AND IF(? IS NULL, TRUE, ns_campaign_id = ?);";

        /**
         * Constructor.
         */
        private NsCampaignNegativeKeywordFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaignNegativeKeyword newInstance(ResultSet resultSet) throws SQLException {
            NsCampaignNegativeKeyword nsCampaignNegativeKeyword = new NsCampaignNegativeKeyword();
            nsCampaignNegativeKeyword.setKeyword(resultSet.getString("keyword"));
            nsCampaignNegativeKeyword.setKeywordType(resultSet.getString("type"));
            nsCampaignNegativeKeyword.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaignNegativeKeyword.setNsNegativeId(resultSet.getLong("ns_negative_id"));
            nsCampaignNegativeKeyword.setProdInstId(resultSet.getString("prod_inst_id"));
            return nsCampaignNegativeKeyword;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignBusinessLocationTargetFactory implements Factory<NsCampaignBusinessLocationTarget> {
        public static final NsCampaignBusinessLocationTargetFactory INSTANCE = new NsCampaignBusinessLocationTargetFactory();

        public static final F1<NsCampaignBusinessLocationTarget, Object[]> INSERTER = new F1<NsCampaignBusinessLocationTarget, Object[]>() {
            @Override
            public Object[] apply(NsCampaignBusinessLocationTarget nsCampaignBusinessLocationTarget) throws Exception {
                return new Object[] {
                        nsCampaignBusinessLocationTarget.getProdInstId(),
                        Long.valueOf(nsCampaignBusinessLocationTarget.getNsCampaignId()),
                        Long.valueOf(nsCampaignBusinessLocationTarget.getNsBusinessLocationId()),
                        Double.valueOf(nsCampaignBusinessLocationTarget.getRadius())
                };
            }};

        public static final String SQL_INSERT_EXPRESSION =
            "INSERT INTO ns_campaign_business_location_targets" +
            "            (prod_inst_id, ns_campaign_id, ns_business_location_id, radius) " +
            "VALUES      (?, ?, ?, ?);";

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_campaign_id," +
            "       ns_business_location_id," +
            "       radius " +
            "FROM   ns_campaign_business_location_targets " +
            "WHERE  prod_inst_id = ?" +
            "       AND IF(? IS NULL, TRUE, ns_campaign_id = ?);";

        /**
         * Constructor.
         */
        private NsCampaignBusinessLocationTargetFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaignBusinessLocationTarget newInstance(ResultSet resultSet) throws SQLException {
            NsCampaignBusinessLocationTarget nsCampaignBusinessLocationTarget = new NsCampaignBusinessLocationTarget();
            nsCampaignBusinessLocationTarget.setNsBusinessLocationId(resultSet.getLong("ns_business_location_id"));
            nsCampaignBusinessLocationTarget.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaignBusinessLocationTarget.setProdInstId(resultSet.getString("prod_inst_id"));
            nsCampaignBusinessLocationTarget.setRadius(resultSet.getDouble("radius"));
            return nsCampaignBusinessLocationTarget;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignLanguageTargetFactory extends IntegerFactory {
        public static final NsCampaignLanguageTargetFactory INSTANCE = new NsCampaignLanguageTargetFactory();

        public static final String SQL_INSERT_EXPRESSION =
            "INSERT INTO ns_campaign_language_targets" +
            "            (prod_inst_id, ns_campaign_id, language_id) " +
            "VALUES      (?, ?, ?);";

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT language_id " +
            "FROM   ns_campaign_language_targets " +
            "WHERE  prod_inst_id = ?" +
            "       AND IF(? IS NULL, TRUE, ns_campaign_id = ?);";

        /**
         * Constructor.
         */
        private NsCampaignLanguageTargetFactory() {
            super("language_id");
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignLocationTargetFactory extends IntegerFactory {
        public static final NsCampaignLocationTargetFactory INSTANCE = new NsCampaignLocationTargetFactory();

        public static final String SQL_INSERT_EXPRESSION =
            "INSERT INTO ns_campaign_location_targets" +
            "            (prod_inst_id, ns_campaign_id, base_location_id) " +
            "VALUES      (?, ?, ?);";

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT base_location_id " +
            "FROM   ns_campaign_location_targets " +
            "WHERE  prod_inst_id = ?" +
            "       AND IF(? IS NULL, TRUE, ns_campaign_id = ?);";

        /**
         * Constructor.
         */
        private NsCampaignLocationTargetFactory() {
            super("base_location_id");
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignPlatformTargetFactory extends IntegerFactory {
        public static final NsCampaignPlatformTargetFactory INSTANCE = new NsCampaignPlatformTargetFactory();

        public static final String SQL_INSERT_EXPRESSION =
            "INSERT INTO ns_campaign_platform_targets" +
            "            (prod_inst_id, ns_campaign_id, platform_type_id) " +
            "VALUES      (?, ?, ?);";

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT platform_type_id " +
            "FROM   ns_campaign_platform_targets " +
            "WHERE  prod_inst_id = ?" +
            "       AND IF(? IS NULL, TRUE, ns_campaign_id = ?);";

        /**
         * Constructor.
         */
        private NsCampaignPlatformTargetFactory() {
            super("platform_type_id");
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsCampaignProximityTargetFactory implements Factory<NsCampaignProximityTarget> {
        public static final NsCampaignProximityTargetFactory INSTANCE = new NsCampaignProximityTargetFactory();

        public static final F1<NsCampaignProximityTarget, Object[]> INSERTER = new F1<NsCampaignProximityTarget, Object[]>() {
            @Override
            public Object[] apply(NsCampaignProximityTarget nsCampaignProximityTarget) throws Exception {
                return new Object[] {
                        nsCampaignProximityTarget.getProdInstId(),
                        Long.valueOf(nsCampaignProximityTarget.getNsCampaignId()),
                        nsCampaignProximityTarget.getZip(),
                        Double.valueOf(nsCampaignProximityTarget.getRadius())
                };
            }};

        public static final String SQL_INSERT_EXPRESSION =
            "INSERT INTO ns_campaign_proximity_targets" +
            "            (prod_inst_id, ns_campaign_id, zip_code, radius) " +
            "VALUES      (?, ?, ?, ?);";

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT ncpt.prod_inst_id," +
            "       ncpt.ns_campaign_id," +
            "       ncpt.zip_code," +
            "       ncpt.radius," +
            "       zc.latitude," +
            "       zc.longitude " +
            "FROM   ns_campaign_proximity_targets AS ncpt" +
            "       INNER JOIN zip_codes AS zc" +
            "           ON zc.zip_code = ncpt.zip_code "+
            "WHERE  ncpt.prod_inst_id = ?" +
            "       AND IF(? IS NULL, TRUE, ncpt.ns_campaign_id = ?);";

        /**
         * Constructor.
         */
        private NsCampaignProximityTargetFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsCampaignProximityTarget newInstance(ResultSet resultSet) throws SQLException {
            NsCampaignProximityTarget nsCampaignProximityTarget = new NsCampaignProximityTarget();
            nsCampaignProximityTarget.setLatitude(resultSet.getDouble("latitude"));
            nsCampaignProximityTarget.setLongtitude(resultSet.getDouble("longitude"));
            nsCampaignProximityTarget.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsCampaignProximityTarget.setProdInstId(resultSet.getString("prod_inst_id"));
            nsCampaignProximityTarget.setRadius(resultSet.getDouble("radius"));
            nsCampaignProximityTarget.setZip(resultSet.getString("zip_code"));
            return nsCampaignProximityTarget;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsKeywordFactory implements Factory<NsKeyword> {
        public static final NsKeywordFactory INSTANCE = new NsKeywordFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT nsk.prod_inst_id," +
            "       nsk.ns_keyword_id," +
            "       nsk.ns_ad_group_id," +
            "       nsk.vendor_id," +
            "       nsk.vendor_keyword_id," +
            "       nsk.market_sub_category_group_id," +
            "       nsk.market_geography_keyword_id," +
            "       nsk.base_keyword," +
            "       nsk.location," +
            "       nsk.`status`," +
            "       nsk.editorial_status," +
            "       nsk.bid," +
            "       nsk.bid_cap," +
            "       nsk.quality_score," +
            "       nsk.min_cpc," +
            "       nsk.max_cpc," +
            "       nsk.dest_url," +
            "       nsk.type," +
            "       nsk.target_range_start," +
            "       nsk.target_range_end," +
            "       nsk.vendor_quality_score " +
            "FROM   ns_keyword AS nsk ";

        /**
         * Constructor.
         */
        private NsKeywordFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsKeyword newInstance(ResultSet resultSet) throws SQLException {
            NsKeyword nsKeyword = new NsKeyword();
            nsKeyword.setBaseKeyword(resultSet.getString("base_keyword"));
            nsKeyword.setBid(resultSet.getDouble("bid"));
            nsKeyword.setBidCap(getDoubleValue(resultSet, "bid_cap"));
            nsKeyword.setDestinationUrl(resultSet.getString("dest_url"));
            nsKeyword.setEditorialStatus(resultSet.getString("editorial_status"));
            nsKeyword.setKeywordType(resultSet.getString("type"));
            nsKeyword.setLocation(resultSet.getString("location"));
            nsKeyword.setMarketGeographyKeywordId(getLongValue(resultSet, "market_geography_keyword_id"));
            nsKeyword.setMarketSubCategoryGroupId(getLongValue(resultSet, "market_sub_category_group_id"));
            nsKeyword.setMaxCpc(getDoubleValue(resultSet, "max_cpc"));
            nsKeyword.setMinCpc(getDoubleValue(resultSet, "min_cpc"));
            nsKeyword.setNsAdGroupId(resultSet.getLong("ns_ad_group_id"));
            nsKeyword.setNsKeywordId(resultSet.getLong("ns_keyword_id"));
            nsKeyword.setNsStatus(resultSet.getString("status"));
            nsKeyword.setProdInstId(resultSet.getString("prod_inst_id"));
            nsKeyword.setQualityScore(getIntegerValue(resultSet, "quality_score"));
            nsKeyword.setTargetRangeEnd(resultSet.getInt("target_range_end"));
            nsKeyword.setTargetRangeStart(resultSet.getInt("target_range_start"));
            nsKeyword.setVendorId(resultSet.getInt("vendor_id"));
            nsKeyword.setVendorKeywordId(getLongValue(resultSet, "vendor_keyword_id"));
            nsKeyword.setVendorQualityScore(resultSet.getLong("vendor_quality_score"));
            return nsKeyword;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsNegativeKeywordFactory implements Factory<NsNegativeKeyword> {
        public static final NsNegativeKeywordFactory INSTANCE = new NsNegativeKeywordFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       ns_negative_id," +
            "       ns_ad_group_id," +
            "       vendor_id," +
            "       vendor_negative_id," +
            "       market_sub_category_group_id," +
            "       keyword," +
            "       `status` " +
            "FROM   ns_negative_keyword ";

        /**
         * Constructor.
         */
        private NsNegativeKeywordFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsNegativeKeyword newInstance(ResultSet resultSet) throws SQLException {
            NsNegativeKeyword nsNegativeKeyword = new NsNegativeKeyword();
            nsNegativeKeyword.setKeyword(resultSet.getString("keyword"));
            nsNegativeKeyword.setMarketSubCategoryGroupId(getLongValue(resultSet, "market_sub_category_group_id"));
            nsNegativeKeyword.setNsAdGroupId(resultSet.getLong("ns_ad_group_id"));
            nsNegativeKeyword.setNsNegativeId(resultSet.getLong("ns_negative_id"));
            nsNegativeKeyword.setNsStatus(resultSet.getString("status"));
            nsNegativeKeyword.setProdInstId(resultSet.getString("prod_inst_id"));
            nsNegativeKeyword.setVendorId(resultSet.getInt("vendor_id"));
            nsNegativeKeyword.setVendorNegativeId(getLongValue(resultSet, "vendor_negative_id"));
            return nsNegativeKeyword;
        }
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class NsSuperPagesCampaignFactory implements Factory<NsSuperPagesCampaign> {
        public static final NsSuperPagesCampaignFactory INSTANCE = new NsSuperPagesCampaignFactory();

        /**
         * Constructor.
         */
        private NsSuperPagesCampaignFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public NsSuperPagesCampaign newInstance(ResultSet resultSet) throws SQLException {
            NsSuperPagesCampaign nsSuperPagesCampaign = new NsSuperPagesCampaign();
            nsSuperPagesCampaign.setCity(resultSet.getString("city"));
            nsSuperPagesCampaign.setDescription(resultSet.getString("description"));
            nsSuperPagesCampaign.setDestinationUrl(resultSet.getString("destination_url"));
            nsSuperPagesCampaign.setDisplayAddressOptions(getShortValue(resultSet, "display_address_option"));
            nsSuperPagesCampaign.setDisplayEmailAddress(resultSet.getBoolean("display_email"));
            nsSuperPagesCampaign.setDisplayMap(resultSet.getBoolean("display_map"));
            nsSuperPagesCampaign.setDisplayPhoneNumber(resultSet.getBoolean("display_phone_number"));
            nsSuperPagesCampaign.setDisplayUrl(resultSet.getString("display_url"));
            nsSuperPagesCampaign.setEmailAddress(resultSet.getString("email"));
            nsSuperPagesCampaign.setLocalCampaign(resultSet.getBoolean("is_local"));
            nsSuperPagesCampaign.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            nsSuperPagesCampaign.setNsSuperPagesCampaignId(resultSet.getLong("ns_superpages_campaign_id"));
            nsSuperPagesCampaign.setPhoneNumber(resultSet.getString("phone_number"));
            nsSuperPagesCampaign.setProdInstId(resultSet.getString("prod_inst_id"));
            nsSuperPagesCampaign.setState(resultSet.getString("state"));
            nsSuperPagesCampaign.setStreetAddress1(resultSet.getString("street_addr_1"));
            nsSuperPagesCampaign.setStreetAddress2(resultSet.getString("street_addr_2"));
            nsSuperPagesCampaign.setTitle(resultSet.getString("title"));
            nsSuperPagesCampaign.setZip(resultSet.getString("zip"));
            return nsSuperPagesCampaign;
        }
    }
}
