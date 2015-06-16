/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.beans.VendorCredentials;
import com.netsol.adagent.util.codes.VendorStatus;
import com.netsol.adagent.util.codes.VendorType;

/**
 * DB helpers for vendor credentials.
 */
public class VendorCredentialsHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:56 VendorCredentialsHelper.java NSI";

    /**
     * Constructor.
     */
    public VendorCredentialsHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public VendorCredentialsHelper(Log logger) {
        super(logger);
    }

    /**
     * Delete the vendor credentials for the specified product instance ID and vendor ID.
     */
    public void deleteVendorCredentials(String logTag, Connection connection, String prodInstId, int vendorId) throws SQLException {
        final String SQL =
            "DELETE FROM vendor_credential " +
            "WHERE       prod_inst_id = ?" +
            "            AND vendor_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setInt(2, vendorId);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return all ad vendor IDs for the specified product instance ID.
     */
    public Collection<Integer> getAdVendorIds(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT vc.vendor_id AS vendor_id " +
            "FROM   vendor_credential AS vc" +
            "       INNER JOIN vendor_service AS vs" +
            "           ON vs.vendor_id = vc.vendor_id " +
            "WHERE  vc.prod_inst_id = ?" +
            "       AND (vs.vendor_type & ?) = ?" +
            "       AND vs.`status` = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setInt(2, VendorType.AD);
            statement.setInt(3, VendorType.AD);
            statement.setString(4, VendorStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, new IntegerFactory("vendor_id") {});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return a map of vendor ID to vendor credentials for the specified product instance ID.
     */
    public Map<Integer, VendorCredentials> getAllVendorCredentials(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            VendorCredentialsFactory.SQL_SELECT_EXPRESSION +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newMap(resultSet, VendorIdAndVendorCredentialsFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the product instance ID for the specified vendor account name, vendor ID and vendor account ID.
     */
    public String getProdInstIdByVendorAccountName(String logTag, Connection connection, String vendorAccountName, int vendorId, int vendorAccountId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vc.prod_inst_id AS prod_inst_id " +
            "FROM" +
            "  vendor_credential AS vc," +
            "  product_vendor_account_xref AS pvax " +
            "WHERE" +
            "  vc.vendor_account_name = ? AND" +
            "  vc.vendor_id = ? AND" +
            "  vc.prod_inst_id = pvax.prod_inst_id AND" +
            "  pvax.vendor_account_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, vendorAccountName);
            statement.setInt(2, vendorId);
            statement.setInt(3, vendorAccountId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            String prodInstId = singleValue(resultSet, ProdInstIdFactory.INSTANCE);
            if (prodInstId == null) {
                logWarning(
                    logTag,
                    "No entries were found for vendor_account_name = '" + vendorAccountName + "' and vendor_id = " + vendorId + ". " +
                    "You might need to get the exact account name from the vendor and update it on vendor_credentials."
                  );
            }
            return prodInstId;
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the product instance ID for the specified vendor account id1(accountId at vendor side), vendor ID and vendor account ID.
     */
    public String getProdInstIdByVendorAccountId1(String logTag, Connection connection, String vendorAccountId1, int vendorId, int vendorAccountId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vc.prod_inst_id AS prod_inst_id " +
            "FROM" +
            "  vendor_credential AS vc," +
            "  product_vendor_account_xref AS pvax " +
            "WHERE" +
            "  vc.vendor_account_id1 = ? AND" +
            "  vc.vendor_id = ? AND" +
            "  vc.prod_inst_id = pvax.prod_inst_id AND" +
            "  pvax.vendor_account_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, vendorAccountId1);
            statement.setInt(2, vendorId);
            statement.setInt(3, vendorAccountId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            String prodInstId = singleValue(resultSet, ProdInstIdFactory.INSTANCE);
            if (prodInstId == null) {
                logWarning(
                    logTag,
                    "No entries were found for vendor_account_id1 = '" + vendorAccountId1 + "' and vendor_id = " + vendorId + ". " +
                    "You might need to get the exact account name from the vendor and update it on vendor_credentials."
                  );
            }
            return prodInstId;
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the vendor credentials for the specified product instance ID and vendor ID.
     */
    public VendorCredentials getVendorCredentials(String logTag, Connection connection, String prodInstId, int vendorId) throws SQLException {
        final String SQL =
            VendorCredentialsFactory.SQL_SELECT_EXPRESSION +
            "WHERE  prod_inst_id = ?" +
            "       AND vendor_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setInt(2, vendorId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return singleValue(resultSet, VendorCredentialsFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert vendor credentials.
     */
    public void insertVendorCredentials(String logTag, Connection connection, VendorCredentials vendorCredentials) throws SQLException {
        final String SQL =
            "INSERT INTO vendor_credential" +
            "  (prod_inst_id, vendor_id, vendor_account_id1, vendor_account_id2," +
            "   vendor_account_name, vendor_account_username, vendor_account_password, vendor_time_zone_id," +
            "   end_date, created_date) " +
            "VALUES" +
            "  (?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, DATE(NOW())) " +
            "ON DUPLICATE KEY UPDATE" +
            "  vendor_account_id1 = VALUES(vendor_account_id1)," +
            "  vendor_account_id2 = VALUES(vendor_account_id2)," +
            "  vendor_account_name = VALUES(vendor_account_name)," +
            "  vendor_account_username = VALUES(vendor_account_username)," +
            "  vendor_account_password = VALUES(vendor_account_password)," +
            "  vendor_time_zone_id = VALUES(vendor_time_zone_id)," +
            "  end_date = VALUES(end_date);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, vendorCredentials.getProdInstId());
            statement.setInt(parameterIndex++, vendorCredentials.getVendorId());
            statement.setString(parameterIndex++, vendorCredentials.getVendorAccountId1());
            statement.setString(parameterIndex++, vendorCredentials.getVendorAccountId2());
            statement.setString(parameterIndex++, vendorCredentials.getVendorAccountName());
            statement.setString(parameterIndex++, vendorCredentials.getVendorAccountUserName());
            statement.setString(parameterIndex++, vendorCredentials.getVendorAccountPassword());
            statement.setObject(parameterIndex++, vendorCredentials.getVendorTimeZoneId());
            statement.setDate(parameterIndex++, toSqlDate(vendorCredentials.getEndDate()));
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create VendorCredentials objects from a result set.
     */
    private static class VendorCredentialsFactory implements Factory<VendorCredentials> {
        public static final VendorCredentialsFactory INSTANCE = new VendorCredentialsFactory();

        public static String SQL_SELECT_EXPRESSION =
            "SELECT prod_inst_id," +
            "       vendor_id," +
            "       vendor_account_id1," +
            "       vendor_account_id2," +
            "       vendor_account_name," +
            "       vendor_account_username," +
            "       vendor_account_password," +
            "       vendor_time_zone_id," +
            "       end_date " +
            "FROM   vendor_credential ";

        /**
         * Constructor.
         */
        private VendorCredentialsFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public VendorCredentials newInstance(ResultSet resultSet) throws SQLException {
            VendorCredentials vendorCredentials = new VendorCredentials();
            vendorCredentials.setEndDate(resultSet.getDate("end_date"));
            vendorCredentials.setProdInstId(resultSet.getString("prod_inst_id"));
            vendorCredentials.setVendorAccountId1(resultSet.getString("vendor_account_id1"));
            vendorCredentials.setVendorAccountId2(resultSet.getString("vendor_account_id2"));
            vendorCredentials.setVendorAccountName(resultSet.getString("vendor_account_name"));
            vendorCredentials.setVendorAccountPassword(resultSet.getString("vendor_account_password"));
            vendorCredentials.setVendorAccountUserName(resultSet.getString("vendor_account_username"));
            vendorCredentials.setVendorId(resultSet.getInt("vendor_id"));
            vendorCredentials.setVendorTimeZoneId(getLongValue(resultSet, "vendor_time_zone_id"));
            return vendorCredentials;
        }
    }

    /**
     * Factory class used to create vendor IDs and VendorCredentials from a result set.
     */
    private static class VendorIdAndVendorCredentialsFactory implements Factory<Pair<Integer, VendorCredentials>> {
        public static final VendorIdAndVendorCredentialsFactory INSTANCE = new VendorIdAndVendorCredentialsFactory();

        /**
         * Constructor.
         */
        private VendorIdAndVendorCredentialsFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public Pair<Integer, VendorCredentials> newInstance(ResultSet resultSet) throws SQLException {
            VendorCredentials credentials = VendorCredentialsFactory.INSTANCE.newInstance(resultSet);
            return Pair.from(Integer.valueOf(credentials.getVendorId()), credentials);
        }
    }
}
