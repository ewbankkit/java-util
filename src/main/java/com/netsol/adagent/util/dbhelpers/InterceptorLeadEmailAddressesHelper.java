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

/**
 * DB helpers for Interceptor e-mail addresses.
 */
public class InterceptorLeadEmailAddressesHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:44 InterceptorLeadEmailAddressesHelper.java NSI";

    /**
     * Constructor.
     */
    public InterceptorLeadEmailAddressesHelper(String logComponent) {
        super(logComponent);

        return;
    }

    /**
     * Constructor.
     */
    public InterceptorLeadEmailAddressesHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);

        return;
    }

    /**
     * Constructor.
     */
    public InterceptorLeadEmailAddressesHelper(Log logger) {
        super(logger);

        return;
    }

    /**
     * Constructor.
     */
    public InterceptorLeadEmailAddressesHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);

        return;
    }

    /**
     * Delete all e-mail addresses for the specified product instance ID.
     */
    public void deleteEmailAddresses(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  interceptor_lead_email_addresses " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(SQL);
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
     * Return the e-mail addresses for the specified product instance ID.
     */
    public Collection<String> getEmailAddresses(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  email_address " +
            "FROM" +
            "  interceptor_lead_email_addresses " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            this.logSqlStatement(null, statement);
            resultSet = statement.executeQuery();
            return BaseHelper.newList(resultSet, EmailAddressFactory.INSTANCE);
        }
        finally {
            BaseHelper.close(statement, resultSet);
        }
    }

    /**
     * Insert the specified e-mail address.
     */
    public void insertEmailAddresses(String logTag, Connection connection, String prodInstId, String emailAddress) throws SQLException {
        final String SQL =
            "INSERT IGNORE INTO interceptor_lead_email_addresses" +
            "  (prod_inst_id, email_address) " +
            "VALUES" +
            "  (?, ?);";

        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, emailAddress);
            this.logSqlStatement(logTag, statement);
            statement.executeUpdate();

            return;
        }
        finally {
            BaseHelper.close(statement);
        }
    }

    /**
     * Factory class used to create an e-mail address from a result set.
     */
    private static class EmailAddressFactory extends StringFactory {
        public static final EmailAddressFactory INSTANCE = new EmailAddressFactory();

        /**
         * Constructor.
         */
        private EmailAddressFactory() {
            super("email_address");

            return;
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
                InterceptorLeadEmailAddressesHelper interceptorLeadEmailAddressesHelper = new InterceptorLeadEmailAddressesHelper("");
                interceptorLeadEmailAddressesHelper.insertEmailAddresses(logTag, connection, prodInstId, "test@example.com");
                System.out.println(interceptorLeadEmailAddressesHelper.getEmailAddresses(logTag, connection, prodInstId));
                interceptorLeadEmailAddressesHelper.deleteEmailAddresses(logTag, connection, prodInstId);
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
