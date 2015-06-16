/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for product secondary URLs.
 */
public class ProductSecondaryUrlHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:53 ProductSecondaryUrlHelper.java NSI";

    /**
     * Constructor.
     */
    public ProductSecondaryUrlHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public ProductSecondaryUrlHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public ProductSecondaryUrlHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Delete all top-level domain names.
     */
    public void deleteSecondaryUrls(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM product_secondary_url WHERE prod_inst_id = ?;";
        deleteForProdInstId(logTag, connection, prodInstId, SQL);
    }

    /**
     * Return all secondary URLs.
     */
    public List<String> getSecondaryUrls(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  url " +
            "FROM" +
            "  product_secondary_url " +
            "WHERE" +
            "  prod_inst_id = ?;";
        return newListFromProdInstId(logTag, connection, prodInstId, SQL, new StringFactory("url") {});
    }

    /**
     * Return all secondary URLs.
     */
    public boolean hasSecondaryUrls(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  COUNT(*) " +
            "FROM" +
            "  product_secondary_url " +
            "WHERE" +
            "  prod_inst_id = ?;";
        return countGreaterThanZeroFromProdInstId(logTag, connection, prodInstId, SQL);
    }

    /**
     * Insert all secondary URLs.
     */
    public void insertSecondaryUrls(String logTag, Connection connection, final String prodInstId, Iterable<String> urls) throws SQLException {
        final String SQL =
            "INSERT IGNORE INTO product_secondary_url (prod_inst_id, url) VALUES (?, ?);";

        insertAll(logTag, connection, SQL, urls, new ParametersSetter<String>() {
            public void setParameters(PreparedStatement statement, String url) throws SQLException {
                statement.setString(1, prodInstId);
                statement.setString(2, url);
            }});
    }
}
