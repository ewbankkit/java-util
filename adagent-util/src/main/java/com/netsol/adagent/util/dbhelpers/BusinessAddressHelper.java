/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.BusinessAddress;
import com.netsol.adagent.util.log.BaseLoggable;

public class BusinessAddressHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:42 BusinessAddressHelper.java NSI";

    /**
     * Constructor.
     */
    public BusinessAddressHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public BusinessAddressHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public BusinessAddressHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public BusinessAddressHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Return any business address for the specified product instance ID.
     */
    public BusinessAddress getBusinessAddress(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  business_name," +
            "  street_addr_1," +
            "  street_addr_2," +
            "  city," +
            "  state," +
            "  zip," +
            "  country_code," +
            "  phone," +
            "  fax," +
            "  email " +
            "FROM" +
            "  business_address " +
            "WHERE" +
            "  prod_inst_id = ?;";

        return singleValueFromProdInstId(logTag, connection, prodInstId, SQL, BusinessAddressFactory.INSTANCE);
    }

    /**
     * Return the ZIP code for the specified product instance ID.
     */
    public String getZipCode(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  zip " +
            "FROM" +
            "  business_address " +
            "WHERE" +
            "  prod_inst_id = ?;";

        return singleValueFromProdInstId(logTag, connection, prodInstId, SQL, new StringFactory("zip") {});
    }

    /**
     * Insert a business address record. Update the record if it already exists.
     */
    public void insertOrUpdateBusinessAddress(String logTag, Connection connection, BusinessAddress businessAddress) throws SQLException {
        final String SQL =
            "INSERT INTO business_address" +
            "  (prod_inst_id, business_name, street_addr_1," +
            "   street_addr_2, city, state, zip," +
            "   country_code, phone, fax, email," +
            "   created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   ?, ?, ?, ?," +
            "   NOW(), NOW(), ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  business_name = VALUES(business_name)," +
            "  street_addr_1 = VALUES(street_addr_1)," +
            "  street_addr_2 = VALUES(street_addr_2)," +
            "  city = VALUES(city)," +
            "  state = VALUES(state)," +
            "  zip = VALUES(zip)," +
            "  country_code = VALUES(country_code)," +
            "  phone = VALUES(phone)," +
            "  fax = VALUES(fax)," +
            "  email = VALUES(email)," +
            "  updated_date = NOW()," +
            "  updated_by_user = VALUES(updated_by_user)," +
            "  updated_by_system = VALUES(updated_by_system);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, businessAddress.getProdInstId());
            statement.setString(parameterIndex++, businessAddress.getBusinessName());
            statement.setString(parameterIndex++, businessAddress.getStreetAddress1());
            statement.setString(parameterIndex++, businessAddress.getStreetAddress2());
            statement.setString(parameterIndex++, businessAddress.getCity());
            statement.setString(parameterIndex++, businessAddress.getState());
            statement.setString(parameterIndex++, businessAddress.getZip());
            statement.setString(parameterIndex++, businessAddress.getCountryCode());
            statement.setString(parameterIndex++, businessAddress.getPhone());
            statement.setString(parameterIndex++, businessAddress.getFax());
            statement.setString(parameterIndex++, businessAddress.getEmailAddress());
            statement.setString(parameterIndex++, businessAddress.getUpdatedByUser());
            statement.setString(parameterIndex++, businessAddress.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create BusinessAddress objects from a result set.
     */
    private static class BusinessAddressFactory implements Factory<BusinessAddress> {
        public static final BusinessAddressFactory INSTANCE = new BusinessAddressFactory();

        /**
         * Constructor.
         */
        private BusinessAddressFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public BusinessAddress newInstance(ResultSet resultSet) throws SQLException {
            BusinessAddress businessAddress = new BusinessAddress();
            businessAddress.setBusinessName(resultSet.getString("business_name"));
            businessAddress.setCity(resultSet.getString("city"));
            businessAddress.setCountryCode(resultSet.getString("country_code"));
            businessAddress.setEmailAddress(resultSet.getString("email"));
            businessAddress.setFax(resultSet.getString("fax"));
            businessAddress.setPhone(resultSet.getString("phone"));
            businessAddress.setProdInstId(resultSet.getString("prod_inst_id"));
            businessAddress.setState(resultSet.getString("state"));
            businessAddress.setStreetAddress1(resultSet.getString("street_addr_1"));
            businessAddress.setStreetAddress2(resultSet.getString("street_addr_2"));
            businessAddress.setZip(resultSet.getString("zip"));
            return businessAddress;
        }
    }
}
