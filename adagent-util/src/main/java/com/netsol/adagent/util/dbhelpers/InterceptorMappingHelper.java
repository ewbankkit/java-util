/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.InterceptorMapping;
import com.netsol.adagent.util.codes.ProductStatus;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for Interceptor mappings.
 */
public class InterceptorMappingHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:44 InterceptorMappingHelper.java NSI";

    /**
     * Constructor.
     */
    public InterceptorMappingHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public InterceptorMappingHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public InterceptorMappingHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public InterceptorMappingHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public InterceptorMappingHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Constructor.
     */
    public InterceptorMappingHelper(BaseLoggable baseLoggable, boolean logSqlStatements) {
        super(baseLoggable, logSqlStatements);
    }

    /**
     * Delete Interceptor mappings for the specified alias.
     */
    public void deleteMappingByAlias(String logTag, Connection connection, String alias) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_mappings " +
            "WHERE" +
            "  alias = ?;";

        deleteForSingleParameter(logTag, connection, alias, SQL);
    }

    /**
     * Delete Interceptor mappings for the specified alias and product instance ID.
     */
    public void deleteMappingByAliasAndProdInstId(String logTag, Connection connection, String alias, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_mappings " +
            "WHERE" +
            "  alias = ? AND" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, alias);
            statement.setString(2, prodInstId);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Delete Interceptor mappings for the specified product instance ID.
     */
    public void deleteMappingsByProdInstId(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_mappings " +
            "WHERE" +
            "  prod_inst_id = ?;";

        deleteForProdInstId(logTag, connection, prodInstId, SQL);
    }

    /**
     * Return the Interceptor mapping for the specified alias.
     * Prefer ACTIVE products over PENDING_DELETE products over products in other statuses.
     */
    public InterceptorMapping getMappingByAlias(String logTag, Connection connection, String alias) throws SQLException {
        final String SQL =
            "SELECT" +
            "  im.prod_inst_id AS prod_inst_id," +
            "  im.realhost AS realhost," +
            "  im.realport AS realport," +
            "  im.alias AS alias " +
            "FROM" +
            "  interceptor_mappings AS im " +
            "INNER JOIN" +
            "  product AS p " +
            "ON" +
            "  p.prod_inst_id = im.prod_inst_id " +
            "WHERE" +
            "  im.alias = ? " +
            "ORDER BY" +
            "  FIELD(p.status, ?, ?) DESC;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, alias);
            statement.setString(2, ProductStatus.PENDING_DELETE);
            statement.setString(3, ProductStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, InterceptorMappingFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the Interceptor mapping for the specified product instance ID and alias.
     */
    public InterceptorMapping getMappingByProdInstIdAndAlias(String logTag, Connection connection, String prodInstId, String alias) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  realhost," +
            "  realport," +
            "  alias " +
            "FROM" +
            "  interceptor_mappings " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  alias = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, alias);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, InterceptorMappingFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the Interceptor mapping for the specified product instance ID and real host name.
     */
    public InterceptorMapping getMappingByProdInstIdAndRealHost(String logTag, Connection connection, String prodInstId, String realHostName) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  realhost," +
            "  realport," +
            "  alias " +
            "FROM" +
            "  interceptor_mappings " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  realhost = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, realHostName);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, InterceptorMappingFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the Interceptor mappings for the specified product instance ID.
     */
    public List<InterceptorMapping> getMappingsByProdInstId(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  realhost," +
            "  realport," +
            "  alias " +
            "FROM" +
            "  interceptor_mappings " +
            "WHERE" +
            "  prod_inst_id = ?;";

        return newListFromProdInstId(logTag, connection, prodInstId, SQL, InterceptorMappingFactory.INSTANCE);
    }

    /**
     * Return the primary alias for specified product instance ID.
     */
    public String getPrimaryAlias(String logTag, Connection connection, String prodInstId) throws SQLException {
        InterceptorMapping interceptorMapping = getPrimaryInterceptorMapping(logTag, connection, prodInstId);
        return (interceptorMapping == null) ? null : interceptorMapping.getAlias();
    }

    /**
     * Return the primary Interceptor mapping for specified product instance ID.
     */
    public InterceptorMapping getPrimaryInterceptorMapping(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "(SELECT 1 AS order_by," +
            "        im.prod_inst_id AS prod_inst_id," +
            "        im.realhost AS realhost," +
            "        im.realport AS realport," +
            "        im.alias AS alias" +
            " FROM   (SELECT p.prod_inst_id   AS prod_inst_id," +
            "                HOST_NAME(p.url) AS hostname" +
            "         FROM   product AS p" +
            "         WHERE  p.prod_inst_id = ?) AS t1" +
            "        INNER JOIN interceptor_mappings AS im" +
            "          ON (im.prod_inst_id = t1.prod_inst_id" +
            "              AND (im.realhost = t1.hostname" +
            "                   OR im.realhost = CONCAT('www.', t1.hostname)))" +
            " ORDER  BY LENGTH(t1.hostname) DESC) " +
            "UNION " +
            "(SELECT 2 AS order_by," +
            "        im.prod_inst_id AS prod_inst_id," +
            "        im.realhost AS realhost," +
            "        im.realport AS realport," +
            "        im.alias AS alias" +
            " FROM   interceptor_mappings AS im" +
            " WHERE  im.prod_inst_id = ?) " +
            "ORDER  BY order_by ASC;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, InterceptorMappingFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert the specified Interceptor mapping.
     */
    public void insertMapping(String logTag, Connection connection, InterceptorMapping interceptorMapping) throws SQLException {
        final String SQL =
            "INSERT INTO interceptor_mappings" +
            "  (prod_inst_id, alias, realhost, realport) " +
            "VALUES" +
            "  (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  realhost = VALUES(realhost)," +
            "  realport = VALUES(realport);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, interceptorMapping.getProdInstId());
            statement.setString(2, interceptorMapping.getAlias());
            statement.setString(3, interceptorMapping.getRealHost());
            statement.setInt(4, interceptorMapping.getRealPort());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update the alias for the specified product instance ID.
     */
    public void updateAlias(String logTag, Connection connection, String prodInstId, String oldAlias, String newAlias) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  interceptor_mappings " +
            "SET" +
            "  alias = ? " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  alias = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, newAlias);
            statement.setString(2, prodInstId);
            statement.setString(3, oldAlias);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create InterceptorMapping objects from a result set.
     */
    private static class InterceptorMappingFactory implements Factory<InterceptorMapping> {
        public static final InterceptorMappingFactory INSTANCE = new InterceptorMappingFactory();

        /**
         * Constructor.
         */
        private InterceptorMappingFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public InterceptorMapping newInstance(ResultSet resultSet) throws SQLException {
            InterceptorMapping interceptorMapping = new InterceptorMapping();
            interceptorMapping.setAlias(resultSet.getString("alias"));
            interceptorMapping.setProdInstId(resultSet.getString("prod_inst_id"));
            interceptorMapping.setRealHost(resultSet.getString("realhost").toLowerCase());
            interceptorMapping.setRealPort(resultSet.getInt("realport"));
            return interceptorMapping;
        }
    }
}
