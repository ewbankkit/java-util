/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.netsol.adagent.util.MapBuilder;
import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.beans.VendorAccountConfig;
import com.netsol.adagent.util.codes.VendorAccountStatus;

/**
 * DB helpers for vendor account configurations.
 */
public class VendorAccountConfigHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:56 VendorAccountConfigHelper.java NSI";

    public VendorAccountConfigHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Delete the vendor account configuration value for the specified vendor account ID and vendor account configuration name.
     */
    public void deleteVendorAccountConfig(String logTag, Connection connection, int vendorAccountId, String name) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  vendor_account_config " +
            "WHERE" +
            "  vendor_account_id = ? AND" +
            "  name = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorAccountId);
            statement.setString(2, name);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Return all vendor account configuration name and value pairs for the specified product instance ID.
     */
    public Map<Integer, VendorAccountConfig> getAllVendorAccountConfigs(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  va.vendor_account_id AS vendor_account_id," +
            "  va.vendor_id AS vendor_id," +
            "  va.master_account AS master_account " +
            "FROM" +
            "  vendor_account AS va " +
            "INNER JOIN" +
            "  product_vendor_account_xref AS pvax " +
            "ON" +
            "  pvax.vendor_account_id = va.vendor_account_id " +
            "WHERE" +
            "  pvax.prod_inst_id = ? AND" +
            "  va.status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, VendorAccountStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            Collection<VendorAccountConfig> vendorAccountConfigs = newList(resultSet, VendorAccountConfigFactory.INSTANCE);
            Map<Integer, VendorAccountConfig> map = new HashMap<Integer, VendorAccountConfig>();
            for (VendorAccountConfig vendorAccountConfig : vendorAccountConfigs) {
                int vendorId = vendorAccountConfig.getVendorId();
                // Merge product_type and vendor_account configurations.
                MapBuilder<String, String> mapBuilder = new MapBuilder<String, String>();
                mapBuilder.putAll(getProductTypeVendorProperties(logTag, connection, prodInstId, vendorId));
                mapBuilder.putAll(getVendorAccountVendorProperties(logTag, connection, vendorAccountConfig.getVendorAccountId()));
                vendorAccountConfig.setProperties(mapBuilder.unmodifiableMap());
                map.put(Integer.valueOf(vendorId), vendorAccountConfig);
            }
            return Collections.unmodifiableMap(map);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the vendor account configuration for the specified product instance ID and vendor ID.
     */
    public VendorAccountConfig getVendorAccountConfigByProdInstIdAndVendorId(String logTag, Connection connection, String prodInstId, int vendorId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  va.vendor_account_id AS vendor_account_id," +
            "  va.vendor_id AS vendor_id," +
            "  va.master_account AS master_account " +
            "FROM" +
            "  vendor_account AS va " +
            "INNER JOIN" +
            "  product_vendor_account_xref AS pvax " +
            "ON" +
            "  pvax.vendor_account_id = va.vendor_account_id " +
            "WHERE" +
            "  pvax.prod_inst_id = ? AND" +
            "  va.vendor_id = ? AND" +
            "  va.status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setInt(2, vendorId);
            statement.setString(3, VendorAccountStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            VendorAccountConfig vendorAccountConfig = singleValue(resultSet, VendorAccountConfigFactory.INSTANCE);
            if (vendorAccountConfig != null) {
                // Merge product_type and vendor_account configurations.
                MapBuilder<String, String> mapBuilder = new MapBuilder<String, String>();
                mapBuilder.putAll(getProductTypeVendorProperties(logTag, connection, prodInstId, vendorId));
                mapBuilder.putAll(getVendorAccountVendorProperties(logTag, connection, vendorAccountConfig.getVendorAccountId()));
                vendorAccountConfig.setProperties(mapBuilder.unmodifiableMap());
            }
            return vendorAccountConfig;
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the vendor account configuration for the specified vendor account ID.
     */
    public VendorAccountConfig getVendorAccountConfigByVendorAccountId(String logTag, Connection connection, int vendorAccountId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_account_id," +
            "  vendor_id," +
            "  master_account " +
            "FROM" +
            "  vendor_account " +
            "WHERE" +
            "  vendor_account_id = ? AND" +
            "  status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorAccountId);
            statement.setString(2, VendorAccountStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            VendorAccountConfig vendorAccountConfig = singleValue(resultSet, VendorAccountConfigFactory.INSTANCE);
            if (vendorAccountConfig != null) {
                vendorAccountConfig.setProperties(getVendorAccountVendorProperties(logTag, connection, vendorAccountConfig.getVendorAccountId()));
            }
            return vendorAccountConfig;
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the configuration properties for the specified vendor account ID.
     */
    public Map<String, String> getVendorAccountVendorProperties(String logTag, Connection connection, int vendorAccountId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  `name`," +
            "  `value` " +
            "FROM" +
            "  vendor_account_config " +
            "WHERE" +
            "  vendor_account_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorAccountId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newMap(resultSet, new Factory<Pair<String, String>>() {
                public Pair<String, String> newInstance(ResultSet resultSet) throws SQLException {
                    return Pair.from(resultSet.getString("name"), resultSet.getString("value"));
                }});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the configuration properties for the specified prodInstId and vendor ID.
     */
    public Map<String, String> getProductTypeVendorProperties(String logTag, Connection connection, String prodInstId, int vendorId) throws SQLException {
        final String SQL =
            "SELECT ptvc.`name`," +
            "       ptvc.`value` " +
            "FROM   product_type_vendor_config AS ptvc" +
            "       INNER JOIN product AS p" +
            "         ON p.channel_id = ptvc.channel_id" +
            "            AND p.prod_id = ptvc.prod_id " +
            "WHERE  ptvc.vendor_id = ?" +
            "       AND p.prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorId);
            statement.setString(2, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newMap(resultSet, new Factory<Pair<String, String>>() {
                public Pair<String, String> newInstance(ResultSet resultSet) throws SQLException {
                    return Pair.from(resultSet.getString("name"), resultSet.getString("value"));
                }});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Replace the vendor account configuration value for the specified vendor account ID and vendor account configuration name.
     */
    public void replaceVendorAccountConfigValue(String logTag, Connection connection, int vendorAccountId, String name, String value) throws SQLException {
        final String SQL =
            "REPLACE INTO vendor_account_config" +
            "  (vendor_account_id, name, value) " +
            "VALUES" +
            "  (?, ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorAccountId);
            statement.setString(2, name);
            statement.setString(3, value);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create vendor account configuration objects from a result set.
     */
    private static class VendorAccountConfigFactory implements Factory<VendorAccountConfig> {
        public static final VendorAccountConfigFactory INSTANCE = new VendorAccountConfigFactory();

        /**
         * Constructor.
         */
        private VendorAccountConfigFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public VendorAccountConfig newInstance(ResultSet resultSet) throws SQLException {
            VendorAccountConfig vendorAccountConfig = new VendorAccountConfig();
            vendorAccountConfig.setMasterAccount(resultSet.getBoolean("master_account"));
            vendorAccountConfig.setVendorAccountId(resultSet.getInt("vendor_account_id"));
            vendorAccountConfig.setVendorId(resultSet.getInt("vendor_id"));
            return vendorAccountConfig;
        }
    }
}
