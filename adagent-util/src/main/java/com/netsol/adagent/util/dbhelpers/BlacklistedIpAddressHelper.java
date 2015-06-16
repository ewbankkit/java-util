/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

/**
 * DB helpers for blacklisted IP addresses.
 */
public class BlacklistedIpAddressHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:41 BlacklistedIpAddressHelper.java NSI";

    /**
     * Constructor.
     */
    public BlacklistedIpAddressHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public BlacklistedIpAddressHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public BlacklistedIpAddressHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public BlacklistedIpAddressHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Delete all blacklisted IP addresses.
     */
    public void deleteBlacklistedIpAddresses(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "DELETE FROM blacklisted_ip_address;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Return whether or not the specified IP address is blacklisted.
     */
    public boolean isBlacklistedIpAddress(String logTag, Connection connection, String ipAddress) throws SQLException {
        final String SQL =
            "SELECT" +
            "  COUNT(*) " +
            "FROM" +
            "  blacklisted_ip_address " +
            "WHERE" +
            "  ip = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, ipAddress);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return countGreaterThanZero(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert all blacklisted IP addresses.
     */
    public void insertBlacklistedIpAddresses(String logTag, Connection connection, Iterable<String> blacklistedIpAddresses) throws SQLException {
        final String SQL =
            "INSERT IGNORE INTO blacklisted_ip_address (ip) VALUES (?);";

        insertAll(logTag, connection, SQL, blacklistedIpAddresses, SINGLE_STRING_VALUE_PARAMETERS_SETTER);
    }
}
