/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.netsol.adagent.util.beans.Customer;

public class CustomerHelper extends BaseHelper {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:43 CustomerHelper.java NSI";

    /**
     * Constructor.
     */
    public CustomerHelper(String logComponent) {
        super(logComponent);
    }

    public Customer getCustomerByAccountIdAndPersonOrgId(String logTag, Connection connection, long accountId, long personOrgId) throws SQLException {
        final String SQL =
            CustomerFactory.SQL_SELECT_EXPRESSION +
            "WHERE person_org_id = ? AND account_id = ?;";

        return singleValueFromParameters(logTag, connection, SQL, CustomerFactory.INSTANCE, personOrgId, accountId);
    }

    public Customer getCustomerByFulfillmentId(String logTag, Connection connection, long fulfillmentId) throws SQLException {
        final String SQL =
            CustomerFactory.SQL_SELECT_EXPRESSION +
            "WHERE fulfillment_id = ?;";

        return singleValueFromSingleParameter(logTag, connection, fulfillmentId, SQL, CustomerFactory.INSTANCE);
    }

    public void insertOrUpdateCustomer(String logTag, Connection connection, Customer customer) throws SQLException {
        final String SQL =
            "INSERT INTO customer" +
            "  (person_org_id, account_id," +
            "   contact_name, contact_phone, contact_email, business_name," +
            "   street_addr_1, street_addr_2, city," +
            "   state, zip, country_code," +
            "   fulfillment_id, crm_id," +
            "   created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?," +
            "   SUBSTRING(?, 1, 100), SUBSTRING(?, 1, 45), SUBSTRING(?, 1, 100), SUBSTRING(?, 1, 100)," +
            "   SUBSTRING(?, 1, 80), SUBSTRING(?, 1, 80), SUBSTRING(?, 1, 80)," +
            "   SUBSTRING(?, 1, 80), SUBSTRING(?, 1, 20), SUBSTRING(?, 1, 2)," +
            "   ?, ?," +
            "   NOW(), NOW(), ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  customer_id = LAST_INSERT_ID(customer_id)," + // Force customer_id to be returned on update
            "  person_org_id = VALUES(person_org_id)," +
            "  account_id = VALUES(account_id)," +
            "  contact_name = VALUES(contact_name)," +
            "  contact_phone = VALUES(contact_phone)," +
            "  contact_email = VALUES(contact_email)," +
            "  business_name = VALUES(business_name)," +
            "  street_addr_1 = VALUES(street_addr_1)," +
            "  street_addr_2 = VALUES(street_addr_2)," +
            "  city = VALUES(city)," +
            "  state = VALUES(state)," +
            "  zip = VALUES(zip)," +
            "  country_code = VALUES(country_code)," +
            "  fulfillment_id = VALUES(fulfillment_id)," +
            "  crm_id = VALUES(crm_id)," +
            "  updated_date = NOW()," +
            "  updated_by_user = VALUES(updated_by_user)," +
            "  updated_by_system = VALUES(updated_by_system);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setObject(parameterIndex++, customer.getPersonOrgId());
            statement.setObject(parameterIndex++, customer.getAccountId());
            statement.setString(parameterIndex++, customer.getContactName());
            statement.setString(parameterIndex++, customer.getContactPhone());
            statement.setString(parameterIndex++, customer.getContactEmail());
            statement.setString(parameterIndex++, customer.getBusinessName());
            statement.setString(parameterIndex++, customer.getStreetAddress1());
            statement.setString(parameterIndex++, customer.getStreetAddress2());
            statement.setString(parameterIndex++, customer.getCity());
            statement.setString(parameterIndex++, customer.getState());
            statement.setString(parameterIndex++, customer.getZip());
            statement.setString(parameterIndex++, customer.getCountryCode());
            statement.setObject(parameterIndex++, customer.getFulfillmentId());
            statement.setString(parameterIndex++, customer.getCrmId());
            statement.setString(parameterIndex++, customer.getUpdatedByUser());
            statement.setString(parameterIndex++, customer.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long customerId = getAutoIncrementId(statement);
            if (customerId != null) {
                customer.setCustomerId(customerId.longValue());
            }
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create Target objects from a result set.
     */
    private static class CustomerFactory implements Factory<Customer> {
        public static final CustomerFactory INSTANCE = new CustomerFactory();

        public static final String SQL_SELECT_EXPRESSION =
            "SELECT" +
            "  customer_id," +
            "  person_org_id," +
            "  account_id," +
            "  contact_name," +
            "  contact_phone," +
            "  contact_email," +
            "  business_name," +
            "  street_addr_1," +
            "  street_addr_2," +
            "  city," +
            "  state," +
            "  zip," +
            "  country_code," +
            "  fulfillment_id," +
            "  crm_id " +
            "FROM" +
            "  customer ";

        /**
         * Constructor.
         */
        private CustomerFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public Customer newInstance(ResultSet resultSet) throws SQLException {
            Customer customer= new Customer();
            customer.setAccountId(getLongValue(resultSet, "account_id"));
            customer.setBusinessName(resultSet.getString("business_name"));
            customer.setCity(resultSet.getString("city"));
            customer.setContactEmail(resultSet.getString("contact_email"));
            customer.setContactName(resultSet.getString("contact_name"));
            customer.setContactPhone(resultSet.getString("contact_phone"));
            customer.setCountryCode(resultSet.getString("country_code"));
            customer.setCrmId(resultSet.getString("crm_id"));
            customer.setCustomerId(resultSet.getLong("customer_id"));
            customer.setFulfillmentId(getLongValue(resultSet, "fulfillment_id"));
            customer.setPersonOrgId(getLongValue(resultSet, "person_org_id"));
            customer.setState(resultSet.getString("state"));
            customer.setStreetAddress1(resultSet.getString("street_addr_1"));
            customer.setStreetAddress2(resultSet.getString("street_addr_2"));
            customer.setZip(resultSet.getString("zip"));
            return customer;
        }
    }
}
