/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.vendor;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.vendor.client.CampaignClient;
import com.netsol.vendor.client.ClientFactories;

public class CampaignClientUnitTest extends BaseClientUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:38 CampaignClientUnitTest.java NSI";

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
    public void syncCampaignsTest1() throws Exception {
        CampaignClient client = ClientFactories.getClientFactory().getCampaignClient(getCredentials());
        client.syncCampaigns(prodInstId);
    }
}
