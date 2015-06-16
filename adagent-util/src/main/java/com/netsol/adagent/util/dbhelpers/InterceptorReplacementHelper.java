/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.InterceptorReplacement;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for Interceptor replacements.
 */
public class InterceptorReplacementHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:44 InterceptorReplacementHelper.java NSI";

    /**
     * Constructor.
     */
    public InterceptorReplacementHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public InterceptorReplacementHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public InterceptorReplacementHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Delete the specified Interceptor replacement.
     */
    public void deleteReplacement(String logTag, Connection connection, InterceptorReplacement interceptorReplacement) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  replacement = ? AND" +
            "  replacement_type = ? AND" +
            "  limit_id = ? AND" +
            "  original_regex = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, interceptorReplacement.getProdInstId());
            statement.setString(2, interceptorReplacement.getReplacementText());
            statement.setString(3, interceptorReplacement.getReplacementType());
            statement.setLong(4, interceptorReplacement.getLimitId());
            statement.setString(5, interceptorReplacement.getOriginalRegex());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Delete Interceptor replacements for the specified product instance ID, vendor ID and vendor entity ID.
     */
    public void deleteReplacements(String logTag, Connection connection, String prodInstId, int vendorId, long vendorEntityId) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  vendor_id = ? AND" +
            "  vendor_entity_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setInt(2, vendorId);
            statement.setLong(3, vendorEntityId);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }
    
    
    /**
     * Delete Interceptor replacements for the specified product instance ID, vendor ID and vendor entity ID.
     */
    public void deleteReplacementsByLimitId(String logTag, Connection connection, String prodInstId, String limitType, long limitId) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  limit_type = ? AND" +
            "  limit_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, limitType);
            statement.setLong(3, limitId);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }
    

    /**
     * Delete Interceptor replacements for the specified product instance ID.
     */
    public void deleteReplacementsByProdInstId(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_replacements " +
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
     * Delete Interceptor replacements for the specified product instance ID and replacement.
     */
    public void deleteReplacementsByProdInstIdAndReplacement(String logTag, Connection connection, String prodInstId, String replacement) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  replacement = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, replacement);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Delete Interceptor replacements for the specified product instance ID and replacement type.
     */
    public void deleteReplacementsByProdInstIdAndReplacementType(String logTag, Connection connection, String prodInstId, String replacementType) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  replacement_type = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, replacementType);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Return any limit ID corresponding to the specified interceptor replacement.
     */
    public Long getLimitId(String logTag, Connection connection, InterceptorReplacement interceptorReplacement) throws SQLException {
        final String SQL =
            "SELECT" +
            "  limit_id " +
            "FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  replacement = ? AND" +
            "  replacement_type = ? AND" +
            "  limit_type = ? AND" +
            "  vendor_id <=> ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, interceptorReplacement.getProdInstId());
            statement.setString(2, interceptorReplacement.getReplacementText());
            statement.setString(3, interceptorReplacement.getReplacementType());
            statement.setString(4, interceptorReplacement.getLimitType());
            statement.setObject(5, interceptorReplacement.getVendorId());
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, LimitIdFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    public Collection<InterceptorReplacement> getReplacements(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  original," +
            "  original_regex," +
            "  vanity_regex," +
            "  replacement," +
            "  replacement_type," +
            "  limit_type," +
            "  limit_id," +
            "  vendor_id," +
            "  vendor_entity_id " +
            "FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, InterceptorReplacementFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    public Collection<InterceptorReplacement> getReplacements(String logTag, Connection connection, String prodInstId, String limitType) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  original," +
            "  original_regex," +
            "  vanity_regex," +
            "  replacement," +
            "  replacement_type," +
            "  limit_type," +
            "  limit_id," +
            "  vendor_id," +
            "  vendor_entity_id " +
            "FROM" +
            "  interceptor_replacements " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  limit_type = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, limitType);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, InterceptorReplacementFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert the specified Interceptor replacement.
     */
    public void insertReplacement(String logTag, Connection connection, InterceptorReplacement interceptorReplacement) throws SQLException {
        final String SQL =
            "INSERT INTO interceptor_replacements" +
            "  (prod_inst_id, original, replacement, replacement_type, limit_type," +
            "   limit_id, original_regex, vanity_regex, vendor_id, vendor_entity_id) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?," +
            "   ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  original = VALUES(original)," +
            "  limit_type = VALUES(limit_type)," +
            "  vanity_regex = VALUES(vanity_regex)," +
            "  vendor_id = VALUES(vendor_id)," +
            "  vendor_entity_id = VALUES(vendor_entity_id);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, interceptorReplacement.getProdInstId());
            statement.setString(2, interceptorReplacement.getOriginalText());
            statement.setString(3, interceptorReplacement.getReplacementText());
            statement.setString(4, interceptorReplacement.getReplacementType());
            statement.setString(5, interceptorReplacement.getLimitType());
            statement.setLong(6, interceptorReplacement.getLimitId());
            statement.setString(7, interceptorReplacement.getOriginalRegex());
            statement.setString(8, interceptorReplacement.getVanityRegex());
            statement.setObject(9, interceptorReplacement.getVendorId());
            statement.setObject(10, interceptorReplacement.getVendorEntityId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update original values for the specified Interceptor replacement.
     */
    public void updateOriginalValues(String logTag, Connection connection, InterceptorReplacement interceptorReplacement) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  interceptor_replacements " +
            "SET" +
            "  original = ?," +
            "  original_regex = ?," +
            "  vanity_regex = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  vendor_id = ? AND" +
            "  vendor_entity_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, interceptorReplacement.getOriginalText());
            statement.setString(2, interceptorReplacement.getOriginalRegex());
            statement.setString(3, interceptorReplacement.getVanityRegex());
            statement.setString(4, interceptorReplacement.getProdInstId());
            statement.setInt(5, interceptorReplacement.getVendorId().intValue());
            statement.setLong(6, interceptorReplacement.getVendorEntityId().longValue());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update vendor values for the specified Interceptor replacement.
     */
    public void updateVendorValues(String logTag, Connection connection, InterceptorReplacement interceptorReplacement) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  interceptor_replacements " +
            "SET" +
            "  vendor_id = ?," +
            "  vendor_entity_id = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  replacement_type = ? AND" +
            "  limit_type = ? AND" +
            "  limit_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setObject(1, interceptorReplacement.getVendorId());
            statement.setObject(2, interceptorReplacement.getVendorEntityId());
            statement.setString(3, interceptorReplacement.getProdInstId());
            statement.setString(4, interceptorReplacement.getReplacementType());
            statement.setString(5, interceptorReplacement.getLimitType());
            statement.setLong(6, interceptorReplacement.getLimitId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create InterceptorReplacement objects from a result set.
     */
    private static class InterceptorReplacementFactory implements Factory<InterceptorReplacement> {
        public static final InterceptorReplacementFactory INSTANCE = new InterceptorReplacementFactory();

        /**
         * Constructor.
         */
        private InterceptorReplacementFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public InterceptorReplacement newInstance(ResultSet resultSet) throws SQLException {
            InterceptorReplacement interceptorReplacement = new InterceptorReplacement();
            interceptorReplacement.setLimitId(resultSet.getLong("limit_id"));
            interceptorReplacement.setLimitType(resultSet.getString("limit_type"));
            interceptorReplacement.setOriginalRegex(resultSet.getString("original_regex"));
            interceptorReplacement.setOriginalText(resultSet.getString("original"));
            interceptorReplacement.setProdInstId(resultSet.getString("prod_inst_id"));
            interceptorReplacement.setReplacementText(resultSet.getString("replacement"));
            interceptorReplacement.setReplacementType(resultSet.getString("replacement_type"));
            interceptorReplacement.setVanityRegex(resultSet.getString("vanity_regex"));
            interceptorReplacement.setVendorEntityId(getLongValue(resultSet, "vendor_entity_id"));
            interceptorReplacement.setVendorId(getIntegerValue(resultSet, "vendor_id"));
            return interceptorReplacement;
        }
    }

    /**
     * Factory class used to create limit IDs from a result set.
     */
    private static class LimitIdFactory extends LongFactory {
        public static final LimitIdFactory INSTANCE = new LimitIdFactory();

        /**
         * Constructor.
         */
        private LimitIdFactory() {
            super("limit_id");
        }
    }
}
