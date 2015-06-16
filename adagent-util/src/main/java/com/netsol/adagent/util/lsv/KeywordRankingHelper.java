/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.lsv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

// Keyword ranking DB helper.
/* package-private */ class KeywordRankingHelper extends BaseHelper {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:11 KeywordRankingHelper.java NSI";
    
    /**
     * Constructor.
     */
    public KeywordRankingHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public KeywordRankingHelper(Log logger) {
        super(logger);
    }
    
    /**
     * Return the latest profile URL for the specified host name, site URL and search engine name.
     */
    public Pair<String, Date> getLatestProfileUrl(String logTag, Connection connection, String hostName, String siteUrl, String searchEngineName) throws SQLException {
        final String SQL =
            "SELECT nsr3.profile_url AS profile_url," +
            "       nsr3.search_date AS search_date "+
            "FROM   (SELECT   se1.search_engine_id  AS search_engine_id,"+
            "                 MAX(nsr1.search_date) AS search_date,"+
            "                 MIN(nsr1.sequence)    AS sequence"+
            "        FROM     natural_search_results nsr1"+
            "                 INNER JOIN search_engines se1"+
            "                   ON nsr1.search_engine_id = se1.search_engine_id"+
            "        WHERE    nsr1.hostname = ?"+
            "                 AND se1.name = ?"+
            "                 AND url LIKE CONCAT('%', ?, '%')"+
            "                 AND nsr1.profile_url IS NOT NULL"+
            "        GROUP BY 1) AS nsr2"+
            "       INNER JOIN natural_search_results nsr3"+
            "         ON nsr2.search_engine_id = nsr3.search_engine_id"+
            "            AND nsr2.search_date = nsr3.search_date"+
            "            AND nsr2.sequence = nsr3.sequence"+
            "       INNER JOIN search_engines se2"+
            "         ON nsr3.search_engine_id = se2.search_engine_id "+
            "WHERE  nsr3.hostname = ?"+
            "       AND se2.name = ?"+
            "       AND url LIKE CONCAT('%', ?, '%')"+
            "       AND nsr3.profile_url IS NOT NULL "+
            "LIMIT  1;";
        
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, hostName);
            statement.setString(2, searchEngineName);
            statement.setString(3, siteUrl);
            statement.setString(4, hostName);
            statement.setString(5, searchEngineName);
            statement.setString(6, siteUrl);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return singleValue(resultSet, ProfileUrlFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }
    
    /**
     * Factory class used to create pairs of ranking dates and keyword rankings from a result set. 
     */
    private static class ProfileUrlFactory implements Factory<Pair<String, Date>> {
        public static final ProfileUrlFactory INSTANCE = new ProfileUrlFactory();
        
        /**
         * Constructor.
         */
        private ProfileUrlFactory() {}
        
        /**
         * Return a new instance with values from the result set. 
         */
        public Pair<String, Date> newInstance(ResultSet resultSet) throws SQLException {
            return Pair.from(resultSet.getString("profile_url"), (Date)resultSet.getTimestamp("search_date"));
        }
    }
}
