/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.netsol.adagent.util.beans.VendorAccount;
import com.netsol.adagent.util.codes.VendorAccountStatus;

/**
 * DB helpers for vendor accounts.
 */
public class VendorAccountHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:56 VendorAccountHelper.java NSI";

    public VendorAccountHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Return the vendor account for the specified vendor account ID.
     */
    public VendorAccount getVendorAccountByVendorAccountId(String logTag, Connection connection, int vendorAccountId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_account_id," +
            "  channel_id," +
            "  vendor_id," +
            "  master_account," +
            "  status " +
            "FROM" +
            "  vendor_account " +
            "WHERE" +
            "  vendor_account_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorAccountId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return singleValue(resultSet, VendorAccountFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the active vendor account IDs for the specified channel ID.
     */
    public List<Integer> getVendorAccountIdsByChannelId(String logTag, Connection connection, int channelId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_account_id " +
            "FROM" +
            "  vendor_account " +
            "WHERE" +
            "  channel_id = ? AND" +
            "  status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, channelId);
            statement.setString(2, VendorAccountStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, new IntegerFactory("vendor_account_id") {});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the active vendor accounts for the specified vendor ID.
     */
    public List<VendorAccount> getVendorAccountsByVendorId(String logTag, Connection connection, int vendorId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_account_id," +
            "  channel_id," +
            "  vendor_id," +
            "  master_account," +
            "  status " +
            "FROM" +
            "  vendor_account " +
            "WHERE" +
            "  vendor_id = ? AND" +
            "  status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorId);
            statement.setString(2, VendorAccountStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, VendorAccountFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the active vendor accounts for the specified vendor ID and channel ID.
     */
    public List<VendorAccount> getVendorAccountsByVendorIdAndChannelId(String logTag, Connection connection, int vendorId, int channelId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_account_id," +
            "  channel_id," +
            "  vendor_id," +
            "  master_account," +
            "  status " +
            "FROM" +
            "  vendor_account " +
            "WHERE" +
            "  vendor_id = ? AND" +
            "  channel_id = ? AND" +
            "  status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorId);
            statement.setInt(2, channelId);
            statement.setString(3, VendorAccountStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, VendorAccountFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the vendor account for the specified product instance ID and vendor ID.
     */
    public VendorAccount getVendorAccountByProdInstIdAndVendorId(String logTag, Connection connection, String prodInstId, int vendorId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  va.vendor_account_id AS vendor_account_id," +
            "  va.channel_id AS channel_id," +
            "  va.vendor_id AS vendor_id," +
            "  va.master_account AS master_account," +
            "  va.status AS status " +
            "FROM" +
            "  vendor_account AS va " +
            "INNER JOIN" +
            "  product AS p " +
            "ON" +
            "  p.channel_id = va.channel_id " +
            "WHERE" +
            "  p.prod_inst_id = ? AND" +
            "  va.vendor_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setInt(2, vendorId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return singleValue(resultSet, VendorAccountFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Factory class used to create vendor accounts from a result set.
     */
    private static class VendorAccountFactory implements Factory<VendorAccount> {
        public static final VendorAccountFactory INSTANCE = new VendorAccountFactory();

        /**
         * Constructor.
         */
        private VendorAccountFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public VendorAccount newInstance(ResultSet resultSet) throws SQLException {
            VendorAccount vendorAccount = new VendorAccount();
            vendorAccount.setChannelId(resultSet.getInt("channel_id"));
            vendorAccount.setMasterAccount(resultSet.getBoolean("master_account"));
            vendorAccount.setStatus(resultSet.getString("status"));
            vendorAccount.setVendorAccountId(resultSet.getInt("vendor_account_id"));
            vendorAccount.setVendorId(resultSet.getInt("vendor_id"));
            return vendorAccount;
        }
    }
}
