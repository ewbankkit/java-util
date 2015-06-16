/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.LocalHost;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DB helpers for product life cycle event.
 */
public class ProductLifeCycleEventHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:16 ProductLifeCycleEventHelper.java NSI";

    /**
     * Constructor.
     */
    public ProductLifeCycleEventHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public ProductLifeCycleEventHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public ProductLifeCycleEventHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Return the latest budget renewal event for the specified product or NULL if there is none.
     */
    public BudgetRenewalEvent getLatestBudgetRenewalEvent(String logTag, Connection connection, String prodInstId) throws SQLException {
        return ProductLifeCycleEventFactory.createBudgetRenewalEvent(
                    getLatestEvent(logTag, connection, prodInstId, ProductLifeCycleEventType.BUDGET_RENEWAL));
    }

    /**
     * Return the latest provisioning renewal event for the specified product or NULL if there is none.
     */
    public ProvisioningRenewalEvent getLatestProvisioningRenewalEvent(String logTag, Connection connection, String prodInstId) throws SQLException {
        return ProductLifeCycleEventFactory.createProvisioningRenewalEvent(
                    getLatestEvent(logTag, connection, prodInstId, ProductLifeCycleEventType.PROVISIONING_RENEWAL));
    }

    /**
     * Insert a product life cycle event.
     * Return the product life cycle event ID.
     */
    public Long insertProductLifeCycleEvent(String logTag, Connection connection, ProductLifeCycleEvent productLifeCycleEvent) {
        try {
            return insertProductLifeCycleEvent(logTag, connection, productLifeCycleEvent.productLifeCycleEventData);
        }
        catch (SQLException ex) {
            logSqlException(logTag, ex);
        }

        return null;
    }

    /**
     * Return the latest event of the specified type for the specified product or NULL if there is none.
     */
    private ProductLifeCycleEventData getLatestEvent(String logTag, Connection connection, String prodInstId, String eventType) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  event_type," +
            "  generic_boolean1," +
            "  generic_boolean2," +
            "  generic_boolean3," +
            "  generic_boolean4," +
            "  generic_date1," +
            "  generic_date2," +
            "  generic_date3," +
            "  generic_date4," +
            "  generic_decimal1," +
            "  generic_decimal2," +
            "  generic_decimal3," +
            "  generic_decimal4," +
            "  generic_number1," +
            "  generic_number2," +
            "  generic_number3," +
            "  generic_number4," +
            "  generic_vchar1," +
            "  generic_vchar2," +
            "  generic_vchar3," +
            "  generic_vchar4," +
            "  generic_vchar5," +
            "  generic_vchar6," +
            "  generic_vchar7," +
            "  generic_vchar8," +
            "  generic_vchar9," +
            "  generic_vchar10," +
            "  created_by_user," +
            "  created_date " +
            "FROM" +
            "  product_lifecycle_event " +
            "WHERE" +
            "  prod_inst_id = ? AND" +
            "  event_type = ? " +
            "ORDER BY" +
            "  created_date DESC," +
            "  product_lifecycle_event_id DESC "+
            "LIMIT 1;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, eventType);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, ProductLifeCycleEventDataFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert a product life cycle event.
     * Return the product life cycle event ID.
     */
    private Long insertProductLifeCycleEvent(String logTag, Connection connection, ProductLifeCycleEventData productLifeCycleEventData) throws SQLException {
        final String SQL =
            "INSERT INTO product_lifecycle_event" +
            "  (prod_inst_id, event_type," +
            "   generic_boolean1, generic_boolean2, generic_boolean3, generic_boolean4," +
            "   generic_date1, generic_date2, generic_date3, generic_date4," +
            "   generic_decimal1, generic_decimal2, generic_decimal3, generic_decimal4," +
            "   generic_number1, generic_number2, generic_number3, generic_number4," +
            "   generic_vchar1, generic_vchar2, generic_vchar3, generic_vchar4," +
            "   generic_vchar5, generic_vchar6, generic_vchar7, generic_vchar8," +
            "   generic_vchar9, generic_vchar10," +
            "   created_by_user) " +
            "VALUES" +
            "  (?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?," +
            "   ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, productLifeCycleEventData.getProdInstId());
            statement.setString(2, productLifeCycleEventData.getEventType());
            statement.setObject(3, productLifeCycleEventData.getGenericBoolean1());
            statement.setObject(4, productLifeCycleEventData.getGenericBoolean2());
            statement.setObject(5, productLifeCycleEventData.getGenericBoolean3());
            statement.setObject(6, productLifeCycleEventData.getGenericBoolean4());
            statement.setDate(7, productLifeCycleEventData.getGenericDate1());
            statement.setDate(8, productLifeCycleEventData.getGenericDate2());
            statement.setDate(9, productLifeCycleEventData.getGenericDate3());
            statement.setDate(10, productLifeCycleEventData.getGenericDate4());
            statement.setBigDecimal(11, productLifeCycleEventData.getGenericDecimal1());
            statement.setBigDecimal(12, productLifeCycleEventData.getGenericDecimal2());
            statement.setBigDecimal(13, productLifeCycleEventData.getGenericDecimal3());
            statement.setBigDecimal(14, productLifeCycleEventData.getGenericDecimal4());
            statement.setObject(15, productLifeCycleEventData.getGenericNumber1());
            statement.setObject(16, productLifeCycleEventData.getGenericNumber2());
            statement.setObject(17, productLifeCycleEventData.getGenericNumber3());
            statement.setObject(18, productLifeCycleEventData.getGenericNumber4());
            statement.setString(19, productLifeCycleEventData.getGenericString1());
            statement.setString(20, productLifeCycleEventData.getGenericString2());
            statement.setString(21, productLifeCycleEventData.getGenericString3());
            statement.setString(22, productLifeCycleEventData.getGenericString4());
            statement.setString(23, productLifeCycleEventData.getGenericString5());
            statement.setString(24, productLifeCycleEventData.getGenericString6());
            statement.setString(25, productLifeCycleEventData.getGenericString7());
            statement.setString(26, productLifeCycleEventData.getGenericString8());
            statement.setString(27, productLifeCycleEventData.getGenericString9());
            statement.setString(28, productLifeCycleEventData.getGenericString10());
            statement.setString(29, LocalHost.NAME);
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            return getAutoIncrementId(statement);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create InterceptorFeatures objects from a result set.
     */
    private static class ProductLifeCycleEventDataFactory implements Factory<ProductLifeCycleEventData> {
        public static final ProductLifeCycleEventDataFactory INSTANCE = new ProductLifeCycleEventDataFactory();

        /**
         * Constructor.
         */
        private ProductLifeCycleEventDataFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public ProductLifeCycleEventData newInstance(ResultSet resultSet) throws SQLException {
            ProductLifeCycleEventData productLifeCycleEventData = new ProductLifeCycleEventData();
            productLifeCycleEventData.setEventDate(resultSet.getTimestamp("created_date"));
            productLifeCycleEventData.setEventType(resultSet.getString("event_type"));
            productLifeCycleEventData.setGenericBoolean1(getBooleanValue(resultSet, "generic_boolean1"));
            productLifeCycleEventData.setGenericBoolean2(getBooleanValue(resultSet, "generic_boolean2"));
            productLifeCycleEventData.setGenericBoolean3(getBooleanValue(resultSet, "generic_boolean3"));
            productLifeCycleEventData.setGenericBoolean4(getBooleanValue(resultSet, "generic_boolean4"));
            productLifeCycleEventData.setGenericDate1(resultSet.getDate("generic_date1"));
            productLifeCycleEventData.setGenericDate2(resultSet.getDate("generic_date2"));
            productLifeCycleEventData.setGenericDate3(resultSet.getDate("generic_date3"));
            productLifeCycleEventData.setGenericDate4(resultSet.getDate("generic_date4"));
            productLifeCycleEventData.setGenericDecimal1(resultSet.getBigDecimal("generic_decimal1"));
            productLifeCycleEventData.setGenericDecimal2(resultSet.getBigDecimal("generic_decimal2"));
            productLifeCycleEventData.setGenericDecimal3(resultSet.getBigDecimal("generic_decimal3"));
            productLifeCycleEventData.setGenericDecimal4(resultSet.getBigDecimal("generic_decimal4"));
            productLifeCycleEventData.setGenericNumber1(getLongValue(resultSet, "generic_number1"));
            productLifeCycleEventData.setGenericNumber2(getLongValue(resultSet, "generic_number2"));
            productLifeCycleEventData.setGenericNumber3(getLongValue(resultSet, "generic_number3"));
            productLifeCycleEventData.setGenericNumber4(getLongValue(resultSet, "generic_number4"));
            productLifeCycleEventData.setGenericString1(resultSet.getString("generic_vchar1"));
            productLifeCycleEventData.setGenericString2(resultSet.getString("generic_vchar2"));
            productLifeCycleEventData.setGenericString3(resultSet.getString("generic_vchar3"));
            productLifeCycleEventData.setGenericString4(resultSet.getString("generic_vchar4"));
            productLifeCycleEventData.setGenericString5(resultSet.getString("generic_vchar5"));
            productLifeCycleEventData.setGenericString6(resultSet.getString("generic_vchar6"));
            productLifeCycleEventData.setGenericString7(resultSet.getString("generic_vchar7"));
            productLifeCycleEventData.setGenericString8(resultSet.getString("generic_vchar8"));
            productLifeCycleEventData.setGenericString9(resultSet.getString("generic_vchar9"));
            productLifeCycleEventData.setGenericString10(resultSet.getString("generic_vchar10"));
            productLifeCycleEventData.setProdInstId(resultSet.getString("prod_inst_id"));
            return productLifeCycleEventData;
        }
    }
}
