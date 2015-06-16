/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.netsol.adagent.util.codes.VendorStatus;
import com.netsol.adagent.util.codes.VendorType;

/**
 * DB helpers for vendor services.
 */
public class VendorServiceHelper extends EntityHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:57 VendorServiceHelper.java NSI";

    /**
     * Constructor.
     */
    public VendorServiceHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public VendorServiceHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Return the vendor ID corresponding to the specified vendor name.
     */
    public Integer getVendorId(String logTag, Connection connection, String vendorName) throws SQLException {
        final String SQL =
            "SELECT" +
            "  vendor_id " +
            "FROM" +
            "  vendor_service " +
            "WHERE" +
            "  name = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, vendorName);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, VendorIdFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return active vendor IDs for the specified vendor type.
     */
    public List<Integer> getVendorIds(String logTag, Connection connection, int vendorType) throws SQLException {
        // Vendor type is a bit mask.
        final String SQL =
            "SELECT" +
            "  vendor_id " +
            "FROM" +
            "  vendor_service " +
            "WHERE" +
            "  (vendor_type & ?) = ? AND" +
            "  status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorType);
            statement.setInt(2, vendorType);
            statement.setString(3, VendorStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, VendorIdFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Is the specified vendor an ad vendor?
     */
    public boolean isAdVendor(String logTag, Connection connection, int vendorId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  COUNT(*) " +
            "FROM" +
            "  vendor_service " +
            "WHERE" +
            "  vendor_id = ? AND" +
            "  (vendor_type & ?) = ? AND" +
            "  status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorId);
            statement.setInt(2, VendorType.AD);
            statement.setInt(3, VendorType.AD);
            statement.setString(4, VendorStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return countGreaterThanZero(resultSet);
        }
        finally {
            close(statement, resultSet);
        }
    }
}
