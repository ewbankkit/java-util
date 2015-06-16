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

import com.netsol.adagent.util.beans.SearchEngine;
import com.netsol.adagent.util.codes.VendorId;

/**
 * DB helpers for search engines.
 */
public class SearchEngineHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:54 SearchEngineHelper.java NSI";

    /**
     * Constructor.
     */
    public SearchEngineHelper(String logComponent) {
        super(logComponent);

        return;
    }

    /**
     * Constructor.
     */
    public SearchEngineHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);

        return;
    }

    /**
     * Constructor.
     */
    public SearchEngineHelper(Log logger) {
        super(logger);

        return;
    }

    /**
     * Constructor.
     */
    public SearchEngineHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);

        return;
    }

    /**
     * Return the search engine corresponding to the given vendor ID.
     */
    public Collection<Integer> getSearchEngineIds(String logTag, Connection connection, int vendorId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  search_engine_id " +
            "FROM" +
            "  search_engines " +
            "WHERE" +
            "  vendor_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorId);
            this.logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return BaseHelper.newList(resultSet, SearchEngineIdFactory.INSTANCE);
        }
        finally {
            BaseHelper.close(statement, resultSet);
        }
    }

    /**
     * Return all search engines.
     */
    public Collection<SearchEngine> getAllSearchEngines(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "SELECT" +
            "  search_engine_id," +
            "  vendor_id," +
            "  search_engine_name," +
            "  http_referer_regex " +
            "FROM" +
            "  search_engines;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(SQL);
            this.logSqlStatement(null, statement);
            resultSet = statement.executeQuery();
            return BaseHelper.newList(resultSet, SearchEngineFactory.INSTANCE);
        }
        finally {
            BaseHelper.close(statement, resultSet);
        }
    }

    /**
     * Factory class used to create SearchEngine objects from a result set.
     */
    private static class SearchEngineFactory implements Factory<SearchEngine> {
        public static final SearchEngineFactory INSTANCE = new SearchEngineFactory();

        /**
         * Constructor.
         */
        private SearchEngineFactory() {
            return;
        }

        /**
         * Return a new instance with values from the result set.
         */
        public SearchEngine newInstance(ResultSet resultSet) throws SQLException {
            SearchEngine searchEngine = new SearchEngine();
            searchEngine.setHttpReferrerRegex(resultSet.getString("http_referer_regex"));
            searchEngine.setSearchEngineId(resultSet.getInt("search_engine_id"));
            searchEngine.setSearchEngineName(resultSet.getString("search_engine_name"));
            searchEngine.setVendorId(resultSet.getInt("vendor_id"));

            return searchEngine;
        }
    }

    /**
     * Factory class used to create objects from a result set.
     */
    private static class SearchEngineIdFactory extends IntegerIdFactory {
        public static final SearchEngineIdFactory INSTANCE = new SearchEngineIdFactory();

        /**
         * Constructor.
         */
        private SearchEngineIdFactory() {
            super("search_engine_id");

            return;
        }
    }

    // Test harness.
    public static void main(String[] args) {
        try {
            Connection connection = null;
            try {
                String logTag = null;
                connection = BaseHelper.createDevGdbConnection();
                SearchEngineHelper searchEngineHelper = new SearchEngineHelper((String)null);
                System.out.println(searchEngineHelper.getAllSearchEngines(logTag, connection));
                System.out.println(searchEngineHelper.getSearchEngineIds(logTag, connection, VendorId.GOOGLE));
            }
            finally {
                BaseHelper.close(connection);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        return;
    }
}
