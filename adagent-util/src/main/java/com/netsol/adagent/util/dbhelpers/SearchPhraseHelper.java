/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.SearchPhrase;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for search phrases.
 */
public class SearchPhraseHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:54 SearchPhraseHelper.java NSI";
    
    /**
     * Constructor.
     */
    public SearchPhraseHelper(String logComponent) {
        super(logComponent);
        
        return;
    }

    /**
     * Constructor.
     */
    public SearchPhraseHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
        
        return;
    }

    /**
     * Constructor.
     */
    public SearchPhraseHelper(Log logger) {
        super(logger);
        
        return;
    }
    
    /**
     * Constructor.
     */
    public SearchPhraseHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
        
        return;
    }
    
    /**
     * Constructor.
     */
    public SearchPhraseHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
        
        return;
    }
    
    /**
     * Constructor.
     */
    public SearchPhraseHelper(BaseLoggable baseLoggable, boolean logSqlStatements) {
        super(baseLoggable, logSqlStatements);
        
        return;
    }
    
    /**
     * Delete the search phrases for the specified product instance ID and tracking number. 
     */
    public void deleteSearchPhrasesByProdInstIdAndTrackingNumber(String logTag, Connection connection, String prodInstId, String trackingNumber) throws SQLException {
        final String DELETE_SEARCH_PHRASE_SQL =
            "DELETE FROM" +
            "  search_phrases " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  tracking_number = ?;";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(DELETE_SEARCH_PHRASE_SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, trackingNumber);
            this.logSqlStatement(logTag, statement);
            statement.executeUpdate();
            
            return;
        }
        finally {
            BaseHelper.close(statement);
        }
    }
    
    /**
     * Delete all search phrases for the specified product instance ID. 
     */
    public void deleteSearchPhrasesByProdInstId(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String DELETE_SEARCH_PHRASES_SQL =
            "DELETE FROM" +
            "  search_phrases " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(DELETE_SEARCH_PHRASES_SQL);
            statement.setString(1, prodInstId);
            this.logSqlStatement(logTag, statement);
            statement.executeUpdate();
            
            return;
        }
        finally {
            BaseHelper.close(statement);
        }
    }
    
    /**
     * Return the search phrase for the specified product instance ID and tracking number. 
     */
    public SearchPhrase getSearchPhrase(String logTag, Connection connection, String prodInstId, String trackingNumber) throws SQLException {
        final String GET_SEARCH_PHRASE_SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  tracking_number," +
            "  search_engine_id," +
            "  keywords " +
            "FROM" +
            "  search_phrases " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  tracking_number = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(GET_SEARCH_PHRASE_SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, trackingNumber);
            this.logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return BaseHelper.firstValue(resultSet, SearchPhraseFactory.INSTANCE);
        }
        finally {
            BaseHelper.close(statement, resultSet);
        }
    }
    
    /**
     * Insert a search phrase. 
     */
    public void insertSearchPhrase(String logTag, Connection connection, SearchPhrase searchPhrase) throws SQLException {
        final String INSERT_SEARCH_PHRASE_SQL =
            "INSERT INTO search_phrases" +
            "  (prod_inst_id, tracking_number, search_engine_id, keywords) " +
            "VALUES" +
            "  (?, ?, ?, ?);";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(INSERT_SEARCH_PHRASE_SQL);
            statement.setString(1, searchPhrase.getProdInstId());
            statement.setString(2, searchPhrase.getTrackingNumber());
            statement.setInt(3, searchPhrase.getSearchEngineId());
            statement.setString(4, searchPhrase.getKeywords());
            this.logSqlStatement(logTag, statement);
            statement.executeUpdate();
            
            return;
        }
        finally {
            BaseHelper.close(statement);
        }
    }
    
    /**
     * Replace a search phrase. 
     */
    public void replaceSearchPhrase(String logTag, Connection connection, SearchPhrase searchPhrase) throws SQLException {
        final String REPLACE_SEARCH_PHRASE_SQL =
            "REPLACE INTO search_phrases" +
            "  (prod_inst_id, tracking_number, search_engine_id, keywords) " +
            "VALUES" +
            "  (?, ?, ?, ?);";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(REPLACE_SEARCH_PHRASE_SQL);
            statement.setString(1, searchPhrase.getProdInstId());
            statement.setString(2, searchPhrase.getTrackingNumber());
            statement.setInt(3, searchPhrase.getSearchEngineId());
            statement.setString(4, searchPhrase.getKeywords());
            this.logSqlStatement(logTag, statement);
            statement.executeUpdate();
            
            return;
        }
        finally {
            BaseHelper.close(statement);
        }
    }
    
    /**
     * Factory class used to create InterceptorMapping objects from a result set. 
     */
    private static class SearchPhraseFactory implements Factory<SearchPhrase> {
        public static final SearchPhraseFactory INSTANCE = new SearchPhraseFactory();
        
        /**
         * Constructor.
         */
        private SearchPhraseFactory() {
            return;
        }
        
        /**
         * Return a new instance with values from the result set. 
         */
        public SearchPhrase newInstance(ResultSet resultSet) throws SQLException {
            SearchPhrase searchPhrase = new SearchPhrase();
            searchPhrase.setKeywords(resultSet.getString("keywords"));
            searchPhrase.setProdInstId(resultSet.getString("prod_inst_id"));
            searchPhrase.setSearchEngineId(resultSet.getInt("search_engine_id"));
            searchPhrase.setTrackingNumber(resultSet.getString("tracking_number"));
            
            return searchPhrase;
        }
    }
    
    // Test harness.
    public static void main(String[] args) {
        try {
            Connection connection = null;
            try {
                String logTag = null;
                String prodInstId = "WN.DEV.BING.0001";
                String trackingNumber = "7036285555";
                connection = BaseHelper.createDevGdbConnection();
                SearchPhraseHelper searchPhraseHelper = new SearchPhraseHelper((String)null);
                SearchPhrase searchPhrase = new SearchPhrase();
                searchPhrase.setKeywords("OMG WTF");
                searchPhrase.setProdInstId(prodInstId);
                searchPhrase.setSearchEngineId(1);
                searchPhrase.setTrackingNumber(trackingNumber);
                searchPhraseHelper.insertSearchPhrase(logTag, connection, searchPhrase);
                System.out.println(searchPhraseHelper.getSearchPhrase(logTag, connection, prodInstId, trackingNumber));
                searchPhraseHelper.deleteSearchPhrasesByProdInstIdAndTrackingNumber(logTag, connection, prodInstId, trackingNumber);
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
