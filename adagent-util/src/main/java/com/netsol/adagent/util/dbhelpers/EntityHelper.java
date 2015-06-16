/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.dbhelpers.EntityHelper.EntityType.AD;
import static com.netsol.adagent.util.dbhelpers.EntityHelper.EntityType.ADGROUP;
import static com.netsol.adagent.util.dbhelpers.EntityHelper.EntityType.CAMPAIGN;
import static com.netsol.adagent.util.dbhelpers.EntityHelper.EntityType.KEYWORD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.MapBuilder;
import com.netsol.adagent.util.codes.AdGroupStatus;
import com.netsol.adagent.util.codes.AdStatus;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.GoogleAdGroupStatus;
import com.netsol.adagent.util.codes.GoogleAdStatus;
import com.netsol.adagent.util.codes.GoogleCampaignStatus;
import com.netsol.adagent.util.codes.GoogleKeywordStatus;
import com.netsol.adagent.util.codes.KeywordStatus;
import com.netsol.adagent.util.codes.MicrosoftAdGroupStatus;
import com.netsol.adagent.util.codes.MicrosoftAdStatus;
import com.netsol.adagent.util.codes.MicrosoftCampaignStatus;
import com.netsol.adagent.util.codes.MicrosoftKeywordStatus;
import com.netsol.adagent.util.codes.StatusBase;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.codes.YahooAdGroupStatus;
import com.netsol.adagent.util.codes.YahooAdStatus;
import com.netsol.adagent.util.codes.YahooCampaignStatus;
import com.netsol.adagent.util.codes.YahooKeywordStatus;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for entities.
 */
public abstract class EntityHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:43 EntityHelper.java NSI";

    private static final int MAX_DESTINATION_URL_LENGTH = 1024;
    private static final String TRUNCATION_MARKER = "...";
    private static final int TRUNCATION_MARKER_LENGTH = TRUNCATION_MARKER.length();

    // NS status to vendor status map has 3 levels:
    //  1) Vendor ID
    //  2) Entity type
    //  3) NS entity status.
    private static Map<Integer, Map<EntityType, Map<String, String>>> nsStatusToVendorStatusMap = new MapBuilder<Integer, Map<EntityType, Map<String, String>>>().
        put(Integer.valueOf(VendorId.GOOGLE), new MapBuilder<EntityType, Map<String, String>>().
            put(CAMPAIGN, new MapBuilder<String, String>().
                    // NS campaign status -> Google campaign status:
                    put(CampaignStatus.ACTIVE, GoogleCampaignStatus.ACTIVE).
                    put(CampaignStatus.DEACTIVATED, GoogleCampaignStatus.PAUSED).
                    put(CampaignStatus.MANUAL_PAUSE, GoogleCampaignStatus.PAUSED).
                    put(CampaignStatus.SYSTEM_PAUSE, GoogleCampaignStatus.PAUSED).
                    put(CampaignStatus.DELETED, GoogleCampaignStatus.DELETED).unmodifiableMap()).
            put(ADGROUP, new MapBuilder<String, String>().
                    // NS ad group status -> Google ad group status:
                    put(AdGroupStatus.ACTIVE, GoogleAdGroupStatus.ACTIVE).
                    put(AdGroupStatus.MANUAL_PAUSE, GoogleAdGroupStatus.PAUSED).
                    put(AdGroupStatus.DELETED, GoogleAdGroupStatus.DELETED).unmodifiableMap()).
            put(AD, new MapBuilder<String, String>().
                    // NS ad status -> Google ad status:
                    put(AdStatus.ACTIVE, GoogleAdStatus.ACTIVE).
                    put(AdStatus.MANUAL_PAUSE, GoogleAdStatus.PAUSED).
                    put(AdStatus.DELETED, GoogleAdStatus.DELETED).unmodifiableMap()).
            put(KEYWORD, new MapBuilder<String, String>().
                    // NS keyword status -> Google keyword status:
                    put(KeywordStatus.ACTIVE, GoogleKeywordStatus.ACTIVE).
                    put(KeywordStatus.MANUAL_PAUSE, GoogleKeywordStatus.PAUSED).
                    put(KeywordStatus.DELETED, GoogleKeywordStatus.DELETED).unmodifiableMap()).unmodifiableMap()).
        put(Integer.valueOf(VendorId.YAHOO), new MapBuilder<EntityType, Map<String, String>>().
            put(CAMPAIGN, new MapBuilder<String, String>().
                    // NS campaign status -> Yahoo campaign status:
                    put(CampaignStatus.ACTIVE, YahooCampaignStatus.ACTIVE).
                    put(CampaignStatus.DELETED, YahooCampaignStatus.DELETED).
                    put(CampaignStatus.DEACTIVATED, YahooCampaignStatus.PAUSED).
                    put(CampaignStatus.MANUAL_PAUSE, YahooCampaignStatus.PAUSED).
                    put(CampaignStatus.SYSTEM_PAUSE, YahooCampaignStatus.PAUSED).unmodifiableMap()).
            put(ADGROUP, new MapBuilder<String, String>().
                    // NS ad group status -> Yahoo ad group status:
                    put(AdGroupStatus.ACTIVE, YahooAdGroupStatus.ACTIVE).
                    put(AdGroupStatus.DELETED, YahooAdGroupStatus.DELETED).
                    put(AdGroupStatus.MANUAL_PAUSE, YahooAdGroupStatus.PAUSED).unmodifiableMap()).
            put(AD, new MapBuilder<String, String>().
                    // NS ad status -> Yahoo ad status:
                    put(AdStatus.ACTIVE, YahooAdStatus.ACTIVE).
                    put(AdStatus.DELETED, YahooAdStatus.DELETED).
                    put(AdStatus.MANUAL_PAUSE, YahooAdStatus.PAUSED).unmodifiableMap()).
            put(KEYWORD, new MapBuilder<String, String>().
                    // NS keyword status -> Yahoo keyword status:
                    put(KeywordStatus.ACTIVE, YahooKeywordStatus.ACTIVE).
                    put(KeywordStatus.DELETED, YahooKeywordStatus.DELETED).
                    put(KeywordStatus.MANUAL_PAUSE, YahooKeywordStatus.PAUSED).unmodifiableMap()).unmodifiableMap()).
        put(Integer.valueOf(VendorId.MICROSOFT), new MapBuilder<EntityType, Map<String, String>>().
            put(CAMPAIGN, new MapBuilder<String, String>().
                    // NS campaign status -> Bing campaign status:
                    put(CampaignStatus.ACTIVE, MicrosoftCampaignStatus.ACTIVE).
                    put(CampaignStatus.DELETED, MicrosoftCampaignStatus.DELETED).
                    put(CampaignStatus.DEACTIVATED, MicrosoftCampaignStatus.PAUSED).
                    put(CampaignStatus.MANUAL_PAUSE, MicrosoftCampaignStatus.PAUSED).
                    put(CampaignStatus.SYSTEM_PAUSE, MicrosoftCampaignStatus.PAUSED).unmodifiableMap()).
            put(ADGROUP, new MapBuilder<String, String>().
                    // NS ad group status -> Bing ad group status:
                    put(AdGroupStatus.ACTIVE, MicrosoftAdGroupStatus.ACTIVE).
                    put(AdGroupStatus.DELETED, MicrosoftAdGroupStatus.DELETED).
                    put(AdGroupStatus.MANUAL_PAUSE, MicrosoftAdGroupStatus.PAUSED).unmodifiableMap()).
            put(AD, new MapBuilder<String, String>().
                    // NS ad status -> Bing ad status:
                    put(AdStatus.ACTIVE, MicrosoftAdStatus.ACTIVE).
                    put(AdStatus.DELETED, MicrosoftAdStatus.DELETED).unmodifiableMap()).
            put(KEYWORD, new MapBuilder<String, String>().
                    // NS keyword status -> Bing keyword status:
                    put(KeywordStatus.ACTIVE, MicrosoftKeywordStatus.ACTIVE).
                    put(KeywordStatus.DELETED, MicrosoftKeywordStatus.DELETED).
                    put(KeywordStatus.MANUAL_PAUSE, MicrosoftKeywordStatus.PAUSED).unmodifiableMap()).unmodifiableMap()).unmodifiableMap();

    // Vendor status to NS status map has 3 levels:
    //  1) Vendor ID
    //  2) Entity type
    //  3) Vendor entity status.
    private static final Map<Integer, Map<EntityType, Map<String, String>>> vendorStatusToNsStatusMap = new MapBuilder<Integer, Map<EntityType, Map<String, String>>>().
        put(Integer.valueOf(VendorId.GOOGLE), new MapBuilder<EntityType, Map<String, String>>().
            put(CAMPAIGN, new MapBuilder<String, String>().
                    // Google campaign status -> NS campaign status:
                    put(GoogleCampaignStatus.ACTIVE, CampaignStatus.ACTIVE).
                    put(GoogleCampaignStatus.PAUSED, CampaignStatus.SYSTEM_PAUSE).
                    put(GoogleCampaignStatus.DELETED, CampaignStatus.DELETED).
                    put(GoogleCampaignStatus.ENDED, CampaignStatus.ENDED).
                    put(GoogleCampaignStatus.SUSPENDED, CampaignStatus.SUSPENDED).
                    put(GoogleCampaignStatus.PENDING, CampaignStatus.PENDING).unmodifiableMap()).
            put(ADGROUP, new MapBuilder<String, String>().
                    // Google ad group status -> NS ad group status:
                    put(GoogleAdGroupStatus.ACTIVE, AdGroupStatus.ACTIVE).
                    put(GoogleAdGroupStatus.PAUSED, AdGroupStatus.MANUAL_PAUSE).
                    put(GoogleAdGroupStatus.DELETED, AdGroupStatus.DELETED).unmodifiableMap()).
            put(AD, new MapBuilder<String, String>().
                    // Google ad status -> NS ad status:
                    put(GoogleAdStatus.ACTIVE, AdStatus.ACTIVE).
                    put(GoogleAdStatus.PAUSED, AdStatus.MANUAL_PAUSE).
                    put(GoogleAdStatus.DELETED, AdStatus.DELETED).
                    put(GoogleAdStatus.DISAPPROVED, AdStatus.DISAPPROVED).unmodifiableMap()).
            put(KEYWORD, new MapBuilder<String, String>().
                    // Google keyword status -> NS keyword status:
                    put(GoogleKeywordStatus.ACTIVE, KeywordStatus.ACTIVE).
                    put(GoogleKeywordStatus.PAUSED, KeywordStatus.MANUAL_PAUSE).
                    put(GoogleKeywordStatus.DELETED, KeywordStatus.DELETED).
                    put(GoogleKeywordStatus.DISAPPROVED, KeywordStatus.DISAPPROVED).unmodifiableMap()).unmodifiableMap()).
        put(Integer.valueOf(VendorId.YAHOO), new MapBuilder<EntityType, Map<String, String>>().
            put(CAMPAIGN, new MapBuilder<String, String>().
                    // Yahoo campaign status -> NS campaign status:
                    put(YahooCampaignStatus.ACTIVE, CampaignStatus.ACTIVE).
                    put(YahooCampaignStatus.DELETED, CampaignStatus.DELETED).
                    put(YahooCampaignStatus.PAUSED, CampaignStatus.SYSTEM_PAUSE).unmodifiableMap()).
            put(ADGROUP, new MapBuilder<String, String>().
                    // Yahoo ad group status -> NS ad group status:
                    put(YahooAdGroupStatus.ACTIVE, AdGroupStatus.ACTIVE).
                    put(YahooAdGroupStatus.DELETED, AdGroupStatus.DELETED).
                    put(YahooAdGroupStatus.PAUSED, AdGroupStatus.MANUAL_PAUSE).unmodifiableMap()).
            put(AD, new MapBuilder<String, String>().
                    // Yahoo ad status -> NS ad status:
                    put(YahooAdStatus.ACTIVE, AdStatus.ACTIVE).
                    put(YahooAdStatus.DELETED, AdStatus.DELETED).
                    put(YahooAdStatus.PAUSED, AdStatus.MANUAL_PAUSE).unmodifiableMap()).
            put(KEYWORD, new MapBuilder<String, String>().
                    // Yahoo keyword status -> NS keyword status:
                    put(YahooKeywordStatus.ACTIVE, KeywordStatus.ACTIVE).
                    put(YahooKeywordStatus.DELETED, KeywordStatus.DELETED).
                    put(YahooKeywordStatus.PAUSED, KeywordStatus.MANUAL_PAUSE).unmodifiableMap()).unmodifiableMap()).
        put(Integer.valueOf(VendorId.MICROSOFT), new MapBuilder<EntityType, Map<String, String>>().
            put(CAMPAIGN, new MapBuilder<String, String>().
                    // Bing campaign status -> NS campaign status:
                    put(MicrosoftCampaignStatus.ACTIVE, CampaignStatus.ACTIVE).
                    put(MicrosoftCampaignStatus.BUDGET_AND_MANUAL_PAUSED, CampaignStatus.SYSTEM_PAUSE).
                    put(MicrosoftCampaignStatus.BUDGET_PAUSED, CampaignStatus.SYSTEM_PAUSE).
                    put(MicrosoftCampaignStatus.DELETED, CampaignStatus.DELETED).
                    put(MicrosoftCampaignStatus.PAUSED, CampaignStatus.SYSTEM_PAUSE).unmodifiableMap()).
            put(ADGROUP, new MapBuilder<String, String>().
                    // Bing ad group status -> NS ad group status:
                    put(MicrosoftAdGroupStatus.ACTIVE, AdGroupStatus.ACTIVE).
                    put(MicrosoftAdGroupStatus.DELETED, AdGroupStatus.DELETED).
                    put(MicrosoftAdGroupStatus.DRAFT, AdGroupStatus.MANUAL_PAUSE).
                    put(MicrosoftAdGroupStatus.PAUSED, AdGroupStatus.MANUAL_PAUSE).unmodifiableMap()).
            put(AD, new MapBuilder<String, String>().
                    // Bing ad status -> NS ad status:
                    put(MicrosoftAdStatus.ACTIVE, AdStatus.ACTIVE).
                    put(MicrosoftAdStatus.DELETED, AdStatus.DELETED).
                    put(MicrosoftAdStatus.INACTIVE, AdStatus.MANUAL_PAUSE).
                    put(MicrosoftAdStatus.PAUSED, AdStatus.MANUAL_PAUSE).unmodifiableMap()).
            put(KEYWORD, new MapBuilder<String, String>().
                    // Bing keyword status -> NS keyword status:
                    put(MicrosoftKeywordStatus.ACTIVE, KeywordStatus.ACTIVE).
                    put(MicrosoftKeywordStatus.DELETED, KeywordStatus.DELETED).
                    put(MicrosoftKeywordStatus.INACTIVE, KeywordStatus.MANUAL_PAUSE).
                    put(MicrosoftKeywordStatus.PAUSED, KeywordStatus.MANUAL_PAUSE).unmodifiableMap()).unmodifiableMap()).unmodifiableMap();

    /**
     * Constructor.
     */
    protected EntityHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    protected EntityHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    protected EntityHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    protected EntityHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Constructor.
     */
    protected EntityHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Return the first NS ad group ID.
     */
    protected static Long firstNsAdGroupId(ResultSet resultSet) throws SQLException {
        return firstValue(resultSet, NsAdGroupIdFactory.INSTANCE);
    }

    /**
     * Return the first NS ad ID.
     */
    protected static Long firstNsAdId(ResultSet resultSet) throws SQLException {
        return firstValue(resultSet, NsAdIdFactory.INSTANCE);
    }

    /**
     * Return the first NS campaign ID.
     */
    protected static Long firstNsCampaignId(ResultSet resultSet) throws SQLException {
        return firstValue(resultSet, NsCampaignIdFactory.INSTANCE);
    }

    /**
     * Return the first NS keyword ID.
     */
    protected static Long firstNsKeywordId(ResultSet resultSet) throws SQLException {
        return firstValue(resultSet, NsKeywordIdFactory.INSTANCE);
    }

    /**
     * Return the first vendor category ID.
     */
    protected static Long firstVendorCategoryId(ResultSet resultSet) throws SQLException {
        return firstValue(resultSet, VendorCategoryIdFactory.INSTANCE);
    }
    
    /**
     * Return the first base location ID.
     */
    protected static Long firstBaseLocationId(ResultSet resultSet) throws SQLException {
        return firstValue(resultSet, BaseLocationIdFactory.INSTANCE);
    }
    
    
    /**
     * Return all NS ad group ID.
     */
    protected static Collection<Long> nsAdGroupIds(ResultSet resultSet) throws SQLException {
        return newList(resultSet, NsAdGroupIdFactory.INSTANCE);
    }

    /**
     * Return all NS ad ID.
     */
    protected static Collection<Long> nsAdIds(ResultSet resultSet) throws SQLException {
        return newList(resultSet, NsAdIdFactory.INSTANCE);
    }

    /**
     * Return all NS campaign ID.
     */
    protected static Collection<Long> nsCampaignIds(ResultSet resultSet) throws SQLException {
        return newList(resultSet, NsCampaignIdFactory.INSTANCE);
    }

    /**
     * Return all NS keyword ID.
     */
    protected static Collection<Long> nsKeywordIds(ResultSet resultSet) throws SQLException {
        return newList(resultSet, NsKeywordIdFactory.INSTANCE);
    }

    /**
     * Return the vendor status for the given NS status and entity type.
     */
    public static String nsStatusToVendorStatus(int vendorId, EntityType entityType, String nsStatus) {
        Map<EntityType, Map<String, String>> entityStatusMap = nsStatusToVendorStatusMap.get(Integer.valueOf(vendorId));
        if (entityStatusMap != null) {
            Map<String, String> statusMap = entityStatusMap.get(entityType);
            if (statusMap != null) {
                String vendorStatus = statusMap.get(nsStatus);
                if (nsStatus != null) {
                    return vendorStatus;
                }
            }
        }
        return nsStatus;
    }

    /**
     * Truncate a destination URL string.
     */
    public static String truncateDestinationUrl(String destinationUrl) {
        if (destinationUrl != null) {
            if (destinationUrl.length() > MAX_DESTINATION_URL_LENGTH) {
                StringBuilder sb = new StringBuilder();
                sb.append(destinationUrl, 0, MAX_DESTINATION_URL_LENGTH - TRUNCATION_MARKER_LENGTH);
                sb.append(TRUNCATION_MARKER);
                destinationUrl = sb.toString();
            }
        }
        return destinationUrl;
    }

    /**
     * Return the NS status for the given vendor status and entity type.
     */
    public static String vendorStatusToNsStatus(int vendorId, EntityType entityType, String vendorStatus) {
        Map<EntityType, Map<String, String>> entityStatusMap = vendorStatusToNsStatusMap.get(Integer.valueOf(vendorId));
        if (entityStatusMap != null) {
            Map<String, String> statusMap = entityStatusMap.get(entityType);
            if (statusMap != null) {
                String nsStatus = statusMap.get(vendorStatus);
                if (nsStatus != null) {
                    return nsStatus;
                }
            }
        }
        return StatusBase.UNKNOWN;
    }

    /**
     * Entity type.
     */
    public enum EntityType {
        CAMPAIGN,
        AD,
        ADGROUP,
        KEYWORD
    }

    /**
     * Factory class used to create an NS ad group ID from a result set.
     */
    private static class NsAdGroupIdFactory extends LongFactory {
        public static final NsAdGroupIdFactory INSTANCE = new NsAdGroupIdFactory();

        /**
         * Constructor.
         */
        private NsAdGroupIdFactory() {
            super("ns_ad_group_id");
        }
    }

    /**
     * Factory class used to create an NS ad ID from a result set.
     */
    private static class NsAdIdFactory extends LongFactory {
        public static final NsAdIdFactory INSTANCE = new NsAdIdFactory();

        /**
         * Constructor.
         */
        private NsAdIdFactory() {
            super("ns_ad_id");
        }
    }

    /**
     * Factory class used to create an vendor category ID from a result set.
     */
    private static class VendorCategoryIdFactory extends LongFactory {
        public static final VendorCategoryIdFactory INSTANCE = new VendorCategoryIdFactory();

        /**
         * Constructor.
         */
        private VendorCategoryIdFactory() {
            super("vendor_category_id");
        }
    }
    
    /**
     * Factory class used to create an vendor category ID from a result set.
     */
    private static class BaseLocationIdFactory extends LongFactory {
        public static final BaseLocationIdFactory INSTANCE = new BaseLocationIdFactory();

        /**
         * Constructor.
         */
        private BaseLocationIdFactory() {
            super("id");
        }
    }
    
    /**
     * Factory class used to create an NS campaign ID from a result set.
     */
    private static class NsCampaignIdFactory extends LongFactory {
        public static final NsCampaignIdFactory INSTANCE = new NsCampaignIdFactory();

        /**
         * Constructor.
         */
        private NsCampaignIdFactory() {
            super("ns_campaign_id");
        }
    }

    /**
     * Factory class used to create an NS keyword ID from a result set.
     */
    private static class NsKeywordIdFactory extends LongFactory {
        public static final NsKeywordIdFactory INSTANCE = new NsKeywordIdFactory();

        /**
         * Constructor.
         */
        private NsKeywordIdFactory() {
            super("ns_keyword_id");
        }
    }

    /**
     * Factory class used to create a vendor ID from a result set.
     */
    protected static class VendorIdFactory extends IntegerFactory {
        public static final VendorIdFactory INSTANCE = new VendorIdFactory();

        /**
         * Constructor.
         */
        private VendorIdFactory() {
            super("vendor_id");
        }
    }
}
