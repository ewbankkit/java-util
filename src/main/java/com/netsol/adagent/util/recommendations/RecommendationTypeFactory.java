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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.json.JsonUtil;
import com.netsol.adagent.util.recommendations.RecommendationType.Category;
import com.netsol.adagent.util.recommendations.RecommendationType.EntityType;

/**
 * Factory singleton for RecommendationType objects. Data is not cached.
 *
 * @author Adam S. Vernon
 */
class RecommendationTypeFactory {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:25 RecommendationTypeFactory.java NSI";
    private static final Log log = LogFactory.getLog(RecommendationTypeFactory.class);

    /** The singleton instance. */
    private static final RecommendationTypeFactory factory = new RecommendationTypeFactory();

    /** Get the singleton instance. */
    public static RecommendationTypeFactory getInstance() { return factory; }

    /** Private constructor enforces the singleton pattern. */
    private RecommendationTypeFactory() {}

    /** SQL query to get all recommendation types. */
    private static final String GET_RECOMMENDATION_TYPES_SQL =
        "select recommendation_type_id, name, is_enabled, recommendation_category, entity_type, priority, frequency_days, recommendation_type_data, type_class_name, data_class_name "
        + "from recommendation_type where is_enabled=1";

    /** SQL query to get a single recommendation type id ID. */
    private static final String GET_RECOMMENDATION_TYPE_SQL =
        "select recommendation_type_id, name, is_enabled, recommendation_category, entity_type, priority, frequency_days, recommendation_type_data, type_class_name, data_class_name "
        + " from recommendation_type where recommendation_type_id=?";

    /**
     * Get a recommendation type by ID.
     *
     * @param conn
     * @param recommendationTypeId
     * @return
     * @throws Exception
     */
    RecommendationType getRecommendationType(Connection conn, Long recommendationTypeId) throws Exception {
        PreparedStatement pstmt = null;
        RecommendationType recommendationType = null;
        try {
            pstmt = conn.prepareStatement(GET_RECOMMENDATION_TYPE_SQL);
            pstmt.setLong(1, recommendationTypeId);
            log.info(pstmt);
            ResultSet results = pstmt.executeQuery();
            if (results != null && results.next()) {
                recommendationType = getRecommendationTypeFromResultSet(results);
            }
        }
        finally {
            BaseHelper.close(pstmt);
        }
        return recommendationType;
    }

    /**
     * Get all the enabled recommendation types.
     *
     * @param conn
     * @param recommendationTypeId
     * @return
     * @throws Exception
     */
    RecommendationType[] getRecommendationTypes(Connection conn) throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(GET_RECOMMENDATION_TYPES_SQL);
            log.info(pstmt);
            ResultSet results = pstmt.executeQuery();
            ArrayList<RecommendationType> list = new ArrayList<RecommendationType>();
            while (results != null && results.next()) {
                list.add(getRecommendationTypeFromResultSet(results));
            }
            return (RecommendationType[])list.toArray(new RecommendationType[list.size()]);
        }
        finally {
            BaseHelper.close(pstmt);
        }
    }

    //
    // Private helper methods:
    //

    /**
     * Reusable method to create a RecommendationType object from a result set row.
     *
     * @param results
     */
    private RecommendationType getRecommendationTypeFromResultSet(ResultSet results) throws Exception {
        int i = 1;
        Long recommendationTypeId = results.getLong(i++);
        String name = results.getString(i++);
        Boolean isEnabled = results.getBoolean(i++);
        Category category = Category.valueOf(results.getString(i++));
        EntityType entityType = EntityType.valueOf(results.getString(i++));
        Integer priority = results.getInt(i++);
        Integer frequencyDays = results.getInt(i++);
        String data = results.getString(i++);
        String typeClassName = results.getString(i++);
        String dataClassName = results.getString(i++);

        RecommendationType recommendationType = null;
        if (data != null) {
            recommendationType = (RecommendationType) JsonUtil.fromJson(data, Class.forName(typeClassName));
        }
        else {
            Constructor<?> constructor = Class.forName(typeClassName).getConstructor((Class[])null);
            recommendationType = (RecommendationType) constructor.newInstance((Object[])null);
        }
        recommendationType.setRecommendationTypeId(recommendationTypeId);
        recommendationType.setName(name);
        recommendationType.setIsEnabled(isEnabled);
        recommendationType.setCategory(category.toString());
        recommendationType.setEntityType(entityType.toString());
        recommendationType.setPriority(priority);
        recommendationType.setFrequencyDays(frequencyDays);
        recommendationType.setTypeClassName(typeClassName);
        recommendationType.setDataClassName(dataClassName);
        return recommendationType;
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
        Connection gdbConn = null;
        try {
            // Get dev gdb connection.
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            gdbConn = DriverManager.getConnection("jdbc:mysql://eng1.dev.netsol.com:4200/adagent?user=adagent&password=adagent");

            RecommendationTypeFactory rtf = RecommendationTypeFactory.getInstance();
            System.out.println("getRecommendationTypes:");
            RecommendationType[] types = rtf.getRecommendationTypes(gdbConn);
            for (int i = 0; i < types.length; i++) {
                System.out.println(types[i]);
            }
        }
        catch(Throwable e) {
            e.printStackTrace(System.out);
        }
        finally {
            BaseHelper.close(gdbConn);
        }
    }
}
