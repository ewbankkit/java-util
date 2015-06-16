/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.InterceptorFeatures;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for Interceptor features.
 */
public class InterceptorFeaturesHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:44 InterceptorFeaturesHelper.java NSI";

    /**
     * Constructor.
     */
    public InterceptorFeaturesHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public InterceptorFeaturesHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public InterceptorFeaturesHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public InterceptorFeaturesHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Delete Interceptor features for the specified product instance ID.
     */
    public void deleteFeatures(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_features " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Return any Interceptor features for the specified product instance ID.
     */
    public InterceptorFeatures getFeatures(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  allow_empty_referrer," +
            "  rewrite_referer," +
            "  propagate_ad_params," +
            "  perform_replacements," +
            "  track_email," +
            "  track_form," +
            "  track_high_value_page," +
            "  track_shopping_cart," +
            "  collect_email_params," +
            "  collect_form_params," +
            "  collect_shopping_cart_params " +
            "FROM" +
            "  interceptor_features " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return singleValue(resultSet, InterceptorFeaturesFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert the specified Interceptor features.
     */
    public void insertFeatures(String logTag, Connection connection, InterceptorFeatures interceptorFeatures) throws SQLException {
        final String SQL =
            "INSERT INTO interceptor_features" +
            "  (prod_inst_id, allow_empty_referrer, rewrite_referer, propagate_ad_params," +
            "   perform_replacements, track_email, track_form, track_high_value_page," +
            "   track_shopping_cart, collect_email_params, collect_form_params, collect_shopping_cart_params) " +
            "VALUES" +
            "  (?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  allow_empty_referrer = VALUES(allow_empty_referrer)," +
            "  rewrite_referer = VALUES(rewrite_referer)," +
            "  propagate_ad_params = VALUES(propagate_ad_params)," +
            "  perform_replacements = VALUES(perform_replacements)," +
            "  track_email = VALUES(track_email)," +
            "  track_form = VALUES(track_form)," +
            "  track_high_value_page = VALUES(track_high_value_page)," +
            "  track_shopping_cart = VALUES(track_shopping_cart)," +
            "  collect_email_params = VALUES(collect_email_params)," +
            "  collect_form_params = VALUES(collect_form_params)," +
            "  collect_shopping_cart_params = VALUES(collect_shopping_cart_params);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, interceptorFeatures.getProdInstId());
            statement.setBoolean(2, interceptorFeatures.isAllowEmptyReferrer());
            statement.setBoolean(3, interceptorFeatures.isRewriteReferrer());
            statement.setBoolean(4, interceptorFeatures.isPropagateAdParams());
            statement.setBoolean(5, interceptorFeatures.isPerformReplacements());
            statement.setBoolean(6, interceptorFeatures.isTrackEmail());
            statement.setBoolean(7, interceptorFeatures.isTrackForm());
            statement.setBoolean(8, interceptorFeatures.isTrackHighValuePage());
            statement.setBoolean(9, interceptorFeatures.isTrackShoppingCart());
            statement.setBoolean(10, interceptorFeatures.isCollectEmailParams());
            statement.setBoolean(11, interceptorFeatures.isCollectFormParams());
            statement.setBoolean(12, interceptorFeatures.isCollectShoppingCartParams());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update the specified Interceptor features.
     */
    public void updateFeatures(String logTag, Connection connection, InterceptorFeatures interceptorFeatures) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  interceptor_features " +
            "SET" +
            "  %1$s " +
            "WHERE" +
            "  prod_inst_id = ?;";

        String sql = String.format(SQL, interceptorFeatures.getUpdateValuesSnippet());

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            int parameterIndex = interceptorFeatures.setUpdateParameters(statement, 1);
            statement.setString(parameterIndex++, interceptorFeatures.getProdInstId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create InterceptorFeatures objects from a result set.
     */
    private static class InterceptorFeaturesFactory implements Factory<InterceptorFeatures> {
        public static final InterceptorFeaturesFactory INSTANCE = new InterceptorFeaturesFactory();

        /**
         * Constructor.
         */
        private InterceptorFeaturesFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public InterceptorFeatures newInstance(ResultSet resultSet) throws SQLException {
            InterceptorFeatures interceptorFeatures = new InterceptorFeatures();
            interceptorFeatures.setAllowEmptyReferrer(resultSet.getBoolean("allow_empty_referrer"));
            interceptorFeatures.setCollectEmailParams(resultSet.getBoolean("collect_email_params"));
            interceptorFeatures.setCollectFormParams(resultSet.getBoolean("collect_form_params"));
            interceptorFeatures.setCollectShoppingCartParams(resultSet.getBoolean("collect_shopping_cart_params"));
            interceptorFeatures.setProdInstId(resultSet.getString("prod_inst_id"));
            interceptorFeatures.setPropagateAdParams(resultSet.getBoolean("propagate_ad_params"));
            interceptorFeatures.setPerformReplacements(resultSet.getBoolean("perform_replacements"));
            interceptorFeatures.setRewriteReferrer(resultSet.getBoolean("rewrite_referer"));
            interceptorFeatures.setTrackForm(resultSet.getBoolean("track_form"));
            interceptorFeatures.setTrackEmail(resultSet.getBoolean("track_email"));
            interceptorFeatures.setTrackHighValuePage(resultSet.getBoolean("track_high_value_page"));
            interceptorFeatures.setTrackShoppingCart(resultSet.getBoolean("track_shopping_cart"));
            return interceptorFeatures;
        }
    }
}
