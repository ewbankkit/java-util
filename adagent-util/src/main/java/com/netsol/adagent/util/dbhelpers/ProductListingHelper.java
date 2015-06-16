/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.ProductListing;
import com.netsol.adagent.util.log.BaseLoggable;

public class ProductListingHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:53 ProductListingHelper.java NSI";

    /**
     * Constructor.
     */
    public ProductListingHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public ProductListingHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public ProductListingHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public ProductListingHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Delete any product listing for the specified product instance ID.
     */
    public void deleteProductListing(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM product_listing " +
            "WHERE  prod_inst_id = ?;";

        deleteForProdInstId(logTag, connection, prodInstId, SQL);
    }
    
    /**
     * Return any product listing for the specified CRM ID.
     */
    public ProductListing getProductListingByCrmId(String logTag, Connection connection, String crmId) throws SQLException {
        final String SQL =
            ProductListingFactory.SQL_SELECT_EXPRESSION +
            "WHERE  crm_id = ?;";

        return singleValueFromSingleParameter(logTag, connection, crmId, SQL, ProductListingFactory.INSTANCE);
    }

    /**
     * Return any product listing for the specified fulfillment ID.
     */
    public ProductListing getProductListingByFulfillmentId(String logTag, Connection connection, int fulfillmentId) throws SQLException {
        final String SQL =
            ProductListingFactory.SQL_SELECT_EXPRESSION +
            "WHERE  fulfillment_id = ?;";

        return singleValueFromSingleParameter(logTag, connection, Integer.valueOf(fulfillmentId), SQL, ProductListingFactory.INSTANCE);
    }

    /**
     * Return any product listing for the specified product listing ID.
     */
    public ProductListing getProductListingByProductListingId(String logTag, Connection connection, long productListingId) throws SQLException {
        final String SQL =
            ProductListingFactory.SQL_SELECT_EXPRESSION +
            "WHERE  product_listing_id = ?;";

        return singleValueFromSingleParameter(logTag, connection, Long.valueOf(productListingId), SQL, ProductListingFactory.INSTANCE);
    }

    /**
     * Return any product listing for the specified product instance ID.
     */
    public ProductListing getProductListingByProdInstId(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            ProductListingFactory.SQL_SELECT_EXPRESSION +
            "WHERE  prod_inst_id = ?;";

        return singleValueFromProdInstId(logTag, connection, prodInstId, SQL, ProductListingFactory.INSTANCE);
    }

    /**
     * Insert or update a product listing.
     */
    public void insertOrUpdateProductListing(String logTag, Connection connection, ProductListing productListing) throws SQLException {
        final String SQL =
            "INSERT INTO product_listing" +
            "  (prod_inst_id, title, description, notes, fulfillment_id, crm_id, created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  product_listing_id = LAST_INSERT_ID(product_listing_id)," + // Force product_listing_id to be returned on update
            "  prod_inst_id = VALUES(prod_inst_id)," +
            "  title = VALUES(title)," +
            "  description = VALUES(description)," +
            "  notes = VALUES(notes)," +
            "  fulfillment_id = VALUES(fulfillment_id)," +
            "  crm_id = VALUES(crm_id)," +
            "  updated_date = NOW()," +
            "  updated_by_user = VALUES(updated_by_user)," +
            "  updated_by_system = VALUES(updated_by_system);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, productListing.getProdInstId());
            statement.setString(parameterIndex++, productListing.getTitle());
            statement.setString(parameterIndex++, productListing.getDescription());
            statement.setString(parameterIndex++, productListing.getNotes());
            statement.setLong(parameterIndex++, productListing.getFulfillmentId());
            statement.setString(parameterIndex++, productListing.getCrmId());
            statement.setString(parameterIndex++, productListing.getUpdatedByUser());
            statement.setString(parameterIndex++, productListing.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long productListingId = getAutoIncrementId(statement);
            if (productListingId != null) {
                productListing.setProductListingId(productListingId.longValue());
            }
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create ProductListing objects from a result set.
     */
    private static class ProductListingFactory implements Factory<ProductListing> {
        public static final ProductListingFactory INSTANCE = new ProductListingFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT product_listing_id," +
            "       prod_inst_id," +
            "       title," +
            "       description," +
            "       notes," +
            "       fulfillment_id," +
            "       crm_id " +
            "FROM   product_listing ";

        /**
         * Constructor.
         */
        private ProductListingFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public ProductListing newInstance(ResultSet resultSet) throws SQLException {
            ProductListing productListing= new ProductListing();
            productListing.setCrmId(resultSet.getString("crm_id"));
            productListing.setDescription(resultSet.getString("description"));
            productListing.setFulfillmentId(resultSet.getLong("fulfillment_id"));
            productListing.setNotes(resultSet.getString("notes"));
            productListing.setProdInstId(resultSet.getString("prod_inst_id"));
            productListing.setProductListingId(resultSet.getLong("product_listing_id"));
            productListing.setTitle(resultSet.getString("title"));
            return productListing;
        }
    }
}
