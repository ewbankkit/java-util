/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.Customer;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.CustomerHelper;

public class CustomerHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:46 CustomerHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getCustomerTest1() throws SQLException {
        CustomerHelper helper = new CustomerHelper("");
        assertNull(helper.getCustomerByAccountIdAndPersonOrgId(logTag, connection, 0L, 0L));
    }

    @Test
    public void getCustomerTest2() throws SQLException {
        CustomerHelper helper = new CustomerHelper("");
        assertNotNull(helper.getCustomerByAccountIdAndPersonOrgId(logTag, connection, 30080719L, 51226641L));
    }

    @Test
    public void insertCustomerTest1() throws SQLException {
        CustomerHelper helper = new CustomerHelper("");
        Customer customer = new Customer();
        customer.setAccountId(2L);
        customer.setBusinessName("BUSINESS NAME");
        customer.setCity("CITY");
        customer.setContactName("A VERY LONG CONTACT NAME WHICH IS LONGER THAN THE ALLOWED LIMIT OF 100 CHARACTERS ***************************");
        customer.setPersonOrgId(2L);
        customer.setState("STATE");
        customer.setStreetAddress1("STREET ADDRESS 1");
        customer.setStreetAddress2("STREET ADDRESS 2");
        customer.setZip("ZIP");
        helper.insertOrUpdateCustomer(logTag, connection, customer);
        long customerId1 = customer.getCustomerId();
        assertFalse(0L == customerId1);

        customer = new Customer();
        customer.setAccountId(2L);
        customer.setBusinessName("A NEW BUSINESS NAME");
        customer.setCity("CITY NUMBER 2");
        customer.setContactName("ANOTHER CONTACT NAME");
        customer.setPersonOrgId(2L);
        customer.setState("STATE");
        customer.setStreetAddress1("STREET ADDRESS 1");
        customer.setStreetAddress2("STREET ADDRESS 2");
        customer.setZip("ZZZZZZZZZZIIIIIIIIIIPPPPPPPPPP");
        helper.insertOrUpdateCustomer(logTag, connection, customer);
        long customerId2 = customer.getCustomerId();
        assertFalse(0L == customerId2);
        assertTrue(customerId1 == customerId2);
    }
}
