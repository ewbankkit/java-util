/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.CallTrackingFeatures;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for call tracking features.
 */
public class CallTrackingFeaturesHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:42 CallTrackingFeaturesHelper.java NSI";

    /**
     * Constructor.
     */
    public CallTrackingFeaturesHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public CallTrackingFeaturesHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public CallTrackingFeaturesHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public CallTrackingFeaturesHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Return any call tracking features for the specified product instance ID.
     */
    public CallTrackingFeatures getCallTrackingFeatures(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  call_lead_min_duration," +
            "  track_unanswered_calls," +
            "  is_call_tracking_enabled," +
            "  is_lead_recording_enabled " +
            "FROM" +
            "  call_tracking_features " +
            "WHERE" +
            "  prod_inst_id = ?;";

        return singleValueFromProdInstId(logTag, connection, prodInstId, SQL, CallTrackingFeaturesFactory.INSTANCE);
    }

    /**
     * Insert a call tracking features record. Update the record if it already exists.
     */
    public void insertOrUpdateCallTrackingFeatures(String logTag, Connection connection, CallTrackingFeatures callTrackingFeatures) throws SQLException {
        final String SQL =
            "INSERT INTO call_tracking_features" +
            "  (prod_inst_id, call_lead_min_duration, track_unanswered_calls," +
            "   is_call_tracking_enabled, is_lead_recording_enabled," +
            "   created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, " +
            "   ?, ?," +
            "   NOW(), NOW(), ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  call_lead_min_duration = VALUES(call_lead_min_duration)," +
            "  track_unanswered_calls = VALUES(track_unanswered_calls)," +
            "  is_call_tracking_enabled = VALUES(is_call_tracking_enabled)," +
            "  is_lead_recording_enabled = VALUES(is_lead_recording_enabled)," +
            "  updated_date = NOW()," +
            "  updated_by_user = VALUES(updated_by_user)," +
            "  updated_by_system = VALUES(updated_by_system);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, callTrackingFeatures.getProdInstId());
            statement.setInt(2, callTrackingFeatures.getCallLeadMinDuration());
            statement.setBoolean(3, callTrackingFeatures.isTrackUnansweredCalls());
            statement.setBoolean(4, callTrackingFeatures.isCallTrackingEnabled());
            statement.setBoolean(5, callTrackingFeatures.isLeadRecordingEnabled());
            statement.setString(6, callTrackingFeatures.getUpdatedByUser());
            statement.setString(7, callTrackingFeatures.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create CallTrackingFeatures objects from a result set.
     */
    private static class CallTrackingFeaturesFactory implements Factory<CallTrackingFeatures> {
        public static final CallTrackingFeaturesFactory INSTANCE = new CallTrackingFeaturesFactory();

        /**
         * Constructor.
         */
        private CallTrackingFeaturesFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public CallTrackingFeatures newInstance(ResultSet resultSet) throws SQLException {
            CallTrackingFeatures callTrackingFeatures = new CallTrackingFeatures();
            callTrackingFeatures.setCallLeadMinDuration(resultSet.getInt("call_lead_min_duration"));
            callTrackingFeatures.setCallTrackingEnabled(resultSet.getBoolean("is_call_tracking_enabled"));
            callTrackingFeatures.setLeadRecordingEnabled(resultSet.getBoolean("is_lead_recording_enabled"));
            callTrackingFeatures.setProdInstId(resultSet.getString("prod_inst_id"));
            callTrackingFeatures.setTrackUnansweredCalls(resultSet.getBoolean("track_unanswered_calls"));
            return callTrackingFeatures;
        }
    }
}
