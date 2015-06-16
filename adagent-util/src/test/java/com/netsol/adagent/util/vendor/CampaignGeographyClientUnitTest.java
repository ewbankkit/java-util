/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.vendor.beans.BaseLocation;
import com.netsol.vendor.client.CampaignGeographyClient;
import com.netsol.vendor.client.ClientFactories;

public class CampaignGeographyClientUnitTest extends BaseClientUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:38 CampaignGeographyClientUnitTest.java NSI";

    @BeforeClass
    public static void setup() throws SQLException {
        setupBuiltInClient();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        teardownBuiltInClient();
    }

    @Test
    public void getLocalGeosByZipCodeTest1() throws Exception {
        CampaignGeographyClient client = ClientFactories.getClientFactory().getCampaignGeographyClient(getCredentials());
        BaseLocation[] locations = client.getLocalGeosByZipCode("98103");
        assertNotNull(locations);
        assertFalse(locations.length == 0);
    }
}
