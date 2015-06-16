/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.vendor;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.vendor.beans.AccountBean;
import com.netsol.vendor.client.AccountClient;
import com.netsol.vendor.client.ClientFactories;

public class AccountClientUnitTest extends BaseClientUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:37 AccountClientUnitTest.java NSI";

    private final static String prodInstId = "WN.DEV.BING.0003";

    @BeforeClass
    public static void setup() throws SQLException {
        setupBuiltInClient();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        teardownBuiltInClient();
    }

    @Test
    public void getAccountDetailTest1() throws Exception {
        AccountClient client = ClientFactories.getClientFactory().getAccountClient(getCredentials());
        AccountBean account = client.getAccountDetail(prodInstId);
        assertNotNull(account);
    }
}
