/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DB helpers for product vendor account cross-references
 */
public class ProductVendorAccountHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:54 ProductVendorAccountHelper.java NSI";

    public ProductVendorAccountHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * return the single product instance ID corresponding the specified vendor account ID.
     */
    public String getSingleProdInstId(String logTag, Connection connection, int vendorAccountId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id " +
            "FROM" +
            "  product_vendor_account_xref " +
            "WHERE" +
            "  vendor_account_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setInt(1, vendorAccountId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return singleValue(resultSet, ProdInstIdFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert a product vendor account cross-reference.
     */
    public void insertProductVendorAccountXRef(String logTag, Connection connection, String prodInstId, int vendorAccountId) throws SQLException {
        final String SQL =
            "INSERT IGNORE INTO" +
            "  product_vendor_account_xref (prod_inst_id, vendor_account_id) " +
            "VALUES" +
            "  (?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setInt(2, vendorAccountId);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }
}
