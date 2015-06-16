/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.recommendations;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.json.JsonUtil;
import com.netsol.adagent.util.recommendations.Recommendation.Status;

/**
 * Factory class for getting recommendation instances.
 *
 * @author Adam S. Vernon
 */
final class RecommendationFactory {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:25 RecommendationFactory.java NSI";
    private static final Log log = LogFactory.getLog(RecommendationFactory.class);

    /** The singleton instance. */
    private static final RecommendationFactory factory = new RecommendationFactory();

    /** Get the singleton instance. */
    static RecommendationFactory getInstance() { return factory; }

    /** Private constructor enforces the singleton pattern. */
    private RecommendationFactory() {}

    /** SQL to get all recommendations for a product. */
    private static final String GET_RECOMMENDATIONS_SQL =
        "select recommendation_id, recommendation_type_id, status, ns_campaign_id, ns_ad_group_id, ns_ad_id, ns_keyword_id, "
        + "recommendation_date, completed_date, ignored_date, recommendation_data "
        + "from recommendation where prod_inst_id=? and status != 'DELETED'";

    /**
     * Get all non-deleted recommendations for the product.
     *
     * @param pdbConn
     * @param prodInstId
     * @return the array of Recommendations
     * @throws Exception
     */
    Recommendation[] getRecommendations(Connection pdbConn, String prodInstId) throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = pdbConn.prepareStatement(GET_RECOMMENDATIONS_SQL);
            pstmt.setString(1, prodInstId);
            log.info(pstmt);
            ResultSet results = pstmt.executeQuery();
            ArrayList<Recommendation> list = new ArrayList<Recommendation>();
            while (results != null && results.next()) {
                list.add(getRecommendationFromResultSet(pdbConn, results, prodInstId));
            }
            return (Recommendation[])list.toArray(new Recommendation[list.size()]);
        }
        finally {
            BaseHelper.close(pstmt);
        }
    }

    /**
     * Get the active recommendation per entity of a given type.
     *
     * @param pdbConn
     * @param prodInstId
     * @param type
     * @return an map of recommendations indexed by entity ID or an empty map
     * @throws Exception
     */
    Map<Object, Recommendation> getActiveRecommendationsOfType(Connection pdbConn, String prodInstId, RecommendationType type) throws Exception{
        Map<Object, Recommendation> returnValue = null;
        switch (RecommendationType.EntityType.valueOf(type.getEntityType())) {
            case PRODUCT:
                returnValue = getActiveRecommendationOfTypeForProduct(pdbConn, prodInstId, type.getRecommendationTypeId());
                break;
            case CAMPAIGN:
                returnValue = getActiveRecommendationsOfTypeForCampaigns(pdbConn, prodInstId, type.getRecommendationTypeId());
                break;
            case AD_GROUP:
                returnValue = getActiveRecommendationsOfTypeForAdGroups(pdbConn, prodInstId, type.getRecommendationTypeId());
                break;
            case AD:
                returnValue = getActiveRecommendationsOfTypeForAds(pdbConn, prodInstId, type.getRecommendationTypeId());
                break;
            case KEYWORD:
                returnValue = getActiveRecommendationsOfTypeForKeywords(pdbConn, prodInstId, type.getRecommendationTypeId());
                break;
        }
        return returnValue;
    }

    //
    // Private helper methods
    //

    private static final String ACTIVE_RECOMMENDATION_OF_TYPE_FOR_PRODUCT_SQL =
        "select recommendation_id, recommendation_type_id, status, ns_campaign_id, ns_ad_group_id, ns_ad_id, ns_keyword_id, "
        + "recommendation_date, completed_date, ignored_date, recommendation_data "
        + "from recommendation where prod_inst_id=? and recommendation_type_id=? order by recommendation_date desc limit 1";

    /**
     * Get the most recent recommendations of the given type.
     *
     * @param pdbConn
     * @param prodInstId
     * @param recommendationTypeId
     * @return the recommendation or null
     * @throws Exception
     */
    private Map<Object, Recommendation> getActiveRecommendationOfTypeForProduct(Connection pdbConn, String prodInstId, Long recommendationTypeId)
        throws Exception {

        HashMap<Object, Recommendation> returnValue = new HashMap<Object, Recommendation>();
        PreparedStatement pstmt = null;
        try {
            pstmt = pdbConn.prepareStatement(ACTIVE_RECOMMENDATION_OF_TYPE_FOR_PRODUCT_SQL);
            pstmt.setString(1, prodInstId);
            pstmt.setLong(2, recommendationTypeId);
            log.info(pstmt);
            ResultSet results = pstmt.executeQuery();
            if (results != null && results.next()) {
                returnValue.put(prodInstId, getRecommendationFromResultSet(pdbConn, results, prodInstId));
            }
        }
        finally {
            BaseHelper.close(pstmt);
        }
        return returnValue;
    }

    /** SQL to get the most recent recommendations of a type an entity type. */
    private static final String ACTIVE_RECOMMENDATIONS_OF_TYPE_BASE_SQL =
        "select recommendation_id, recommendation_type_id, status, ns_campaign_id, ns_ad_group_id, ns_ad_id, ns_keyword_id, "
        + "recommendation_date, completed_date, ignored_date, recommendation_data "
        + "from recommendation r "
        + "where prod_inst_id=? and r.recommendation_type_id=? and r.recommendation_date = "
        + "(select max(r2.recommendation_date) from recommendation r2 "
        + "WHERE r2.prod_inst_id = r.prod_inst_id and r2.recommendation_type_id = r.recommendation_type_id and r.status='ACTIVE'";
    private static final String ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_CAMPAIGNS_SQL =
        ACTIVE_RECOMMENDATIONS_OF_TYPE_BASE_SQL + "and r2.ns_campaign_id = r.ns_campaign_id)";
    private static final String ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_AD_GROUPS_SQL =
        ACTIVE_RECOMMENDATIONS_OF_TYPE_BASE_SQL + "and r2.ns_ad_group_id = r.ns_ad_group_id)";
    private static final String ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_ADS_SQL          =
        ACTIVE_RECOMMENDATIONS_OF_TYPE_BASE_SQL + "and r2.ns_ad_id = r.ns_ad_id)";
    private static final String ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_KEYWORDS_SQL  =
        ACTIVE_RECOMMENDATIONS_OF_TYPE_BASE_SQL + "and r2.ns_keyword_id = r.ns_keyword_id)";

    /**
     * Get the most recent recommendations for campaigns.
     *
     * @param pdbConn
     * @param prodInstId
     * @param recommendationTypeId
     * @return an array of recommendations
     * @throws Exception
     */
    private Map<Object, Recommendation> getActiveRecommendationsOfTypeForCampaigns(Connection pdbConn, String prodInstId, Long recommendationTypeId)
        throws Exception {
        return getActiveRecommendationsOfTypeForEntity(pdbConn, prodInstId, recommendationTypeId,
                ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_CAMPAIGNS_SQL);
    }

    /**
     * Get the most recent recommendations for ad groups.
     *
     * @param pdbConn
     * @param prodInstId
     * @param recommendationTypeId
     * @return an array of recommendations
     * @throws Exception
     */
    private Map<Object, Recommendation> getActiveRecommendationsOfTypeForAdGroups(Connection pdbConn, String prodInstId, Long recommendationTypeId)
        throws Exception {
        return getActiveRecommendationsOfTypeForEntity(pdbConn, prodInstId, recommendationTypeId,
                ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_AD_GROUPS_SQL);
    }

    /**
     * Get the most recent recommendations for ads.
     *
     * @param pdbConn
     * @param prodInstId
     * @param recommendationTypeId
     * @return an map of recommendations
     * @throws Exception
     */
    private Map<Object, Recommendation> getActiveRecommendationsOfTypeForAds(Connection pdbConn, String prodInstId, Long recommendationTypeId)
        throws Exception {
        return getActiveRecommendationsOfTypeForEntity(pdbConn, prodInstId, recommendationTypeId,
                ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_ADS_SQL);
    }

    /**
     * Get the most recent recommendations for ads.
     *
     * @param pdbConn
     * @param prodInstId
     * @return an map of recommendations
     * @throws Exception
     */
    private Map<Object, Recommendation> getActiveRecommendationsOfTypeForKeywords(Connection pdbConn, String prodInstId, Long recommendationTypeId)
        throws Exception {
        return getActiveRecommendationsOfTypeForEntity(pdbConn, prodInstId, recommendationTypeId,
                ACTIVE_RECOMMENDATIONS_OF_TYPE_FOR_KEYWORDS_SQL);
    }

    /**
     * Reusable method to get the active recommendations of the given type for a particular entity type.
     *
     * @param pdbConn
     * @param prodInstId
     * @param recommendationTypeId
     * @param sql
     * @return
     * @throws Exception
     */
    private Map<Object, Recommendation> getActiveRecommendationsOfTypeForEntity(Connection pdbConn, String prodInstId, Long recommendationTypeId,
            String sql) throws Exception {

        HashMap<Object, Recommendation> returnValue = new HashMap<Object, Recommendation>();
        PreparedStatement pstmt = null;
        try {
            pstmt = pdbConn.prepareStatement(sql);
            pstmt.setString(1, prodInstId);
            pstmt.setLong(2, recommendationTypeId);
            log.info(pstmt);
            ResultSet results = pstmt.executeQuery();
            while (results != null && results.next()) {
                Recommendation recommendation = getRecommendationFromResultSet(pdbConn, results, prodInstId);
                returnValue.put(recommendation.getEntityId(), recommendation);
            }
        }
        finally {
            BaseHelper.close(pstmt);
        }
        return returnValue;
    }

    /**
     * Build a recommendation from a result set row.
     *
     * @param pdbConn
     * @param results
     * @param prodInstId
     * @return
     * @throws Exception
     */
    private Recommendation getRecommendationFromResultSet(Connection pdbConn, ResultSet results, String prodInstId) throws Exception {
        int i = 1;
        Long recommendationId = results.getLong(i++);
        Long recommendationTypeId = results.getLong(i++);
        Status status = Status.valueOf(results.getString(i++));
        Long nsCampaignId = BaseHelper.getLongValue(results, i++);
        Long nsAdGroupId = BaseHelper.getLongValue(results, i++);
        Long nsAdId = BaseHelper.getLongValue(results, i++);
        Long nsKeywordId = BaseHelper.getLongValue(results, i++);

        // recommendation_date
        java.sql.Date date = results.getDate(i++);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String recommendationDate =  CalendarUtil.calendarToString(c);

        // completed_date
        String completedDate = null;
        date = results.getDate(i++);
        if (date != null) {
            c = Calendar.getInstance();
            c.setTime(date);
            completedDate =  CalendarUtil.calendarToString(c);
        }

        // completed_date
        String ignoredDate = null;
        date = results.getDate(i++);
        if (date != null) {
            c = Calendar.getInstance();
            c.setTime(date);
            ignoredDate =  CalendarUtil.calendarToString(c);
        }

        // Create the recommendation and type objects.
        RecommendationType type = RecommendationTypeFactory.getInstance().getRecommendationType(pdbConn, recommendationTypeId);
        Recommendation recommendation = new Recommendation(recommendationId, prodInstId,  type, status, nsCampaignId,
                nsAdGroupId, nsAdId, nsKeywordId, recommendationDate, completedDate, ignoredDate);

        // Initialize the recommendation data.
        String data = results.getString(i++);
        String dataClassName = type.getDataClassName();
        RecommendationData recommendationData = null;
        if (data != null) {
            recommendationData = (RecommendationData) JsonUtil.fromJson(data, Class.forName(dataClassName));
            recommendationData.init(pdbConn, recommendation);
        }
        else if (dataClassName != null) {
            Constructor<?> constructor = Class.forName(dataClassName).getConstructor((Class[])null);
            recommendationData = (RecommendationData) constructor.newInstance((Object[])null);
            recommendationData.init(pdbConn, recommendation);
        }
        recommendation.setRecommendationData(recommendationData);

        return recommendation;
    }

    //
    // Unit test
    //

    /**
     * Simple unit test.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args){
        Connection pdbConn = null;
        try {
            // Get dev pdb2 connection.
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            pdbConn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent");
            String prodInstId= "WN-DIY-TEST9";
            System.out.println("getRecommendations:");
            RecommendationFactory rf = RecommendationFactory.getInstance();
            Recommendation[] recommendations = rf.getRecommendations(pdbConn, prodInstId);
            for (Recommendation r : recommendations) {
                System.out.println(r);
            }

            RecommendationTypeFactory rtf = RecommendationTypeFactory.getInstance();
            RecommendationType[] types = rtf.getRecommendationTypes(pdbConn);
            for (RecommendationType type : types) {
                System.out.println("getActiveRecommendationsOfType " + type.getRecommendationTypeId() + "(" + type.getName() + "):");
                Map<Object, Recommendation> map = rf.getActiveRecommendationsOfType(pdbConn, prodInstId,
                        rtf.getRecommendationType(pdbConn, type.getRecommendationTypeId()));
                Set<Object> keys = map.keySet();
                for (Object key : keys) {
                    Recommendation r = map.get(key);
                    System.out.println("\t" + key + ":" + r);
                }
            }
        }
        catch(Throwable e) {
            e.printStackTrace(System.out);
        }
        finally {
            BaseHelper.close(pdbConn);
        }
    }
}
