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

import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for top-level domains.
 */
public class TopLevelDomainHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:55 TopLevelDomainHelper.java NSI";

    /**
     * Constructor.
     */
    public TopLevelDomainHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public TopLevelDomainHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public TopLevelDomainHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Delete all top-level domain names.
     */
    public void deleteTopLevelDomainNames(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "DELETE FROM top_level_domain;";

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
     * Return all top-level domain names.
     */
    public Collection<String> getTopLevelDomainNames(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "SELECT" +
            "  name " +
            "FROM" +
            "  top_level_domain;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, new StringFactory("name") {});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert all top-level domain names.
     */
    public void insertTopLevelDomainNames(String logTag, Connection connection, Iterable<String> topLevelDomainNames) throws SQLException {
        final String SQL =
            "INSERT IGNORE INTO top_level_domain (name) VALUES (LOWER(?));";

        insertAll(logTag, connection, SQL, topLevelDomainNames, SINGLE_STRING_VALUE_PARAMETERS_SETTER);
    }
}
