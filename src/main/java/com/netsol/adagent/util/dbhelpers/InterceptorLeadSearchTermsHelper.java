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

import com.netsol.adagent.util.beans.InterceptorLeadSearchTerms;

/**
 * DB helpers for Interceptor lead pages.
 */
public class InterceptorLeadSearchTermsHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:44 InterceptorLeadSearchTermsHelper.java NSI";

    /**
     * Constructor.
     */
    public InterceptorLeadSearchTermsHelper(String logComponent) {
        super(logComponent);

        return;
    }

    /**
     * Constructor.
     */
    public InterceptorLeadSearchTermsHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);

        return;
    }

    /**
     * Constructor.
     */
    public InterceptorLeadSearchTermsHelper(Log logger) {
        super(logger);

        return;
    }

    /**
     * Constructor.
     */
    public InterceptorLeadSearchTermsHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);

        return;
    }

    /**
     * Delete all lead search terms.
     */
    public void deleteAll(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_lead_search_terms;";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(SQL);
            this.logSqlStatement(logTag, statement);
            statement.executeUpdate();

            return;
        }
        finally {
            BaseHelper.close(statement);
        }
    }

    /**
     * Return the Interceptor lead search terms for the specified product instance ID.
     */
    public Collection<InterceptorLeadSearchTerms> getLeadSearchTerms(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  search_terms," +
            "  search_engine_id " +
            "FROM" +
            "  interceptor_lead_search_terms " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            this.logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return BaseHelper.newList(resultSet, InterceptorLeadSearchTermsFactory.INSTANCE);
        }
        finally {
            BaseHelper.close(statement, resultSet);
        }
    }

    /**
     * Insert the specified Interceptor lead search terms.
     */
    public void insertLeadPage(String logTag, Connection connection, InterceptorLeadSearchTerms interceptorLeadSearchTerms) throws SQLException {
        final String SQL =
            "INSERT INTO interceptor_lead_search_terms" +
            "  (prod_inst_id, search_terms, search_engine_id) " +
            "VALUES" +
            "  (?, ?, ?);";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, interceptorLeadSearchTerms.getProdInstId());
            statement.setString(2, interceptorLeadSearchTerms.getSearchTerms());
            statement.setObject(3, interceptorLeadSearchTerms.getSearchEngineId());
            this.logSqlStatement(logTag, statement);
            statement.executeUpdate();

            return;
        }
        finally {
            BaseHelper.close(statement);
        }
    }

    /**
     * Factory class used to create InterceptorLeadSearchTerms objects from a result set.
     */
    private static class InterceptorLeadSearchTermsFactory implements Factory<InterceptorLeadSearchTerms> {
        public static final InterceptorLeadSearchTermsFactory INSTANCE = new InterceptorLeadSearchTermsFactory();

        /**
         * Constructor.
         */
        private InterceptorLeadSearchTermsFactory() {
            return;
        }

        /**
         * Return a new instance with values from the result set.
         */
        public InterceptorLeadSearchTerms newInstance(ResultSet resultSet) throws SQLException {
            InterceptorLeadSearchTerms interceptorLeadSearchTerms = new InterceptorLeadSearchTerms();
            interceptorLeadSearchTerms.setProdInstId(resultSet.getString("prod_inst_id"));
            interceptorLeadSearchTerms.setSearchEngineId(BaseHelper.getIntegerValue(resultSet, "search_engine_id"));
            interceptorLeadSearchTerms.setSearchTerms(resultSet.getString("search_terms"));

            return interceptorLeadSearchTerms;
        }
    }

    // Test harness.
    public static void main(String[] args) {
        try {
            Connection connection = null;
            try {
                String logTag = null;
                String prodInstId = "WN.PP.33344444";
                connection = BaseHelper.createDevGdbConnection();
                InterceptorLeadSearchTermsHelper interceptorLeadSearchTermsHelper = new InterceptorLeadSearchTermsHelper("");
                InterceptorLeadSearchTerms interceptorLeadSearchTerms = new InterceptorLeadSearchTerms();
                interceptorLeadSearchTerms.setProdInstId(prodInstId);
                interceptorLeadSearchTerms.setSearchTerms("OMG");
                interceptorLeadSearchTermsHelper.insertLeadPage(logTag, connection, interceptorLeadSearchTerms);
                System.out.println(interceptorLeadSearchTermsHelper.getLeadSearchTerms(logTag, connection, prodInstId));
                interceptorLeadSearchTermsHelper.deleteAll(logTag, connection);
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
